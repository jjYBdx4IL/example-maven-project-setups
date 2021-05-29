package com.github.jjYBdx4IL.jetty.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler() {
        LOG.info("constructor: RequestHandler()");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String handleGet() {
        LOG.info("handleGet()");
        return "<h1>ehlo</h1>";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String handlePost(@QueryParam("p") String p) {
        LOG.info("handlePost() p={}", p);
        return p;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handlePut(JsonRequest jr) {
        LOG.info("handlePut()");
        return Response.ok(new JsonReply(3, jr.command + " " + String.join(",", jr.args))).build();
    }

    static class JsonRequest {
        public String command;
        public String[] args;

        public JsonRequest() {
        }

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

        public JsonReply() {
        }

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
