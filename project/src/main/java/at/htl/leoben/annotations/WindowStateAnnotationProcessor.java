package at.htl.leoben.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

/**
 * Derieved from
 * - https://www.specialagentsqueaky.com/blog-post/wvn1xas5/2011-08-13-how-to-enforce-static-methods-on-class/
 * - https://github.com/corgrath/abandoned-Requires-Static-Method-Annotation/blob/master/src/com/osbcp/requiresmethodannotation/RequiresMethodAnnotationProcessor.java?utm_source=specialagentsqueaky.com&utm_medium=backlink
 */
public class WindowStateAnnotationProcessor extends AbstractProcessor {
    @Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {

		// Create a string builder for out debug messages
		StringBuilder debug = new StringBuilder();

		// Get all classes that has the annotation
		Set<? extends Element> classElements = roundEnvironment.getElementsAnnotatedWith(WindowStateAnnotation.class);

		// For each class that has the annotation
		for (final Element classElement : classElements) {

			// Get the annotation information
			WindowStateAnnotation annotation = classElement.getAnnotation(WindowStateAnnotation.class);
			String[] states = annotation.states();

            for (String state : states) {
                
                String expectedMethodName = "onStart__" + state;
                // Try and fetch the expected method
                Element methodElement = getMethod(debug, classElement, expectedMethodName);
    
                // Check that the method exists
                if (methodElement == null) {
                    error(classElement, debug, "The class '" + classElement.getSimpleName() + "' requires a method namned '" + expectedMethodName + "'.");
                    return true;
                }
            }
		}

		return true;

	}

	private final Element getMethod(final StringBuilder debug, final Element classElement, final String methodName) {

		List<Element> methods = getEnclosedElements(debug, classElement, ElementKind.METHOD);

		// For each method
		for (final Element methodElement : methods) {

			debug.append("getMethod:" + methodElement.getSimpleName() + ":" + methodName + ":" + methodElement.getSimpleName().toString().equals(methodName) + "\n");
			if (methodElement.getSimpleName().toString().equals(methodName)) {
				return methodElement;
			}

		}

		return null;

	}

	private void error(final Element element, final StringBuilder debug, final String message) {

		// Debug output (uncomment this to show debug message)
		//		processingEnv.getMessager().printMessage(Kind.ERROR, debug.toString() + "\n\n" + message, element);

		// Production output (uncomment this for production message)
		processingEnv.getMessager().printMessage(Kind.ERROR, message, element);

	}

	private List<Element> getEnclosedElements(final StringBuilder debug, final Element element, final ElementKind elementKind) {

		List<Element> list = new ArrayList<Element>();

		for (Element enclosedElement : element.getEnclosedElements()) {
			debug.append("enclosed:" + enclosedElement.getSimpleName() + ":" + enclosedElement.getKind() + "\n");
			if (enclosedElement.getKind() == elementKind) {
				list.add(enclosedElement);
			}
		}

		return list;

	}
}
