package apidiff.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ElementInfo {

	// internal VM name
	private final String name;

	private final Set<ElementTag> tags = new HashSet<>();

	protected ElementInfo(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public abstract ElementType getType();

	public abstract String getDisplayName();

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
		return String.format("%s[%s]", getType(), getDisplayName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ElementInfo other = (ElementInfo) obj;
		return name.equals(other.name);
	}

}
