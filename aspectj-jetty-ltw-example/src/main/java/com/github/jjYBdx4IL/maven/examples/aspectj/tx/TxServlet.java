package com.github.jjYBdx4IL.maven.examples.aspectj.tx;

import com.github.jjYBdx4IL.maven.examples.aspectj.Tx;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here is the masterplan on how to add TX support to GenericServlets via AOP/AspectJ:
 * 
 * We keep one EMF per PU in use.
 * 
 * Weave init() and destroy() methods. Before (GenericServlet's) init() method gets called, we add the servlet context
 * as a reference to keep track of when to close an EMF. Respectively, we remove the reference after destroy().
 * 
 * The servlet classes get identified by the @Tx annotation and must be a subtype of GenericServlet. 
 * 
 * EntityManagers get initialized on a per thread and servlet context basis before the GenericServlet's service() method is called.
 * Methods annotated with @Tx will be provided with a running transaction.
 * If the method throws an exception, a rollback will be performed, otherwise a commit.
 * EntityManager and transaction objects are stored in the servlet context via ThreadLocal.
 * 
 * The servlet context attribute names are "JPAEntityManager" and "JPATransaction".
 * 
 * EntityManagers will be re-used on the same thread, and never get explicitly closed -- except indirectly through
 * the EMF close operation at servlet shutdown (destroy()).
 * 
 * For convenience, the @Tx annotation may be put on a field of type EntityManager, and the EntityManager will be
 * injected into this field.
 * 
 * 
 * 
 * @author jjYBdx4IL
 */
@Tx
public class TxServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TxServlet.class);
    
    private String message = "Load-time weaving failed!";
   
    @Override
    public void init() throws ServletException {
        LOG.info("ContentServlet.init()");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.getWriter().append(message);
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

}
