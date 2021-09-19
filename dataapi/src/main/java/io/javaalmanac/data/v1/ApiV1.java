package io.javaalmanac.data.v1;

import java.util.Arrays;
import java.util.List;

import org.openapi4j.parser.model.v3.Info;
import org.openapi4j.parser.model.v3.License;

import io.javaalmanac.data.ApiDefinition;
import io.javaalmanac.data.NamedSchema;
import io.javaalmanac.data.PathElementDefinition;

public class ApiV1 implements ApiDefinition {

	@Override
	public Info getInfo() {
		return new Info() //
				.setTitle("data.javaalmanac.io") //
				.setDescription("Open data behind javaalmanac.io") //
				.setVersion("1.0.0") //
				.setLicense(new License() //
						.setName("CC BY-SA 4.0") //
						.setUrl("https://creativecommons.org/licenses/by-sa/4.0/") //
				);
	}

	@Override
	public String getPathPrefix() {
		return "/v1";
	}

	@Override
	public PathElementDefinition getPathDefinition() {
		return new PathElementDefinition() //
				.path("jdk", new PathElementDefinition() //
						.path("versions", new PathElementDefinition() //
								.get(new GetJdkVersionList()) //
								.param("version", GetJdkVersionList.JDK_VERSIONS, new PathElementDefinition() //
										.get(new GetJdkVersion()) //
										.path("apidiffs", new PathElementDefinition() //
												.get(new GetJdkVersionApiDiffList()) //
												.param("baseversion", GetJdkVersionApiDiffList.BASE_VERSIONS,
														new PathElementDefinition() //
																.get(new GetJdkVersionApiDiff()))))) //

						.path("vendors", new PathElementDefinition() //
								.get(new GetJdkVendorList()) //
								.param("vendor", GetJdkVendorList.JDK_VENDORS, new PathElementDefinition() //
										.get(new GetJdkVendor()))));

	}

	@Override
	public List<NamedSchema> getSchemas() {
		return Arrays.asList(SchemaV1.values());
	}

}
