package io.javaalmanac.data.output;

import java.io.IOException;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.model.v3.OpenApi3;

import com.fasterxml.jackson.databind.JsonNode;

public interface ApiOutput {

	void writeApiDescription(String prefix, OpenApi3 description)
			throws IOException, EncodeException, ValidationException;

	void writeGetResponse(String path, JsonNode content) throws IOException;

	void finish() throws IOException;

}
