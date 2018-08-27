package apidiff.model;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

public class Modifiers {
	
	public static Set<String> getModifiers(int acc) {
		Set<String> set = new HashSet<>();
		addModifier(Opcodes.ACC_ABSTRACT, "abstract", acc, set);
		addModifier(Opcodes.ACC_FINAL, "final", acc, set);
		addModifier(Opcodes.ACC_PUBLIC, "public", acc, set);
		addModifier(Opcodes.ACC_PROTECTED, "protected", acc, set);
		addModifier(Opcodes.ACC_PRIVATE, "private", acc, set);
		addModifier(Opcodes.ACC_STATIC, "static", acc, set);
		addModifier(Opcodes.ACC_DEPRECATED, "deprecated", acc, set);
		
		return set;
	}
	
	private static void addModifier(int flag, String name, int acc, Set<String> result) {
		if ((acc & flag) != 0) {
			result.add(name);
		}
	}

}
