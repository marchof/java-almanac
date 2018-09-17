package apidiff.loader;

import org.objectweb.asm.AnnotationVisitor;

import apidiff.model.AnnotationTag;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;

class AnnotationTagExtractor extends AnnotationVisitor{

	private ElementInfo element;
	private String annotationType;

	public AnnotationTagExtractor(ElementInfo element, String annotationType) {
		super(Loader.ASM_API);
		this.element = element;
		this.annotationType = annotationType;
	}
	
	@Override
	public void visit(String name, Object value) {
		ElementTag tag = AnnotationTag.getTag(annotationType, name, value);
		if (tag != null) {
			element.addTag(tag);
		}
	}

}
