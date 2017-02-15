package samurai;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author TonTL
 * @since 4.0
 */
@SupportedAnnotationTypes("samurai.MustOverride")
public class MustOverrideProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            final Set<? extends Element> elts = roundEnv.getElementsAnnotatedWith(te);
            for (Element elt : elts) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("%s must override %s", roundEnv.getRootElements(), elt));
            }
        }
        return true;
    }
}
