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

	private final ElementInfo element;

	private final List<Delta> children;

	private final Set<ElementTag> addedTags, removedTags;

	public <E extends ElementInfo> Delta(E oldElement, E newElement) {
		Set<? extends ElementTag> newTags = newElement.getTags();
		Set<? extends ElementTag> oldTags = oldElement.getTags();
		this.addedTags = new HashSet<>(newTags);
		this.addedTags.removeAll(oldTags);
		this.removedTags = new HashSet<>(oldTags);
		this.removedTags.removeAll(newTags);
		
		status = (addedTags.isEmpty() && removedTags.isEmpty()) ? Status.NOTMODIFIED : Status.MODIFIED;
		element = newElement;
		Map<ElementInfo, ElementInfo> oldChildren = new HashMap<>();
		oldElement.getChildren().forEach(c -> oldChildren.put(c, c));
		children = new ArrayList<Delta>();
		for (ElementInfo newChild : newElement.getChildren()) {
			ElementInfo oldChild = oldChildren.remove(newChild);
			if (oldChild == null) {
				children.add(new Delta(Status.ADDED, newChild));
			} else {
				Delta delta = new Delta((ElementInfo) oldChild, (ElementInfo) newChild);
				if (delta.status != Status.NOTMODIFIED || !delta.children.isEmpty()) {
					children.add(delta);
				}
			}
		}
		oldChildren.keySet().forEach(c -> children.add(new Delta(Status.REMOVED, c)));
		Collections.sort(children, Comparator.comparing((Delta d) -> d.getElement().toString()));
	}

	private Delta(Status status, ElementInfo element) {
		this.status = status;
		this.element = element;
		this.children = Collections.emptyList();
		this.addedTags = Collections.emptySet();
		this.removedTags = Collections.emptySet();		
	}

	public Status getStatus() {
		return status;
	}

	public ElementInfo getElement() {
		return element;
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
		out.print(indent + this);
		removedTags.forEach(t -> out.print("  -" + t));
		addedTags.forEach(t -> out.print("  +" + t));
		out.println();
		String nextIndent = indent + "  ";
		children.forEach(c -> c.tree(out, nextIndent));
	}

}
