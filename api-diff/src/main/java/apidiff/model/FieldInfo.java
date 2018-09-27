package apidiff.model;

/**
 * Identity: name
 */
public class FieldInfo extends MemberInfo {

	public FieldInfo(ClassInfo owner, String name, int access, String desc) {
		super(owner, name, access, desc);
		addTags(ModifierTag.getModifiers(access));
	}

	@Override
	public ElementType getType() {
		return ElementType.FIELD;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

}
