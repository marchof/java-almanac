package io.javaalmanac.data.v1;

import static io.javaalmanac.data.OpenApi3Support.newComponentSchemaRef;

import org.openapi4j.parser.model.v3.Schema;

import io.javaalmanac.data.NamedSchema;

public enum SchemaV1 implements NamedSchema {

	vendor(new Schema() //
			.setType("object") //
			.setProperty("name", new Schema() //
					.setType("string")) //
			.setProperty("url", new Schema() //
					.setType("string")) //
			.setProperty("products", new Schema() //
					.setType("array") //
					.setMinItems(1) //
					.setItemsSchema(new Schema() //
							.setType("object") //
							.setProperty("id", new Schema() //
									.setType("string")) //
							.setProperty("name", new Schema() //
									.setType("string")) //
							.setProperty("license", new Schema() //
									.setType("string")) //
							.setProperty("url", new Schema() //
									.setType("string")) //
							.setProperty("foojaydownload", new Schema() //
									.setType("boolean")) //
							.setProperty("platforms", new Schema() //
									.setType("array") //
									.setMinItems(1) //
									.setItemsSchema(new Schema() //
											.setType("string"))) //
							.setProperty("versions", new Schema() //
									.setType("array") //
									.setMinItems(1) //
									.setItemsSchema(new Schema() //
											.setType("string"))) //
							.addRequiredField("id") //
							.addRequiredField("name") //
							.addRequiredField("license") //
							.addRequiredField("url") //
							.addRequiredField("foojaydownload") //
							.addRequiredField("platforms") //
							.addRequiredField("versions"))) //
			.addRequiredField("name") //
			.addRequiredField("url") //
			.addRequiredField("products")),

	apidelta(new Schema() //
			.setType("object") //
			.setProperty("type", new Schema() //
					.setType("string") //
					.addEnum("module") //
					.addEnum("package") //
					.addEnum("class") //
					.addEnum("interface") //
					.addEnum("enum") //
					.addEnum("field") //
					.addEnum("method") //
					.addEnum("annotation")) //
			.setProperty("name", new Schema() //
					.setType("string")) //
			.setProperty("status", new Schema() //
					.setType("string") //
					.addEnum("notmodified") //
					.addEnum("modified") //
					.addEnum("added") //
					.addEnum("removed")) //
			.setProperty("preview", new Schema() //
					.setType("boolean")) //
			.setProperty("javadoc", new Schema() //
					.setType("string")) //
			.setProperty("addedTags", new Schema() //
					.setType("array") //
					.setItemsSchema(new Schema() //
							.setType("string"))) //
			.setProperty("removedTags", new Schema() //
					.setType("array") //
					.setItemsSchema(new Schema() //
							.setType("string"))) //
			.setProperty("deltas", new Schema() //
					.setType("array") //
					.setItemsSchema(newComponentSchemaRef("apidelta"))) //
			.addRequiredField("type") //
			.addRequiredField("name") //
			.addRequiredField("status")),

	implementationversion(new Schema() //
			.setType("object") //
			.setProperty("feature", new Schema() //
					.setType("string")) //
			.setProperty("version", new Schema() //
					.setType("string")) //
			.setProperty("vendor", new Schema() //
					.setType("string")) //
			.addRequiredField("feature") //
			.addRequiredField("version") //
			.addRequiredField("vendor"));

	private Schema schema;

	private SchemaV1(Schema schema) {
		this.schema = schema;
	}

	public Schema schema() {
		return schema;
	}

}
