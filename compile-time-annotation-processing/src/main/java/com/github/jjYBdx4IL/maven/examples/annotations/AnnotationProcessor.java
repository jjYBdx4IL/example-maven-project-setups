package com.github.jjYBdx4IL.maven.examples.annotations;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.writer.FileCodeWriter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@SupportedAnnotationTypes("com.github.jjYBdx4IL.maven.examples.annotations.ExampleAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    private static final Logger log = LoggerFactory.getLogger(AnnotationProcessor.class);

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    public AnnotationProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        List<Element> classesFound = new ArrayList<>();

        for (Element elem : roundEnv.getElementsAnnotatedWith(ExampleAnnotation.class)) {
            log.info("annotation found in " + elem.toString());
            
            if (!elem.getKind().isClass() || !(elem instanceof QualifiedNameable)) {
                throw new AnnotationFormatError(elem.toString() + " type not supported by " + ExampleAnnotation.class);
            }

            ExampleAnnotation exampleAnno = elem.getAnnotation(ExampleAnnotation.class);

            TypeMirror requiredIface = elementUtils.getTypeElement(Serializable.class.getCanonicalName()).asType();
            if (typeUtils.isAssignable(requiredIface, elem.asType())) {
                log.info("annotated class implements Serializable");
            }

            classesFound.add(elem);
        }

        try {
            writeSourceCode(classesFound);
        } catch (JClassAlreadyExistsException | IOException ex) {
            throw new AnnotationFormatError("failed to write source code model", ex);
        }

        return true; // no further processing of this annotation type
    }

    private void writeSourceCode(List<Element> classesFound) throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(getClass().getPackage().getName() + ".GeneratedByAnnotationProcessor");

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        AbstractJClass stringBuilder = cm.ref(StringBuilder.class);
        JVar sbVar = toStringMethod.body().decl(stringBuilder, "sb", JExpr._new(stringBuilder));
        for (Element className : classesFound) {
            toStringMethod.body().invoke(sbVar, "append").arg(className + " ");
        }
        toStringMethod.body()._return(sbVar.invoke("toString"));

        File outputDir = new File(System.getProperty("basedir"), "target/generated-sources/anno");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        log.info("writing source code to: " + outputDir.getAbsolutePath());
        cm.build(new FileCodeWriter(outputDir));
    }
}
