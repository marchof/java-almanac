package io.javaalmanac.data.output;

import java.io.IOException;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Request.Method;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.v3.ValidationOptions;

import com.fasterxml.jackson.databind.JsonNode;

public class ContentValidator implements ApiOutput {

	private RequestValidator validator;

	@Override
	public void writeApiDescription(String prefix, OpenApi3 api)
			throws IOException, EncodeException, ValidationException {
		OpenApi3Validator.instance().validate(api);
		ValidationContext<OAI3> ctx = new ValidationContext<>(api.getContext());
		ctx.setOption(ValidationOptions.ADDITIONAL_PROPS_RESTRICT, true);
		validator = new RequestValidator(ctx, api);
	}

	@Override
	public void writeGetResponse(String path, JsonNode content) throws IOException {
		Request request = new DefaultRequest.Builder(path, Method.GET).build();
		Response response = new DefaultResponse.Builder(200).header("Content-Type", "application/json")
				.body(Body.from(content)).build();
		try {
			validator.validate(response, request);
		} catch (ValidationException vex) {
			throw new IOException("Invalid content at " + path, vex);
		}
	}

	@Override
	public void finish() throws IOException {
	}

}
