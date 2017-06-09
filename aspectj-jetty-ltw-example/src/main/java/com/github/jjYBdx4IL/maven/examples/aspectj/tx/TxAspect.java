package com.github.jjYBdx4IL.maven.examples.aspectj.tx;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.aspectj.utils.AspectJWeaveConfig;
import com.github.jjYBdx4IL.maven.examples.aspectj.Tx;

/**
 *
 * @author jjYBdx4IL
 */
@Aspect
@AspectJWeaveConfig(
		includesWithin = {
				"javax.servlet.GenericServlet"},
		showWeaveInfo = true,
		verbose = true,
		weaveJavaxPackages = true
)
public class TxAspect {

    private static final Logger LOG = LoggerFactory.getLogger(TxAspect.class);

    @Pointcut("execution(void javax.servlet.GenericServlet+.init(..))")
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

    @Pointcut("get(@com.github.jjYBdx4IL.maven.examples.aspectj.tx.TxInject String *..*)")
    public void fieldInjectionViaAnnotation() {
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

    @Around("fieldInjectionViaAnnotation() && classAnnotatedWithTx() && this(foo)")
    public Object injectIntoFieldViaAnnotation(ProceedingJoinPoint thisJoinPoint, Object foo) throws Throwable {
        thisJoinPoint.proceed();
        LOG.info("injecting message via annotation");
        return "Load-time weaving works with jetty and field annotations! " + foo;
    }
    
    @After("servletInitInAnnotatedClass()")
    public void afterServletInit() {
        LOG.info("after servlet init");
    }

    @Before("servletDestroyInAnnotatedClass()")
    public void beforeServletDestroy() {
        LOG.info("before servlet destroy");
    }

    @Around("execution(* *..TxServlet.*(..)) && this(foo)")
    public Object handleTx(ProceedingJoinPoint thisJoinPoint, Object foo) throws Throwable {
        LOG.info("handle tx, isAnnotationPresent(Tx): " + foo.getClass().isAnnotationPresent(Tx.class));
        // start tx

        Object result = thisJoinPoint.proceed();

        // commit or rollback tx dependening on exception thrown or not
        return result;
    }
    
    // the within(..) statement prevents multiple executions when the GenericServet's init() method gets overridden
    // and the override does a super.init() call. On the downside, it *must* do the super.init() call for this to work...
    @After("execution(* init()) && @this(tx) && within(javax.servlet.GenericServlet) && this(foo)")
    public void handleAfterInit(Tx tx, Object foo) {
    	LOG.info("handleAfterInit, foo = " + foo);
        LOG.info("handleAfterInit, isAnnotationPresent(Tx): " + foo.getClass().isAnnotationPresent(Tx.class));
    }
}
