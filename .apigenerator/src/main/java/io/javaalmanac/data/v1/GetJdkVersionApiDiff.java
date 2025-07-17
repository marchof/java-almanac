package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;
import static io.javaalmanac.data.OpenApi3Support.newComponentSchemaRef;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetJdkVersionApiDiff extends GetOperationDefinition {

	private static final JsonProcessor JDK_DIFF = root()
			.select("$.jdk.versions.[`version`].apidiff.[`baseversion`].['base', 'target', 'deltas']");

	public GetJdkVersionApiDiff() {
		super("API diff between two JDK versions");
	}

	@Override
	public Schema getSchema() {
		return new Schema() //
				.setType("object") //
				.setProperty("base", newComponentSchemaRef(SchemaV1.implementationversion)) //
				.setProperty("target", newComponentSchemaRef(SchemaV1.implementationversion)) //
				.setProperty("deltas", new Schema() //
						.setType("array") //
						.setItemsSchema(newComponentSchemaRef(SchemaV1.apidelta)))
				.addRequiredField("base") //
				.addRequiredField("target");
	}

	@Override
	public JsonProcessor getContentResolver() {
		return JDK_DIFF;
	}

}
