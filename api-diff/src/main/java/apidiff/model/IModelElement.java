package apidiff.model;

import java.util.Collections;
import java.util.List;

public interface IModelElement<M extends IModelElement<M>> {
	
	default List<IModelElement<?>> getChildren() {
		return Collections.emptyList();
	}
	
	default boolean isModified(M other) {
		return false;
	}
	
	default String getDiffInfo(M other) {
		return "";		
	}

}
