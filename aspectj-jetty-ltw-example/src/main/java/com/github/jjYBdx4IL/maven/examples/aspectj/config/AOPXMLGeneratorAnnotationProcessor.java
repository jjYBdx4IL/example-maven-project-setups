package com.github.jjYBdx4IL.maven.examples.aspectj.config;

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
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@SupportedAnnotationTypes("org.aspectj.lang.annotation.Aspect")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AOPXMLGeneratorAnnotationProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AOPXMLGeneratorAnnotationProcessor.class);

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    public AOPXMLGeneratorAnnotationProcessor() {
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

        for (Element elem : roundEnv.getElementsAnnotatedWith(Aspect.class)) {
            LOG.info("annotation found in " + elem.toString());

            if (!elem.getKind().isClass() || !(elem instanceof QualifiedNameable)) {
                throw new AnnotationFormatError(elem.toString() + " type not supported by " + Aspect.class);
            }

            Aspect exampleAnno = elem.getAnnotation(Aspect.class);

            TypeMirror requiredIface = elementUtils.getTypeElement(Serializable.class.getCanonicalName()).asType();
            if (typeUtils.isAssignable(requiredIface, elem.asType())) {
                LOG.info("annotated class implements Serializable");
            }

            classesFound.add(elem);
        }

        try {
            writeXML(classesFound);
        } catch (IOException ex) {
            throw new AnnotationFormatError("failed to write source code model", ex);
        }

        return true; // no further processing of this annotation type
    }

    private void writeXML(List<Element> classesFound) throws IOException {
        for (Element className : classesFound) {
        }

        File outputDir = new File(System.getProperty("basedir"), "target/generated-sources/anno");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        LOG.info("writing source code to: " + outputDir.getAbsolutePath());
    }
}
