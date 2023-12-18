package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetJdkVersionApiDiffList extends GetOperationDefinition {

	public static final JsonProcessor BASE_VERSIONS = root() //
			.select("$.jdk.versions.[`version`].apidiff.keys()") //
			.sort(n -> Float.valueOf(n.asText()));

	public GetJdkVersionApiDiffList() {
		super("List of all base versions where API diffs are available");
	}

	@Override
	public Schema getSchema() {
		return new Schema() //
				.setType("array") //
				.setItemsSchema(new Schema() //
						.setType("string"));
	}

	@Override
	public JsonProcessor getContentResolver() {
		return BASE_VERSIONS;
	}

}
