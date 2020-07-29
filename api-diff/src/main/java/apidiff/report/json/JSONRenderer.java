package apidiff.report.json;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import com.google.gson.stream.JsonWriter;

import apidiff.cmp.Delta;
import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.model.ApiInfo;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;
import apidiff.report.IMultiReportOutput;

public class JSONRenderer {

	private IMultiReportOutput output;
	private IJavaDocLinkProvider doc;

	public JSONRenderer(IMultiReportOutput output, IJavaDocLinkProvider doc) throws IOException {
		this.output = output;
		this.doc = doc;
	}

	public void render(Delta delta) throws IOException {
		writeContent(delta);
		writeData(delta);
	}

	private void writeContent(Delta delta) throws IOException {
		String filename = String.format("content/jdk/%s/apidiff/%s.md", delta.getNewElement().getName(),
				delta.getOldElement().getName());
		try (PrintWriter writer = new PrintWriter(output.createFile(filename), false, StandardCharsets.UTF_8)) {
			writer.println("---");
			writer.println(String.format("title: \"New APIs in Java %s\"", delta.getNewElement().getName()));
			writer.println("type: \"apidiff\"");
			writer.println(String.format("targetversion: \"%s\"", delta.getNewElement().getName()));
			writer.println(String.format("baseversion: \"%s\"", delta.getOldElement().getName()));
			writer.println("---");
		}
	}

	private void writeData(Delta delta) throws IOException {
		String filename = String.format("data/jdk/versions/%s/apidiff/%s.json", delta.getNewElement().getName(),
				delta.getOldElement().getName());
		try (JsonWriter json = new JsonWriter(
				new OutputStreamWriter(output.createFile(filename), StandardCharsets.UTF_8))) {
			json.setIndent("\t");
			json.beginObject();
			version(json.name("base"), (ApiInfo) delta.getOldElement());
			version(json.name("target"), (ApiInfo) delta.getNewElement());
			json.name("order").value(Double.valueOf(delta.getOldElement().getName()));
			deltas(json, delta.getChildren());
			json.endObject();
		}
	}

	private void version(JsonWriter json, ApiInfo info) throws IOException {
		json.beginObject();
		json.name("version").value(info.getName());
		json.name("build").value(info.getDetail());
		json.endObject();
	}

	private void deltas(JsonWriter json, List<Delta> children) throws IOException {
		if (!children.isEmpty()) {
			json.name("deltas").beginArray();
			for (Delta c : children) {
				delta(json, c);
			}
			json.endArray();
		}
	}
	
	private void delta(JsonWriter json, Delta delta) throws IOException {
		json.beginObject();
		ElementInfo element = delta.getElement();
		json.name("type").value(element.getType().name().toLowerCase());
		json.name("name").value(element.getDisplayName());
		json.name("status").value(delta.getStatus().name().toLowerCase());
		String javadoc = doc.getLink(element);
		if (javadoc != null) {
			json.name("javadoc").value(javadoc);
		}
		tags(json, "addedTags", delta.getAddedTags());
		tags(json, "removedTags", delta.getRemovedTags());
		deltas(json, delta.getChildren());	
		json.endObject();
	}

	private void tags(JsonWriter json, String key, Set<ElementTag> tags) throws IOException {
		if (!tags.isEmpty()) {
			json.name(key).beginArray();
			for (ElementTag tag : tags) {
				json.value(tag.toString());
			}
			json.endArray();
		}
	}

}
