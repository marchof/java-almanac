package apidiff.model;

import java.util.Set;

/**
 * Identity: name
 */
public class FieldInfo extends MemberInfo {

	public FieldInfo(ClassInfo owner, String name, int access, String desc) {
		super(owner, name, access, desc);
	}

	@Override
	public ElementType getType() {
		return ElementType.FIELD;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Set<ElementTag> getTags() {
		return ModifierTag.getModifiers(access);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MemberInfo)) {
			return false;
		}
		MemberInfo other = (MemberInfo) obj;
		return name.equals(other.name);
	}
	
}
