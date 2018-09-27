package apidiff.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import apidiff.cmp.Delta;
import apidiff.cmp.Delta.Status;
import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;

public class HTMLRenderer {

	private IMultiReportOutput output;
	private IJavaDocLinkProvider doc;

	public HTMLRenderer(IMultiReportOutput output, IJavaDocLinkProvider doc) {
		this.output = output;
		this.doc = doc;
	}

	public void render(Delta delta) throws IOException {
		copyResources();
		try (HTMLElement html = new HTMLElement(output.createFile("index.html"), "UTF-8")) {

			HTMLElement head = html.head();
			head.title().text(delta.getElement().getName());
			head.link("stylesheet", "style.css", "text/css");

			HTMLElement body = html.body();
			body.h1().text(delta.getElement().getName());

			HTMLElement tbody = body.table().tbody();
			for (Delta c : delta.getChildren()) {
				renderElement(tbody, c);
			}
		}
	}

	private void renderElement(HTMLElement tbody, Delta delta) throws IOException {
		HTMLElement tr = tbody.tr();
		ElementInfo element = delta.getElement();
		HTMLElement td = tr.td(element.getType().name().toLowerCase());
		HTMLElement linked = td;
		if (!Status.REMOVED.equals(delta.getStatus())) {
			linked = linked.a(doc.getLink(element));
		}
		trimText(linked, element.getName(), 80);
		renderTags(tr.td(), delta);
		for (Delta c : delta.getChildren()) {
			renderElement(tbody, c);
		}
	}

	private void renderTags(HTMLElement td, Delta delta) throws IOException {
		if (Status.ADDED.equals(delta.getStatus())) {
			HTMLElement span = td.span("added");
			span.text("added");
		}
		if (Status.REMOVED.equals(delta.getStatus())) {
			HTMLElement span = td.span("removed");
			span.text("removed");
		}
		for (ElementTag tag : delta.getAddedTags()) {
			HTMLElement span = td.span("tagadd");
			span.b().text("+");
			span.text(" ");
			span.text(tag.toString());
		}
		for (ElementTag tag : delta.getRemovedTags()) {
			HTMLElement span = td.span("tagremove");
			span.b().text("-");
			span.text(" ");
			span.text(tag.toString());
		}
	}
	
	private void trimText(HTMLElement parent, String text, int maxlength) throws IOException {
		if (text.length() > maxlength) {
			parent = parent.div();
			parent.attr("title", text);
			text = text.substring(0, maxlength) + "...";
		}
		parent.text(text);
	}

	private void copyResources() throws IOException {
		copyResource("style.css");
		copyResource("annotation.png");
		copyResource("class.png");
		copyResource("enum.png");
		copyResource("field.png");
		copyResource("interface.png");
		copyResource("method.png");
		copyResource("package.png");
	}

	private void copyResource(String name) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(name); OutputStream out = output.createFile(name)) {
			in.transferTo(out);
		}
	}

}
