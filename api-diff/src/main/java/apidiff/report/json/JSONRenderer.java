package apidiff.report.json;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import apidiff.cmp.Delta;
import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.model.ApiInfo;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;
import apidiff.model.ElementType;
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
		try (Writer writer = new OutputStreamWriter(output.createFile(filename), StandardCharsets.UTF_8)) {
			JSONObject root = new JSONObject(writer);
			version(root.add("base").object(), (ApiInfo) delta.getOldElement());
			version(root.add("target").object(), (ApiInfo) delta.getNewElement());
			JSONArray deltaarray = root.add("deltas").array();
			for (Delta d : delta.getChildren()) {
				delta(deltaarray, d, 0);
			}
			root.finish();
		}
	}

	private void version(JSONObject version, ApiInfo info) throws IOException {
		version.add("version").string(info.getName());
		version.add("build").string(info.getDetail());
	}

	private void delta(JSONArray parent, Delta delta, int level) throws IOException {
		JSONObject object = parent.add().object();
		ElementInfo element = delta.getElement();
		object.add("type").string(element.getType().name().toLowerCase());
		object.add("name").string(element.getDisplayName());
		object.add("status").string(delta.getStatus().name().toLowerCase());
		object.add("level").number(level);
		if (!ElementType.API.equals(element.getType())) {
			String javadoc = doc.getLink(element);
			if (javadoc != null) {
				object.add("javadoc").string(javadoc);
			}
		}
		tags(object, "addedTags", delta.getAddedTags());
		tags(object, "removedTags", delta.getRemovedTags());
		List<Delta> children = delta.getChildren();
		for (Delta c : children) {
			delta(parent, c, level + 1);
		}
	}

	private void tags(JSONObject object, String key, Set<ElementTag> tags) throws IOException {
		if (!tags.isEmpty()) {
			JSONArray array = object.add(key).array();
			for (ElementTag tag : tags) {
				array.add().string(tag.toString());
			}
		}
	}

}
