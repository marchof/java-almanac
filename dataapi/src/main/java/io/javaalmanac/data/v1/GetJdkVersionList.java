package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetJdkVersionList extends GetOperationDefinition {

	public static final JsonProcessor JDK_VERSIONS = //
			root().select("$.jdk.versions.keys()").sort(n -> Float.valueOf(n.asText()));

	public GetJdkVersionList() {
		super("List of currently known Java major versions");
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
		return JDK_VERSIONS;
	}

}
