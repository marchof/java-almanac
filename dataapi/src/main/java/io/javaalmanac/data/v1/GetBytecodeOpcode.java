package io.javaalmanac.data.v1;

import static io.javaalmanac.data.JsonProcessor.root;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.GetOperationDefinition;
import io.javaalmanac.data.JsonProcessor;

public class GetBytecodeOpcode extends GetOperationDefinition {

	private static final JsonProcessor BYTECODE_OPCODE = root().select("$.bytecode.opcodes.[`opcode`]");

	public GetBytecodeOpcode() {
		super("Opcode information");
	}

	@Override
	public Schema getSchema() {
		return new Schema() //
				.setType("object") //
				.setProperty("mnemonic", new Schema() //
						.setType("string")) //
				.setProperty("opcode", new Schema() //
						.setType("integer")) //
				.setProperty("category", new Schema() //
						.setType("string") //
						.addEnum("constant") //
						.addEnum("load") //
						.addEnum("store") //
						.addEnum("stack") //
						.addEnum("math") //
						.addEnum("conversion") //
						.addEnum("comparison") //
						.addEnum("conditional") //
						.addEnum("objects") //
						.addEnum("array") //
						.addEnum("flow") //
						.addEnum("other") //
						.addEnum("reserved")) //
				.setProperty("shortdescr", new Schema() //
						.setType("string")) //
				.setProperty("specref", new Schema() //
						.setType("string"));

	}

	@Override
	public JsonProcessor getContentResolver() {
		return BYTECODE_OPCODE;
	}

}
