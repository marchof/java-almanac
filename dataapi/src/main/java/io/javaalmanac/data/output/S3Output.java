package io.javaalmanac.data.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.parser.model.SerializationFlag;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Output implements ApiOutput {

	private static final Logger LOG = LoggerFactory.getLogger(S3Output.class);

	private final S3Client s3;
	private final String bucket;

	private CloudFrontClient cf;
	private String distributionid;

	private final Map<String, String> existingContent;
	private final OutputStats stats;

	public S3Output(String bucket, String distributionid) {
		this.s3 = S3Client.builder().region(Region.AWS_GLOBAL).build();
		this.bucket = bucket;
		this.cf = CloudFrontClient.builder().region(Region.AWS_GLOBAL).build();
		this.distributionid = distributionid;
		this.stats = new OutputStats();
		this.existingContent = new HashMap<>();
		listBucketContent();
		writeErrorResponse(404, "Not Found");
	}

	private void listBucketContent() {
		ListObjectsRequest request = ListObjectsRequest.builder() //
				.bucket(bucket) //
				.build();
		ListObjectsResponse listObjects = s3.listObjects(request);
		for (S3Object o : listObjects.contents()) {
			existingContent.put(o.key(), o.eTag());
		}
	}

	private void writeErrorResponse(int code, String message) {
		ObjectNode msg = new ObjectNode(JsonNodeFactory.instance);
		msg.put("code", code);
		msg.put("message", message);
		uploadJson(String.format("/errors/%s.json", code), msg.toString());
	}

	@Override
	public void writeApiDescription(String prefix, OpenApi3 api) throws IOException, EncodeException {
		uploadJson(prefix + "/openapi.json", api.toString(EnumSet.of(SerializationFlag.OUT_AS_JSON)));
		uploadYaml(prefix + "/openapi.yaml", api.toString(EnumSet.of(SerializationFlag.OUT_AS_YAML)));
	}

	@Override
	public void writeGetResponse(String path, JsonNode content) throws IOException {
		uploadJson(path, content.toString());
	}

	@Override
	public void finish() throws IOException {
		LOG.info("Uploaded {}", stats);
		boolean changed = stats.hasFiles();
		if (existingContent.isEmpty()) {
			LOG.info("No obsolete files to delete");
		} else {
			LOG.info("Deleting obsolete files {}", existingContent.keySet());
			var ids = existingContent.keySet().stream().map( //
					key -> ObjectIdentifier.builder().key(key).build() //
			).toList();
			s3.deleteObjects(b -> b //
					.bucket(bucket) //
					.delete(d -> d.objects(ids)));
			changed = true;
		}
		if (changed) {
			LOG.info("Create CloudFront invalidation");
			cf.createInvalidation(i -> i //
					.distributionId(distributionid) //
					.invalidationBatch(b -> b //
							.callerReference(String.valueOf(System.currentTimeMillis())) //
							.paths(p -> p //
									.quantity(1) //
									.items("/*"))));
		}
		s3.close();
		cf.close();
	}

	private void uploadJson(String path, String content) {
		upload(path, "application/json; charset=utf-8", content);
	}

	private void uploadYaml(String path, String content) {
		upload(path, "application/x-yaml", content);
	}

	private void upload(String path, String contentType, String content) {
		String key = path.substring(1);
		MD5 md5 = new MD5(content);
		if (md5.hexInQuotes().equals(existingContent.remove(key))) {
			LOG.debug("No update required for {}", path);
			return;
		}
		LOG.info("Uploading {}", path);
		RequestBody body = RequestBody.fromString(content, StandardCharsets.UTF_8);
		s3.putObject(r -> r //
				.bucket(bucket) //
				.key(key) //
				.contentType(contentType) //
				.contentMD5(md5.base64()), //
				body);
		stats.addFile(body.contentLength());
	}

}
