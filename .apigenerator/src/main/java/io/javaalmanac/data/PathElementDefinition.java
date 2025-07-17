package io.javaalmanac.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.model.v3.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.javaalmanac.data.output.ApiOutput;

/**
 * Data structure and builder API to define a path element with its operations
 * and its sub-elements.
 */
public class PathElementDefinition {

	private final Map<String, PathElementDefinition> subpaths = new LinkedHashMap<>();

	private String paramName = null;
	private JsonProcessor paramResolver;
	private PathElementDefinition paramChild = null;

	private GetOperationDefinition get;

	// === Builder API ===

	public PathElementDefinition path(String name, PathElementDefinition child) {
		subpaths.put(name, child);
		return this;
	}

	public PathElementDefinition param(String name, JsonProcessor resolver, PathElementDefinition child) {
		paramName = name;
		paramResolver = resolver;
		paramChild = child;
		return this;
	}

	public PathElementDefinition get(GetOperationDefinition operation) {
		this.get = operation;
		return this;
	}

	// === OpenAPI Description ===

	public Map<String, org.openapi4j.parser.model.v3.Path> getPaths() {
		var paths = new LinkedHashMap<String, org.openapi4j.parser.model.v3.Path>();
		findPaths(paths, "", List.of());
		return paths;
	}

	private void findPaths(Map<String, org.openapi4j.parser.model.v3.Path> result, String path, List<String> params) {
		if (get != null) {
			Operation operation = new Operation() //
					.setOperationId(get.getOperationId()) //
					.setSummary(get.getDescription()) //
					.setResponse("200", new Response() //
							.setDescription("OK") //
							.setContentMediaType("application/json", new MediaType() //
									.setSchema(get.getSchema()))); //
			if (!params.isEmpty()) {
				operation.setResponse("404", new Response() //
						.setDescription("Not Found"));
			}
			for (var p : params) {
				Parameter parameter = new Parameter() //
						.setName(p) //
						.setIn("path");
				parameter.setRequired(Boolean.TRUE);
				parameter.setSchema(new Schema().setType("string"));
				operation.addParameter(parameter);
			}
			result.put(path, new org.openapi4j.parser.model.v3.Path().setOperation("get", operation));
		}
		if (paramName != null) {
			var extParams = new ArrayList<>(params);
			extParams.add(paramName);
			paramChild.findPaths(result, path + "/{" + paramName + "}", extParams);

		} else {
			for (var e : subpaths.entrySet()) {
				e.getValue().findPaths(result, path + "/" + e.getKey(), params);
			}
		}

	}

	// === Content Generator ===

	public void generateContent(ApiOutput output, String basepath, ObjectNode data)
			throws IOException, ValidationException {
		generateContent(output, basepath, Map.of(), data);
	}

	private void generateContent(ApiOutput output, String path, Map<String, String> paramValues, ObjectNode data)
			throws IOException, ValidationException {
		if (get != null) {
			JsonNode content = get.getContentResolver().apply(data, paramValues);
			output.writeGetResponse(path, content);
		}
		if (paramName != null) {
			var extParamValues = new HashMap<>(paramValues);
			for (var v : paramResolver.applyAsStringList(data, paramValues)) {
				extParamValues.put(paramName, v);
				paramChild.generateContent(output, path + "/" + v, Map.copyOf(extParamValues), data);
			}
		} else {
			for (var e : subpaths.entrySet()) {
				e.getValue().generateContent(output, path + "/" + e.getKey(), paramValues, data);
			}
		}
	}

}
