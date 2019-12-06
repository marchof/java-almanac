package apidiff.report.json;

import java.io.IOException;
import java.io.Writer;

public class JSONObject extends JSONElement {

	public JSONObject(Writer writer) throws IOException {
		super(writer);
		writer.write('{');
	}

	public JSONValue add(String key) throws IOException {
		finishLastChild();
		quoteAndEscapeString(key);
		writer.write(':');
		JSONValue jsonValue = new JSONValue(writer);
		lastChild = jsonValue;
		return jsonValue;
	}

	protected void writeSeparator() throws IOException {
		writer.write(',');
	}

	@Override
	protected void writeEpilog() throws IOException {
		writer.write('}');
	}

}
