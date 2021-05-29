package com.github.jjYBdx4IL.jetty.example;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

import jakarta.ws.rs.core.UriBuilder;

public class Main {

    // https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest3x/deployment.html#d0e3123
    // "Example 4.8. Using Jersey with Jetty HTTP Server"
    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8081).build();
        ResourceConfig config = new ResourceConfig() {
            {
                packages(Main.class.getPackageName());
            }
        };
        //ResourceConfig config = new ResourceConfig(RequestHandler.class);
        Server server = JettyHttpContainerFactory.createServer(baseUri, config);

        //server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);

        server.start();
        server.join();
    }
}
