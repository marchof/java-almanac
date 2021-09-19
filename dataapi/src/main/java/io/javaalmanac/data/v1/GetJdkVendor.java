package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;
import static io.javaalmanac.data.OpenApi3Support.newComponentSchemaRef;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetJdkVendor extends GetOperationDefinition {

	private static final JsonProcessor JDK_VENDOR = root().select("$.jdk.vendors.[`vendor`]");

	public GetJdkVendor() {
		super("JDK vendor information");
	}

	@Override
	public Schema getSchema() {
		return newComponentSchemaRef(SchemaV1.vendor);
	}

	@Override
	public JsonProcessor getContentResolver() {
		return JDK_VENDOR;
	}

}
