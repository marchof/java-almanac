package apidiff.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ElementInfo {
	
	private Set<ElementTag> tags = new HashSet<>();

	public abstract ElementType getType();

	public abstract String getName();

	public final Set<ElementTag> getTags() {
		return tags;
	}
	
	public final void addTag(ElementTag tag) {
		tags.add(tag);
	}

	public final void addTags(Collection<ElementTag> tags) {
		tags.addAll(tags);
	}
	
	public List<ElementInfo> getChildren() {
		return Collections.emptyList();
	}

	public final String toString() {
		return String.format("%s[%s]", getType(), getName());
	};

}
