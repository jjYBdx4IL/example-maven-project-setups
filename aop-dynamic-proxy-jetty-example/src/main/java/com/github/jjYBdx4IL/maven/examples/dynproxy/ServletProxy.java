package com.github.jjYBdx4IL.maven.examples.dynproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ServletProxy implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServletProxy.class);
    
    private final Servlet obj;

    public static Servlet newInstance(Servlet obj) {
        return (Servlet) Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                new Class<?>[]{Servlet.class},
                new ServletProxy(obj));
    }

    private ServletProxy(Servlet obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result;
        try {
            LOG.info("before method " + m.getName());
            result = m.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("unexpected invocation exception: "
                    + e.getMessage());
        } finally {
            LOG.info("after method " + m.getName());
        }
        return result;
    }
}
