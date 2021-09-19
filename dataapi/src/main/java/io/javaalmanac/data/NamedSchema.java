package io.javaalmanac.data;

import org.openapi4j.parser.model.v3.Schema;

public interface NamedSchema {

	String name();

	Schema schema();

}
