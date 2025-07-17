package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;

import org.openapi4j.parser.model.v3.Schema;

import com.fasterxml.jackson.databind.JsonNode;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetBytecodeOpcodesList extends GetOperationDefinition {

	public static final JsonProcessor BYTECODE_OPCODES = //
			root().select("$.bytecode.opcodes.keys()").sort(JsonNode::asText);

	public GetBytecodeOpcodesList() {
		super("List of bytecode opcodes");
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
		return BYTECODE_OPCODES;
	}

}
