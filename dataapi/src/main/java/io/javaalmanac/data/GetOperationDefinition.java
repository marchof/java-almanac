package io.javaalmanac.data;

import org.openapi4j.parser.model.v3.Schema;

public abstract class GetOperationDefinition {

	private String operationId;
	private String description;

	public GetOperationDefinition(String description) {
		var name = this.getClass().getSimpleName();
		name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
		this.operationId = name;
		this.description = description;
	}

	public String getOperationId() {
		return operationId;
	}

	public String getDescription() {
		return description;
	}

	public abstract Schema getSchema();

	protected abstract JsonProcessor getContentResolver();

}
