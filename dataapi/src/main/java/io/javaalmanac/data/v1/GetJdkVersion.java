package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;
import static io.javaalmanac.data.OpenApi3Support.newComponentSchemaRef;

import org.openapi4j.parser.model.v3.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;
import io.javaalmanac.data.JsonProcessor.ProcessingContext;

public class GetJdkVersion extends GetOperationDefinition {

	private static final JsonProcessor VENDOR_PROCESSOR = root() //
			.select("$.jdk.vendors.*") //
			.delete("$[*].products[*][?(!(@.versions[*] contains `version`))]") //
			.delete("$[*][?(@.products.length() == 0)]");

	public GetJdkVersion() {
		super("Basic information about a specific Java version");
	}

	@Override
	public Schema getSchema() {
		return new Schema() //
				.setType("object") //
				.setProperty("version", new Schema() //
						.setType("string")) //
				.setProperty("name", new Schema() //
						.setType("string")) //
				.setProperty("ga", new Schema() //
						.setType("string") //
						.setExample("yyyy-mm-dd")) //
				.setProperty("eol", new Schema() //
						.setType("string") //
						.setExample("yyyy-mm-dd")) //
				.setProperty("latestversion", new Schema() //
						.setType("string")) //
				.setProperty("status", new Schema() //
						.setType("string") //
						.addEnum("DEV") //
						.addEnum("REL") //
						.addEnum("LTS") //
						.addEnum("EOL")) //
				.setProperty("bytecode", new Schema() //
						.setType("string")) //
				.setProperty("documentation", new Schema() //
						.setType("object") //
						.setProperty("notes", new Schema() //
								.setType("string")) //
						.setProperty("vm", new Schema() //
								.setType("string")) //
						.setProperty("lang", new Schema() //
								.setType("string")) //
						.setProperty("api", new Schema() //
								.setType("string"))) //
				.setProperty("scm", new Schema() //
						.setType("array") //
						.setItemsSchema(new Schema() //
								.setType("object") //
								.setProperty("type", new Schema() //
										.setType("string") //
										.addEnum("git") //
										.addEnum("hg")) //
								.setProperty("url", new Schema() //
										.setType("string")) //
								.addRequiredField("type") //
								.addRequiredField("url"))) //
				.setProperty("features", new Schema() //
						.setType("array") //
						.setItemsSchema(new Schema() //
								.setType("object") //
								.setProperty("title", new Schema() //
										.setType("string")) //
								.setProperty("category", new Schema() //
										.setType("string") //
										.addEnum("lang") //
										.addEnum("api") //
										.addEnum("jvm") //
										.addEnum("tools") //
										.addEnum("internal")) //
								.setProperty("preview", new Schema() //
										.setType("boolean")) //
								.setProperty("refs", new Schema() //
										.setType("array") //
										.setItemsSchema(new Schema() //
												.setType("object") //
												.setProperty("type", new Schema() //
														.setType("string") //
														.addEnum("AlmanacFeature") //
														.addEnum("JEP") //
														.addEnum("JSR") //
														.addEnum("JDKIssue")) //
												.setProperty("identifier", new Schema() //
														.setType("string")) //
												.setProperty("href", new Schema() //
														.setType("string")) //
												.addRequiredField("type") //
												.addRequiredField("identifier") //
												.addRequiredField("href"))) //
								.addRequiredField("title") //
								.addRequiredField("category") //
								.addRequiredField("refs"))) //
				.setProperty("apidiffversions", new Schema() //
						.setType("array") //
						.setItemsSchema(new Schema() //
								.setType("string"))) //
				.setProperty("vendors", new Schema() //
						.setType("array") //
						.setItemsSchema(newComponentSchemaRef(SchemaV1.vendor))) //
				.addRequiredField("version") //
				.addRequiredField("name") //
				.addRequiredField("status") //
				.addRequiredField("bytecode") //
				.addRequiredField("documentation") //
				.addRequiredField("scm") //
				.addRequiredField("features") //
				.addRequiredField("apidiffversions") //
				.addRequiredField("vendors");
	}

	@Override
	public JsonProcessor getContentResolver() {
		return root() //
				.select("$.jdk.versions.[`version`].['version', 'name', 'ga', 'eol', 'latestversion', 'status', 'bytecode', 'documentation', 'scm', 'features']") //
				.map("$.features.*.refs.*", this::addFeatureRefHref) //
				.set("apidiffversions", GetJdkVersionApiDiffList.BASE_VERSIONS) //
				.set("vendors", VENDOR_PROCESSOR);
	}

	private JsonNode addFeatureRefHref(JsonNode node, ProcessingContext ctx) {
		String href = getFeatureRefHref(node.get("type").asText(), node.get("identifier").asText());
		node.<ObjectNode>require().put("href", href);
		return node;
	}

	private String getFeatureRefHref(String type, String identifier) {
		switch (type) {
		case "JEP":
			return "https://openjdk.java.net/jeps/" + identifier;
		case "JSR":
			return "https://jcp.org/en/jsr/detail?id=" + identifier;
		case "JDKIssue":
			return "https://bugs.openjdk.java.net/browse/JDK-" + identifier;
		case "AlmanacFeature":
			return "https://javaalmanac.io/features/" + identifier + "/";
		default:
			throw new AssertionError(type);
		}
	}

}
