package apidiff.loader;

import org.objectweb.asm.Opcodes;

public class PublicApiFilter implements IFilter {

	@Override
	public boolean filterClass(String name, int acc) {
		if (!name.startsWith("java")) {
			return false;
		}
		return is(acc, Opcodes.ACC_PUBLIC) && !is(acc, Opcodes.ACC_SYNTHETIC);
	}

	@Override
	public boolean filterField(String name, int acc) {
		return (is(acc, Opcodes.ACC_PUBLIC) || is(acc, Opcodes.ACC_PROTECTED)) && !is(acc, Opcodes.ACC_SYNTHETIC);
	}

	
	@Override
	public boolean filterMethod(String name, int acc) {
		return (is(acc, Opcodes.ACC_PUBLIC) || is(acc, Opcodes.ACC_PROTECTED)) && !is(acc, Opcodes.ACC_SYNTHETIC);
	}

	private static boolean is(int acc, int flag) {
		return (acc & flag) != 0;
	}

}
