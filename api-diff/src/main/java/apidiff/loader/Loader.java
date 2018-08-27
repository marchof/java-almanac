package apidiff.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;

public class Loader {

	@SuppressWarnings("deprecation")
	private static final int ASM_API = Opcodes.ASM7_EXPERIMENTAL;

	private Consumer<ClassInfo> output;
	private IFilter filter;

	public Loader(Consumer<ClassInfo> output, IFilter filter) {
		this.output = output;
		this.filter = filter;
	}

	public void loadClassFile(InputStream in) throws IOException {
		ClassReader reader = new ClassReader(in);
		if (!filter.filterClass(reader.getClassName(), reader.getAccess())) {
			return;
		}
		ClassInfo c = new ClassInfo(reader.getClassName(), reader.getAccess());
		reader.accept(new ClassVisitor(ASM_API) {

			@Override
			public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
				if (filter.filterField(name, access)) {
					c.addField(new FieldInfo(name, access, descriptor));
				}
				return null;
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) {
				if (filter.filterMethod(name, access)) {
					c.addMethod(new MethodInfo(name, access, descriptor));
				}
				return null;
			}
		}, ClassReader.SKIP_CODE);
		output.accept(c);
	}

	public void loadZip(InputStream in) throws IOException {
		try (ZipInputStream zip = new ZipInputStream(in)) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					loadClassFile(zip);
				}
			}
		}
	}

	public void loadZip(Path path) throws IOException {
		try (InputStream in = Files.newInputStream(path)) {
			loadZip(in);
		}
	}

	public void loadJMod(Path path) throws IOException {
		try (InputStream in = Files.newInputStream(path)) {
			// Remove header:
			in.read();
			in.read();
			in.read();
			in.read();

			loadZip(in);
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

}
