package io.javaalmanac.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Parse all Json files from a directory tree and merge them into a single data
 * tree.
 */
public class JsonLoader {

	private static final Logger LOG = LoggerFactory.getLogger(JsonLoader.class);

	private static final String JSON_EXT = ".json";

	private final ObjectMapper mapper;

	public JsonLoader() {
		mapper = new ObjectMapper();
	}

	private ObjectNode parseJson(Path file) throws IOException {
		LOG.debug("Reading {}", file);
		return (ObjectNode) mapper.readTree(file.toFile());
	}

	private void parseAll(Path folder, ObjectNode parent) throws IOException {
		for (Path p : Files.list(folder).toList()) {
			String name = p.getFileName().toString();
			if (Files.isDirectory(p)) {
				parseAll(p, getOrCreateChild(parent, name));
			}
			if (Files.isRegularFile(p) && name.endsWith(JSON_EXT)) {
				name = name.replace(JSON_EXT, "");
				getOrCreateChild(parent, name).setAll(parseJson(p));
			}
		}
	}

	private static ObjectNode getOrCreateChild(ObjectNode parent, String name) {
		ObjectNode child = (ObjectNode) parent.get(name);
		if (child == null) {
			child = parent.objectNode();
			parent.replace(name, child);
		}
		return child;
	}

	public ObjectNode parseTree(Path folder) throws IOException {
		ObjectNode root = mapper.getNodeFactory().objectNode();
		parseAll(folder, root);
		return root;
	}

}
