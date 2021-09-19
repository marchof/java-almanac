package io.javaalmanac.data;

import java.net.MalformedURLException;
import java.net.URL;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.ReferenceRegistry;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Schema;

import com.fasterxml.jackson.databind.JsonNode;

public class OpenApi3Support {

	private static final URL LOCAL_BASE_URL;

	static {
		try {
			LOCAL_BASE_URL = new URL("http://local/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static Schema newSchemaRef(String ref) {
		Schema s = new Schema();
		s.setReference(new LocalContext(), LOCAL_BASE_URL, ref);
		return s;
	}

	public static Schema newComponentSchemaRef(String name) {
		return newSchemaRef("#/components/schemas/" + name);
	}

	public static Schema newComponentSchemaRef(NamedSchema schema) {
		return newComponentSchemaRef(schema.name());
	}

	public static OpenApi3 connectRefs(OpenApi3 api) throws EncodeException, ResolutionException {
		// Serialize/Deserialize to properly wire references
		api = TreeUtil.json.convertValue(api.toNode(), OpenApi3.class);
		api.setContext(new OAI3Context(LOCAL_BASE_URL, api.toNode()));
		return api;
	}

	private static class LocalContext implements OAIContext {

		private ReferenceRegistry referenceRegistry;

		public LocalContext() {
			referenceRegistry = new ReferenceRegistry(LOCAL_BASE_URL);
		}

		@Override
		public ReferenceRegistry getReferenceRegistry() {
			return referenceRegistry;
		}

		@Override
		public URL getBaseUrl() {
			return null;
		}

		@Override
		public JsonNode getBaseDocument() {
			return null;
		}

	}

}
