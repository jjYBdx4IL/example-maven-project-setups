package com.github.jjYBdx4IL.jetty.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static Gson gson = createGson();

    public RequestHandler() {
        LOG.info("RequestHandler()");
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        LOG.info("{}", request);
        LOG.info("  target: {}", target);

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        Enumeration<String> attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attr = attrs.nextElement();
            LOG.info("{}: {}", attr, request.getAttribute(attr));
        }
        
        SSLSession ssl = (SSLSession) request.getAttribute("org.eclipse.jetty.servlet.request.ssl_session");
        if (ssl != null) {
            try {
                LOG.info("authenticated peer principal: {}", ssl.getPeerPrincipal().getName());
            } catch (SSLPeerUnverifiedException ex) {
                LOG.info("peer not verified");
            }
        }
        
        if ("GET".equals(request.getMethod())) {
            out.print("<h1>ehlo</h1>");
        } else if ("POST".equals(request.getMethod())) {
            out.print(request.getParameter("p"));
        } else if ("PUT".equals(request.getMethod())) {
            JsonRequest jr = gson.fromJson(request.getReader(), JsonRequest.class);
            LOG.info(jr.toString());
            gson.toJson(new JsonReply(3, jr.command + " " + String.join(",", jr.args)), response.getWriter());
        } else {
            throw new RuntimeException("unsupported method: " + request.getMethod());
        }

        baseRequest.setHandled(true);
    }

    static Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.disableHtmlEscaping();
        b.setPrettyPrinting();
        return b.create();
    }
    
    static class JsonRequest {
        public String command;
        public String[] args;
        public JsonRequest(String _command, String... _args) {
            command = _command;
            args = _args;
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("JsonRequest [command=").append(command).append(", args=").append(Arrays.toString(args))
                .append("]");
            return builder.toString();
        }
    }
    
    static class JsonReply {
        public int status;
        public String content;
        public JsonReply(int _status, String _content) {
            status = _status;
            content = _content;
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("JsonReply [status=").append(status).append(", content=").append(content).append("]");
            return builder.toString();
        }
    }
}
