package apidiff.model;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

public enum ModifierTag implements ElementTag {

	ABSTRACT(Opcodes.ACC_ABSTRACT),

	FINAL(Opcodes.ACC_FINAL),

	PUBLIC(Opcodes.ACC_PUBLIC),

	PROTECTED(Opcodes.ACC_PROTECTED),

	PRIVATE(Opcodes.ACC_PRIVATE),

	STATIC(Opcodes.ACC_STATIC),

	DEPRECATED(Opcodes.ACC_DEPRECATED);

	private int mask;

	private ModifierTag(int mask) {
		this.mask = mask;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static Set<ElementTag> getModifiers(int flags) {
		Set<ElementTag> modifiers = new HashSet<>();
		for (ModifierTag m : values()) {
			if ((flags & m.mask) != 0) {
				modifiers.add(m);
			}
		}
		return modifiers;
	}

}
