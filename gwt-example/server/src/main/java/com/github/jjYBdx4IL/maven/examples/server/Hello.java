package com.github.jjYBdx4IL.maven.examples.server;

import com.github.jjYBdx4IL.aop.tx.Tx;
import com.github.jjYBdx4IL.aop.tx.TxReadOnly;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Tx
@Path("/hello")
@Singleton // needed to avoid entity manager leak with TX manager because the TX
           // manager assumes GenericServlet's destroy method for cleanup
public class Hello {

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @TxReadOnly
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    @TxReadOnly
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    @TxReadOnly
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "Hello Jersey" + "</body></h1>"
                + "</html> ";
    }

}