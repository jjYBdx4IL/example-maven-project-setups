package com.github.jjYBdx4IL.maven.examples.aspectj.fieldaccess;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * from: https://eclipse.org/aspectj/doc/next/adk15notebook/ataspectj-pcadvice.html
 *
 * @author jjYBdx4IL
 */
@Aspect
public class FieldAccessAspect {

    private static final Logger LOG = LoggerFactory.getLogger(FieldAccessAspect.class);

    @Pointcut("get(* com.github.jjYBdx4IL.maven.examples.aspectj.fieldaccess.FieldAccessServlet.message)")
    public void fieldAccess() {
    }

    @AfterReturning(pointcut = "fieldAccess()", returning = "field")
    public void afterFieldAccess(Object field, JoinPoint thisJoinPoint) {
        LOG.info(thisJoinPoint.toLongString());
        LOG.info("  " + thisJoinPoint.getSignature().getName());
        LOG.info("  " + field);
    }
    
    @Around("fieldAccess()")
    public Object doNothing4(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        LOG.info(">>> around field access");
        thisJoinPoint.proceed();
        LOG.info(">>> replacing field get return value");
        return "Load-time weaving works with jetty!";
    }
    
}
