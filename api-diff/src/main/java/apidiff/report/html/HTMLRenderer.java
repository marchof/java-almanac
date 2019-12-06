package apidiff.report.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import apidiff.cmp.Delta;
import apidiff.cmp.Delta.Status;
import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.model.ApiInfo;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;
import apidiff.report.IMultiReportOutput;

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
			HTMLElement h1 = body.h1();
			h1.text("New APIs in ");
			h1.text(delta.getElement().getName());

			HTMLElement p = body.p();
			p.text("Comparing ");
			p.text(((ApiInfo) delta.getNewElement()).getDetail());
			p.text(" with ");
			p.text(((ApiInfo) delta.getOldElement()).getDetail());
			p.text(".");

			HTMLElement tbody = body.table().tbody();
			for (Delta c : delta.getChildren()) {
				renderDelta(tbody, c, 0);
			}

			renderFooter(body);
		}
	}

	private void renderDelta(HTMLElement tbody, Delta delta, int level) throws IOException {
		HTMLElement tr = tbody.tr();
		renderElement(tr.td(), delta, level);
		renderTags(tr.td(), delta);
		for (Delta c : delta.getChildren()) {
			renderDelta(tbody, c, level + 1);
		}
	}

	private void renderElement(HTMLElement parent, Delta delta, int level) throws IOException {
		ElementInfo element = delta.getElement();
		parent = parent.div(element.getType().name().toLowerCase());
		parent.attr("style", String.format("margin-left: %dpx;", level * 18));
		parent = addLink(parent, delta);
		trimText(parent, element.getDisplayName(), 80);
	}

	private HTMLElement addLink(HTMLElement parent, Delta delta) throws IOException {
		switch (delta.getStatus()) {
		case ADDED:
			return parent.a(doc.getLink(delta.getElement()));
		case MODIFIED:
			return parent.a(doc.getLink(delta.getElement()));
		case NOTMODIFIED:
			return parent.div("notmodified");
		default:
			return parent;
		}
	}

	private void renderTags(HTMLElement parent, Delta delta) throws IOException {
		if (Status.ADDED.equals(delta.getStatus())) {
			HTMLElement span = parent.span("added");
			span.text("added");
		}
		if (Status.REMOVED.equals(delta.getStatus())) {
			HTMLElement span = parent.span("removed");
			span.text("removed");
		}
		for (ElementTag tag : delta.getAddedTags()) {
			HTMLElement span = parent.span("tagadd");
			span.b().text("+");
			span.text(" ");
			span.text(tag.toString());
		}
		for (ElementTag tag : delta.getRemovedTags()) {
			HTMLElement span = parent.span("tagremove");
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

	private void renderFooter(HTMLElement parent) throws IOException {
		HTMLElement p = parent.p();
		p.text("Report created by ");
		p.a("https://javaalmanac.io/").text("javaalmanac.io");
		p.text(" on ");
		p.text(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
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
		copyResource("module.png");
	}

	private void copyResource(String name) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(name); OutputStream out = output.createFile(name)) {
			in.transferTo(out);
		}
	}

}
