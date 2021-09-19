package io.javaalmanac.data.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.parser.model.SerializationFlag;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class FileOutput implements ApiOutput {

	private static final Logger LOG = LoggerFactory.getLogger(FileOutput.class);

	private final Path basepath;

	private final OutputStats stats;

	public FileOutput(Path basepath) {
		this.basepath = basepath;
		this.stats = new OutputStats();
	}

	@Override
	public void writeApiDescription(String prefix, OpenApi3 api) throws IOException, EncodeException {
		prefix += "/openapi";
		writeFile(prefix, "json", api.toString(EnumSet.of(SerializationFlag.OUT_AS_JSON)));
		writeFile(prefix, "yaml", api.toString(EnumSet.of(SerializationFlag.OUT_AS_YAML)));
	}

	@Override
	public void writeGetResponse(String path, JsonNode content) throws IOException {
		writeFile(path, "json", content.toString());
	}

	@Override
	public void finish() throws IOException {
		LOG.info("Written {}", stats);
	}

	private void writeFile(String path, String ext, String content) throws IOException {
		Path file = basepath.resolve(path.substring(1));
		Files.createDirectories(file.getParent());
		file = file.resolveSibling(file.getFileName() + "." + ext);

		LOG.info("Writing {}", file);
		Files.writeString(file, content, StandardCharsets.UTF_8);
		stats.addFile(Files.size(file));
	}

}
