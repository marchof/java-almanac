package io.javaalmanac.data;

import static java.lang.System.getenv;
import static java.util.stream.Collectors.toMap;

import java.nio.file.Paths;

import org.openapi4j.parser.model.v3.Components;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Server;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.javaalmanac.data.output.ApiOutput;
import io.javaalmanac.data.output.ContentValidator;
import io.javaalmanac.data.output.S3Output;
import io.javaalmanac.data.v1.ApiV1;

public class ApiGenerator {

	private static final String OPENAPI_VERSION = "3.0.1";
	private static final String SERVER = "https://data.javaalmanac.io";

	public static void main(String[] args) throws Exception {

		JsonLoader r = new JsonLoader();
		ObjectNode data = r.parseTree(Paths.get("../site/data"));

		generateApi(new ApiV1(), data, //
				new ContentValidator(), //
				new S3Output(getenv("BUCKET"), getenv("DISTRIBUTION")));

	}

	private static void generateApi(ApiDefinition apiDefinition, ObjectNode data, ApiOutput... outputs)
			throws Exception {

		PathElementDefinition definition = apiDefinition.getPathDefinition();

		OpenApi3 api = new OpenApi3();
		api.setOpenapi(OPENAPI_VERSION);
		api.setInfo(apiDefinition.getInfo()) //
				.addServer(new Server() //
						.setUrl(SERVER + apiDefinition.getPathPrefix()));
		api.setComponents(new Components() //
				.setSchemas(
						apiDefinition.getSchemas().stream().collect(toMap(NamedSchema::name, NamedSchema::schema))));
		api.setPaths(definition.getPaths());
		api = OpenApi3Support.connectRefs(api);

		for (ApiOutput output : outputs) {
			output.writeApiDescription(apiDefinition.getPathPrefix(), api);
			definition.generateContent(output, apiDefinition.getPathPrefix(), data);
			output.finish();
		}
	}

}
