package com.github.jjYBdx4IL.jetty.example;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SslMain {

    public static final Path keystoreLoc = Paths.get(System.getProperty("keystore.loc", "./target/keystore"));

    public static void main(String[] args) throws Exception {
        Server server = new Server();

        // https://examples.javacodegeeks.com/enterprise-java/jetty/jetty-ssl-configuration-example/
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server ssl = new SslContextFactory.Server();
        ssl.setCertAlias("server");
        
        ssl.setKeyStorePath(keystoreLoc.toString());
        ssl.setKeyStorePassword("password");
        
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(ssl, "http/1.1"),
            new HttpConnectionFactory(https));
        sslConnector.setPort(8443);

        server.setConnectors(new Connector[] { sslConnector });

        server.setHandler(new RequestHandler());

        server.start();
        server.join();
    }
}
