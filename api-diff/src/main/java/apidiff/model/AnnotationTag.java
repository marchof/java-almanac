package apidiff.model;

public enum AnnotationTag implements ElementTag {

	forRemoval;

	public static ElementTag getTag(String annotationType, String valueName, Object value) {
		if ("Ljava/lang/Deprecated;".equals(annotationType) && "forRemoval".equals(valueName)
				&& Boolean.TRUE.equals(value)) {
			return forRemoval;
		}
		return null;
	}

}