package apidiff.report.json;

import java.io.IOException;
import java.io.Writer;

public class JSONArray extends JSONElement {

	public JSONArray(Writer writer) throws IOException {
		super(writer);
		writer.write('[');
	}

	public JSONValue add() throws IOException {
		finishLastChild();
		JSONValue jsonValue = new JSONValue(writer);
		lastChild = jsonValue;
		return jsonValue;
	}
	
	protected void writeSeparator() throws IOException {
		writer.write(',');
	}

	@Override
	protected void writeEpilog() throws IOException {
		writer.write(']');
	}

}
