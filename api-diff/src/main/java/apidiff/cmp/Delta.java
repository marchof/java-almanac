package apidiff.cmp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apidiff.model.IModelElement;

public class Delta {

	public static enum Status {
		NOTMODIFIED, MODIFIED, ADDED, REMOVED
	}

	private final Status status;

	private Object element;

	private List<Delta> children = new ArrayList<Delta>();

	private String diffInfo = "";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <M extends IModelElement<M>> Delta(M oldElement, M newElement) {
		status = oldElement.isModified(newElement) ? Status.MODIFIED : Status.NOTMODIFIED;
		if (status == Status.MODIFIED) {
			diffInfo = oldElement.getDiffInfo(newElement);
		}
		element = newElement;
		Map<IModelElement<?>, IModelElement<?>> oldChildren = new HashMap<>();
		oldElement.getChildren().forEach(c -> oldChildren.put(c, c));
		for (IModelElement<?> newChild : newElement.getChildren()) {
			IModelElement<?> oldChild = oldChildren.remove(newChild);
			if (oldChild == null) {
				children.add(new Delta(Status.ADDED, newChild));
			} else {
				Delta delta = new Delta((IModelElement) oldChild, (IModelElement) newChild);
				if (delta.status != Status.NOTMODIFIED || !delta.children.isEmpty()) {
					children.add(delta);
				}
			}
		}
		oldChildren.keySet().forEach(c -> children.add(new Delta(Status.REMOVED, c)));
		Collections.sort(children, Comparator.comparing((Delta d) -> d.getElement().toString()));
	}

	private Delta(Status status, Object element) {
		this.status = status;
		this.element = element;
	}

	public Status getStatus() {
		return status;
	}

	public Object getElement() {
		return element;
	}

	List<Delta> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		switch (status) {
		case ADDED:
			return "+ " + element;
		case REMOVED:
			return "- " + element;
		case MODIFIED:
			return "m " + element;
		case NOTMODIFIED:
			return element.toString();
		}
		return null;
	}

	public void tree(PrintStream out) {
		tree(out, "");
	}

	private void tree(PrintStream out, String indent) {
		out.println(indent + this + "  " + diffInfo);
		String nextIndent = indent + "  ";
		children.forEach(c -> c.tree(out, nextIndent));
	}

}
