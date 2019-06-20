package apidiff.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.ModuleInfo;

public class Loader {

	static final int ASM_API = Opcodes.ASM7;

	static final int MAX_VERSION = Opcodes.V13;

	private Consumer<ClassInfo> output;
	private IFilter filter;

	public Loader(Consumer<ClassInfo> output, IFilter filter) {
		this.output = output;
		this.filter = filter;
	}

	public void loadClassFile(InputStream in, String module) throws IOException {
		ClassReader reader = new ClassReader(downgrade(in));
		if (!filter.filterClass(reader.getClassName(), reader.getAccess())) {
			return;
		}
		ClassInfo c = new ClassInfo(reader.getClassName(), reader.getAccess(), module, reader.getSuperName(),
				reader.getInterfaces());
		reader.accept(new ClassVisitor(ASM_API) {

			@Override
			public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
				return new AnnotationTagExtractor(c, descriptor);
			}

			@Override
			public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
				if (filter.filterField(name, access)) {
					FieldInfo f = new FieldInfo(c, name, access, descriptor);
					c.addField(f);
					return new FieldVisitor(ASM_API) {
						@Override
						public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
							return new AnnotationTagExtractor(f, descriptor);
						}
					};
				}
				return null;
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) {
				if (filter.filterMethod(name, access)) {
					MethodInfo m = new MethodInfo(c, name, access, descriptor, exceptions);
					c.addMethod(m);
					return new MethodVisitor(ASM_API) {
						@Override
						public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
							return new AnnotationTagExtractor(m, descriptor);
						}
					};
				}
				return null;
			}
		}, ClassReader.SKIP_CODE);
		output.accept(c);
	}

	private static byte[] downgrade(InputStream in) throws IOException {
		byte[] buffer = in.readAllBytes();
		int version = (((buffer[6] & 0xFF) << 8) | (buffer[7] & 0xFF));
		if (version > MAX_VERSION) {
			buffer[6] = (byte) (MAX_VERSION >>> 8);
			buffer[7] = (MAX_VERSION);
		}
		return buffer;
	}

	public void loadZip(InputStream in, String module) throws IOException {
		try (ZipInputStream zip = new ZipInputStream(in)) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					loadClassFile(zip, module);
				}
			}
		}
	}

	public void loadZip(Path path) throws IOException {
		try (InputStream in = Files.newInputStream(path)) {
			loadZip(in, ModuleInfo.UNDEFINED);
		}
	}

	public void loadJMod(Path path) throws IOException {
		String module = path.getFileName().toString();
		int idx = module.lastIndexOf('.');
		if (idx != -1) {
			module = module.substring(0, idx);
		}
		try (InputStream in = Files.newInputStream(path)) {
			// Remove header:
			in.read();
			in.read();
			in.read();
			in.read();

			loadZip(in, module);
		}
	}

	public void loadDirectory(Path path) throws IOException {
		for (Path f : Files.newDirectoryStream(path)) {
			String name = f.toString();
			if (name.endsWith(".jmod")) {
				loadJMod(f);
			}
			if (name.endsWith(".jar")) {
				loadZip(f);
			}
		}
	}

	public void loadJDK(Path path) throws IOException {
		Path lib = path.resolve("jre/lib");
		if (Files.isDirectory(lib)) {
			loadDirectory(lib);
			return;
		}
		Path jmods = path.resolve("jmods");
		if (Files.isDirectory(jmods)) {
			loadDirectory(jmods);
			return;
		}
		throw new IOException("Unknown JDK layout at " + path);
	}

}
