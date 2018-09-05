package apidiff.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class ElementInfo {

	public abstract ElementType getType();

	public abstract String getName();

	public Set<ElementTag> getTags() {
		return Collections.emptySet();
	}

	public List<ElementInfo> getChildren() {
		return Collections.emptyList();
	}

	public final String toString() {
		return String.format("%s[%s]", getType(), getName());
	};

}
