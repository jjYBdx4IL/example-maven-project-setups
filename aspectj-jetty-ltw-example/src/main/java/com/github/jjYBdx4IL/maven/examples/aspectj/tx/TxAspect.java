package com.github.jjYBdx4IL.maven.examples.aspectj.tx;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@Aspect
public class TxAspect {

    private static final Logger LOG = LoggerFactory.getLogger(TxAspect.class);

    @Pointcut("execution(void javax.servlet.GenericServlet+.init())")
    public void servletInit() {
    }

    @Pointcut("execution(void javax.servlet.GenericServlet+.destroy())")
    public void servletDestroy() {
    }

    @Pointcut("within(@com.github.jjYBdx4IL.maven.examples.aspectj.Tx *)")
    public void classAnnotatedWithTx() {
    }

    @Pointcut("get(String *..message)")
    public void messageFieldAccess() {
    }

    @Pointcut("classAnnotatedWithTx() && messageFieldAccess()")
    public void messageFieldAccessInAnnotatedClass() {
    }

    @Pointcut("classAnnotatedWithTx() && servletInit()")
    public void servletInitInAnnotatedClass() {
    } 

    @Pointcut("classAnnotatedWithTx() && servletDestroy()")
    public void servletDestroyInAnnotatedClass() {
    }

    @Around("messageFieldAccessInAnnotatedClass()")
    public Object doNothing4(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        thisJoinPoint.proceed();
        LOG.info("injecting message");
        return "Load-time weaving works with jetty!";
    }

    @After("servletInitInAnnotatedClass()")
    public void afterServletInit() {
        LOG.info("after servlet init");
    }

    @Before("servletDestroyInAnnotatedClass()")
    public void beforeServletDestroy() {
        LOG.info("before servlet destroy");
    }

    @Around("execution(@com.github.jjYBdx4IL.maven.examples.aspectj.Tx * *..*(..))")
    public Object handleTx(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        LOG.info("handle tx");
        // start tx

        Object result = thisJoinPoint.proceed();

        // commit or rollback tx dependening on exception thrown or not
        return result;
    }
    
}
