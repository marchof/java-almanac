package io.javaalmanac.data;

import java.util.List;

import org.openapi4j.parser.model.v3.Info;

public interface ApiDefinition {

	Info getInfo();

	String getPathPrefix();

	List<NamedSchema> getSchemas();

	PathElementDefinition getPathDefinition();

}
