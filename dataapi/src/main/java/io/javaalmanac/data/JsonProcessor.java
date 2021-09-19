package io.javaalmanac.data;

import static io.javaalmanac.data.JsonProcessorSupport.CONFIG;
import static io.javaalmanac.data.JsonProcessorSupport.compileWithParameters;
import static io.javaalmanac.data.JsonProcessorSupport.newContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.PathNotFoundException;

import io.javaalmanac.data.JsonProcessor.ProcessingContext;

public interface JsonProcessor extends BiFunction<JsonNode, ProcessingContext, JsonNode> {

	public interface ProcessingContext {

		JsonNode getRootData();

		String getParameter(String name);

	}

	// Convenience Methods for root evaluation

	public default JsonNode apply(JsonNode rootdata, Map<String, String> parameters) {
		return apply(rootdata, newContext(rootdata, parameters));
	}

	public default List<String> applyAsStringList(JsonNode rootdata, Map<String, String> parameters) {
		return StreamSupport.stream(apply(rootdata, parameters).spliterator(), false) //
				.map(JsonNode::asText) //
				.collect(Collectors.toList());
	}

	// Chaining

	public default JsonProcessor select(String path) {
		return (data, ctx) -> {
			var expr = compileWithParameters(path, ctx);
			return expr.read(apply(data, ctx), CONFIG);
		};
	}

	public default <U extends Comparable<? super U>> JsonProcessor sort(Function<JsonNode, ? extends U> converter) {
		return (data, ctx) -> {
			Comparator<JsonNode> cmp = Comparator.comparing(converter);
			var array = apply(data, ctx).<ArrayNode>require();
			var result = array.arrayNode();
			StreamSupport.stream(array.spliterator(), false) //
					.sorted(cmp) //
					.forEach(result::add);
			return result;
		};
	}

	public default JsonProcessor ignorePathNotFound() {
		return (data, ctx) -> {
			try {
				return apply(data, ctx);
			} catch (PathNotFoundException e) {
				return JsonNodeFactory.instance.arrayNode();
			}
		};
	}

	public default JsonProcessor delete(String selector) {
		return (data, ctx) -> {
			var expr = compileWithParameters(selector, ctx);
			var copy = apply(data, ctx).deepCopy();
			expr.delete(copy, CONFIG);
			return copy;
		};
	}

	public default JsonProcessor map(String selector, JsonProcessor mapper) {
		return (data, ctx) -> {
			var expr = compileWithParameters(selector, ctx);
			var copy = apply(data, ctx).deepCopy();
			return expr.map(copy, (n, conf) -> mapper.apply((JsonNode) n, ctx), CONFIG);
		};
	}

	public default JsonProcessor set(String attribute, JsonProcessor source) {
		return (data, ctx) -> {
			var copy = apply(data, ctx).deepCopy().<ObjectNode>require();
			copy.set(attribute, source.apply(copy, ctx));
			return copy;
		};
	}

	// Factories Methods

	public static JsonProcessor root() {
		return (data, ctx) -> {
			return ctx.getRootData();
		};
	}

}
