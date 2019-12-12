package apidiff.report.json;

import java.io.IOException;
import java.io.Writer;

public class JSONValue extends JSONElement {

	private boolean written;

	public JSONValue(Writer writer) {
		super(writer);
		this.written = false;
	}

	public void string(String string) throws IOException {
		ensureNotFinished();
		if (string == null) {
			writer.write("null");
		} else {
			quoteAndEscapeString(string);
		}
	}

	public void number(int number) throws IOException {
		ensureNotFinished();
		writer.write(String.valueOf(number));
	}

	public void number(double number) throws IOException {
		ensureNotFinished();
		writer.write(String.valueOf(number));
	}

	public void bool(boolean bool) throws IOException {
		ensureNotFinished();
		writer.write(String.valueOf(bool));
	}

	public void nul() throws IOException {
		ensureNotFinished();
		writer.write("null");
	}

	public JSONArray array() throws IOException {
		ensureNotFinished();
		JSONArray jsonArray = new JSONArray(writer);
		lastChild = jsonArray;
		return jsonArray;
	}

	public JSONObject object() throws IOException {
		ensureNotFinished();
		JSONObject jsonObject = new JSONObject(writer);
		lastChild = jsonObject;
		return jsonObject;
	}

	@Override
	protected void ensureNotFinished() {
		if (written) {
			throw new IllegalStateException("JSON Value already written.");
		}
		written = true;
		super.ensureNotFinished();
	}

}
