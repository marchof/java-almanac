package apidiff.cmp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;

public class Delta {

	public static enum Status {
		NOTMODIFIED, MODIFIED, ADDED, REMOVED
	}

	private final Status status;

	private final ElementInfo oldElement, newElement;

	private final List<Delta> children;

	private final Set<ElementTag> addedTags, removedTags;

	public Delta(ElementInfo oldElement, ElementInfo newElement) {
		this.oldElement = oldElement;
		this.newElement = newElement;

		Set<? extends ElementTag> newTags = newElement.getTags();
		Set<? extends ElementTag> oldTags = oldElement.getTags();
		this.addedTags = new HashSet<>(newTags);
		this.addedTags.removeAll(oldTags);
		this.removedTags = new HashSet<>(oldTags);
		this.removedTags.removeAll(newTags);

		status = (addedTags.isEmpty() && removedTags.isEmpty()) ? Status.NOTMODIFIED : Status.MODIFIED;
		Map<ElementInfo, ElementInfo> oldChildren = new HashMap<>();
		oldElement.getChildren().forEach(c -> oldChildren.put(c, c));
		children = new ArrayList<Delta>();
		for (ElementInfo newChild : newElement.getChildren()) {
			ElementInfo oldChild = oldChildren.remove(newChild);
			if (oldChild == null) {
				children.add(new Delta(Status.ADDED, oldChild, newChild));
			} else {
				Delta delta = new Delta((ElementInfo) oldChild, (ElementInfo) newChild);
				if (delta.status != Status.NOTMODIFIED || !delta.children.isEmpty()) {
					children.add(delta);
				}
			}
		}
		oldChildren.keySet().forEach(c -> children.add(new Delta(Status.REMOVED, c, null)));
		Collections.sort(children, Comparator.comparing((Delta d) -> d.getElement().toString()));
	}

	private Delta(Status status, ElementInfo oldElement, ElementInfo newElement) {
		this.status = status;
		this.oldElement = oldElement;
		this.newElement = newElement;
		this.children = Collections.emptyList();
		this.addedTags = Collections.emptySet();
		this.removedTags = Collections.emptySet();
	}

	public Status getStatus() {
		return status;
	}

	public ElementInfo getElement() {
		return newElement == null ? oldElement : newElement;
	}
	
	public ElementInfo getOldElement() {
		return oldElement;
	}
	
	public ElementInfo getNewElement() {
		return newElement;
	}

	public List<Delta> getChildren() {
		return children;
	}

	public Set<ElementTag> getAddedTags() {
		return addedTags;
	}

	public Set<ElementTag> getRemovedTags() {
		return removedTags;
	}

	@Override
	public String toString() {
		switch (status) {
		case ADDED:
			return "+ " + newElement;
		case REMOVED:
			return "- " + oldElement;
		case MODIFIED:
			return "m " + newElement;
		case NOTMODIFIED:
			return newElement.toString();
		}
		return null;
	}

	public void tree(PrintStream out) {
		tree(out, "");
	}

	private void tree(PrintStream out, String indent) {
		out.print(indent + this);
		removedTags.forEach(t -> out.print("  -" + t));
		addedTags.forEach(t -> out.print("  +" + t));
		out.println();
		String nextIndent = indent + "  ";
		children.forEach(c -> c.tree(out, nextIndent));
	}

}
