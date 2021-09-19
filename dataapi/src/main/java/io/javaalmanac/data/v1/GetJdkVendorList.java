package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;

import org.openapi4j.parser.model.v3.Schema;

import com.fasterxml.jackson.databind.JsonNode;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetJdkVendorList extends GetOperationDefinition {

	public static final JsonProcessor JDK_VENDORS = //
			root().select("$.jdk.vendors.keys()").sort(JsonNode::asText);

	public GetJdkVendorList() {
		super("List of currently known vendors");
	}

	@Override
	public Schema getSchema() {
		return new Schema() //
				.setType("array") //
				.setItemsSchema(new Schema() //
						.setType("string")); //
	}

	@Override
	public JsonProcessor getContentResolver() {
		return JDK_VENDORS;
	}

}
