package com.github.jjYBdx4IL.jetty.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.ws.rs.core.UriBuilder;

public class SslMain {

    public static final Path keystoreLoc = Paths.get(System.getProperty("keystore.loc", "./target/keystore"));

    public static void main(String[] args) throws Exception {
        SslContextFactory.Server ssl = new SslContextFactory.Server();
        ssl.setCertAlias("server");
        ssl.setKeyStorePath(keystoreLoc.toString());
        ssl.setKeyStorePassword("password");
        
        URI baseUri = UriBuilder.fromUri("https://localhost/").port(8443).build();
        ResourceConfig config = new ResourceConfig() {
            {
                packages(Main.class.getPackageName());
            }
        };
        //ResourceConfig config = new ResourceConfig(RequestHandler.class);
        Server server = JettyHttpContainerFactory.createServer(baseUri, ssl, config);

        server.start();
        server.join();
    }
}
