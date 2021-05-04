package com.github.jjYBdx4IL.maven.examples.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * from: https://eclipse.org/aspectj/doc/next/adk15notebook/ataspectj-pcadvice.html
 *
 * @author jjYBdx4IL
 */
@Aspect
public class ContentGeneratorAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ContentGeneratorAspect.class);

    @Around("execution(* com.github.jjYBdx4IL.maven.examples.aspectj.ContentGenerator.getContent(..))")
    public Object doNothing(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        LOG.info(">>> around ContentGenerator.getContent()");
        thisJoinPoint.proceed();
        return "Load-time weaving works with jetty!";
    }
    
    @Before("execution(* com.github.jjYBdx4IL.maven.examples.aspectj.ContentServlet.init())")
    public void doNothing2() {
        LOG.info(">>> before servlet init");
    }
    
    @Before("execution(* com.github.jjYBdx4IL.maven.examples.aspectj.ContentServlet.doGet(..)) && this(foo)")
    public void doNothing3(ISetMessage foo) throws Throwable {
        LOG.info(">>> before servlet doGet");
        foo.setMessage("Load-time weaving works with jetty!");
    }
    
    @Around("@annotation(com.github.jjYBdx4IL.maven.examples.aspectj.Tx) && execution(* com.github.jjYBdx4IL..*(..))")
    public Object doNothing4(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        LOG.info(">>> around @Tx");
        thisJoinPoint.proceed();
        return "Load-time weaving works with jetty!";
    }
}
