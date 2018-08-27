package apidiff.loader;

public interface IFilter {
	
	boolean filterClass(String name, int access);

	boolean filterField(String name, int access);

	boolean filterMethod(String name, int access);

}
