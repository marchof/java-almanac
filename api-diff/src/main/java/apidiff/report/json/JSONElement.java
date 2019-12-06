package apidiff.report.json;

import java.io.IOException;
import java.io.Writer;

abstract class JSONElement {

	protected Writer writer;

	protected JSONElement lastChild;

	private boolean finished;

	protected JSONElement(Writer writer) {
		this.writer = writer;
		this.finished = false;
		this.lastChild = null;
	}

	protected void quoteAndEscapeString(String string) throws IOException {
		writer.write('"');
		for (char c : string.toCharArray()) {
			switch (c) {
			case '\b':
				writer.write("\\b");
				break;
			case '\f':
				writer.write("\\f");
				break;
			case '\n':
				writer.write("\\n");
				break;
			case '\r':
				writer.write("\\r");
				break;
			case '\t':
				writer.write("\\t");
				break;
			case '"':
				writer.write("\\\"");
				break;
			case '\\':
				writer.write("\\\\");
				break;
			default:
				writer.write(c);
			}
		}
		writer.write('"');
	}

	public final void finish() throws IOException {
		if (!finished) {
			if (lastChild != null) {
				lastChild.finish();
			}
			writeEpilog();
			finished = true;
		}
	}

	protected void finishLastChild() throws IOException {
		if (lastChild != null) {
			lastChild.finish();
			lastChild = null;
			writeSeparator();
		}
	}

	protected void ensureNotFinished() {
		if (finished) {
			throw new IllegalStateException("JSON element already finished.");
		}
	}

	protected void writeSeparator() throws IOException {
	}
	
	protected void writeEpilog() throws IOException {
	}

}
