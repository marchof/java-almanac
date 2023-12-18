package io.javaalmanac.data;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.javaalmanac.data.JsonProcessor.ProcessingContext;

class JsonProcessorSupport {

	static final Configuration CONFIG = Configuration.builder() //
			.jsonProvider(new JacksonJsonNodeJsonProvider()) //
			.mappingProvider(new JacksonMappingProvider()) //
			.build() //
			.addOptions(Option.SUPPRESS_EXCEPTIONS);

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("`([^`]+)`");

	static JsonPath compileWithParameters(String query, ProcessingContext ctx) {
		return JsonPath.compile(substitute(query, ctx));
	}

	private static String substitute(String template, ProcessingContext ctx) {
		return PLACEHOLDER_PATTERN.matcher(template).replaceAll( //
				mr -> "'" + ctx.getParameter(mr.group(1)) + "'" //
		);
	}

	static ProcessingContext newContext(JsonNode rootdata, Map<String, String> parameters) {
		return new ProcessingContext() {
			@Override
			public JsonNode getRootData() {
				return rootdata;
			}

			@Override
			public String getParameter(String name) {
				String value = parameters.get(name);
				Objects.requireNonNull(value, "Missing parameter: " + name);
				return value;
			}
		};
	}

}
