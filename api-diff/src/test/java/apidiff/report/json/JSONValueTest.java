package apidiff.report.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JSONValueTest {

	private StringWriter result;

	private JSONValue value;

	@BeforeEach
	void beforeEach() {
		result = new StringWriter();
		value = new JSONValue(result);
	}

	@Test
	void string() throws IOException {
		value.string("hello");
		assertResult("\"hello\"");
	}
	
	@Test
	void string_null() throws IOException {
		value.string(null);
		assertResult("null");
	}
	
	@Test
	void string_escape() throws IOException {
		value.string("He said:\t\"Hi!\"");
		assertResult("\"He said:\\t\\\"Hi!\\\"\"");
	}	
	@Test
	void number_int() throws IOException {
		value.number(-123);
		assertResult("-123");
	}
	
	@Test
	void bool_true() throws IOException {
		value.bool(true);
		assertResult("true");
	}

	@Test
	void bool_false() throws IOException {
		value.bool(false);
		assertResult("false");
	}
	
	@Test
	void nul() throws IOException {
		value.nul();
		assertResult("null");
	}

	@Test
	void array() throws IOException {
		JSONArray a = value.array();
		a.add().nul();
		a.add().number(3);
		a.add().string("abc");
		value.finish();
		assertResult("[null,3,\"abc\"]");
	}

	
	@Test
	void array_array() throws IOException {
		JSONArray a = value.array();
		a.add().array().add().string("nested");;
		a.add().number(3);
		value.finish();
		assertResult("[[\"nested\"],3]");
	}
	
	@Test
	void object() throws IOException {
		JSONObject o = value.object();
		o.add("firstName").string("Donald");
		o.add("lastName").string("Duck");
		value.finish();
		assertResult("{\"firstName\":\"Donald\",\"lastName\":\"Duck\"}");
	}
	
	@Test
	void object_array() throws IOException {
		JSONObject o = value.object();
		o.add("items").array().add().string("book");
		o.add("price").number(100);
		value.finish();
		assertResult("{\"items\":[\"book\"],\"price\":100}");
	}

	@Test
	void string_should_throw_exception_when_called_again() throws IOException {
		value.string(null);
		assertThrows(IllegalStateException.class, () -> value.string(null));	
	}
	
	@Test
	void object_should_throw_exception_when_finished() throws IOException {
		JSONObject o = value.object();
		JSONValue nested = o.add("nested");
		o.add("next").string("next");
		assertThrows(IllegalStateException.class, () -> nested.string("too late"));	
	}
	
	
	private void assertResult(String expected) {
		assertEquals(expected, result.toString());
	}

}
