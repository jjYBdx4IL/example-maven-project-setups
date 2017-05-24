package com.github.jjYBdx4IL.maven.examples.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * from: http://www.mojohaus.org/aspectj-maven-plugin/multimodule/multimodule_strategy.html
 *
 * @author jjYBdx4IL
 */
@Aspect
public class ValidatableAspect {

    private static final Logger LOG = LoggerFactory.getLogger(Aspect.class);

    /**
     * Pointcut defining a default constructor within any class.
     */
    @Pointcut("initialization(*.new())")
    void anyDefaultConstructor() {
    }

    /**
     * Defines a Pointcut for any constructor in a class implementing Validatable - except default constructors (i.e.
     * those having no arguments).
     *
     * @param joinPoint The currently executing joinPoint.
     * @param aValidatable The Validatable instance just created.
     */
    @Pointcut(value = "initialization(com.github.jjYBdx4IL.maven.examples.aspectj.Validatable+.new(..)) "
            + "&& this(aValidatable) "
            + "&& !anyDefaultConstructor()", argNames = "joinPoint, aValidatable")
    void anyNonDefaultConstructor(final JoinPoint joinPoint, final Validatable aValidatable) {
    }

    /**
     * Validation aspect, performing its job after calling any constructor except non-private default ones (having no
     * arguments).
     *
     * @param joinPoint The currently executing joinPoint.
     * @param validatable The validatable instance just created.
     * @throws InternalStateValidationException if the validation of the validatable failed.
     */
    @AfterReturning(value = "anyNonDefaultConstructor(joinPoint, validatable)", argNames = "joinPoint, validatable")
    public void performValidationAfterCompoundConstructor(final JoinPoint joinPoint, final Validatable validatable)
            throws InternalStateValidationException {

        LOG.info("Validating instance of type [" + validatable.getClass().getName() + "]");

        if (joinPoint.getStaticPart() == null) {
            LOG.warn("Static part of join point was null for validatable of type: "
                    + validatable.getClass().getName(), new IllegalStateException());
            return;
        }

        // Ignore calling validateInternalState when we execute constructors in
        // any class but the concrete Validatable class.
        final ConstructorSignature sig = (ConstructorSignature) joinPoint.getSignature();
        final Class<?> constructorDefinitionClass = sig.getConstructor().getDeclaringClass();
        if (validatable.getClass() == constructorDefinitionClass) {

            // Now fire the validateInternalState method.
            validatable.validateInternalState();
        } else {

            LOG.info("Ignored firing validatable for constructor defined in ["
                    + constructorDefinitionClass.getName() + "] and Validatable of type ["
                    + validatable.getClass().getName() + "]");
        }
    }
}
