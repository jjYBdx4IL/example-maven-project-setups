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

    public static final Path serverKs = Paths.get(System.getProperty("project.build.directory", "./target"),
        "keystore.server");
    public static final Path serverTs = Paths.get(System.getProperty("project.build.directory", "./target"),
        "truststore.server");
    public static final Path clientKs = Paths.get(System.getProperty("project.build.directory", "./target"),
        "keystore.client");
    public static final Path clientTs = Paths.get(System.getProperty("project.build.directory", "./target"),
        "truststore.client");
    public static final Path bothTs = Paths.get(System.getProperty("project.build.directory", "./target"),
        "truststore.both");
    
    public static Server server = new Server();

    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().gc();

        // https://examples.javacodegeeks.com/enterprise-java/jetty/jetty-ssl-configuration-example/
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server ssl = new SslContextFactory.Server();

        ssl.setCertAlias("server");
        ssl.setKeyStorePath(serverKs.toString());
        ssl.setKeyStorePassword("password");

        for (String arg : args) {
            if ("VALIDATE_CERTS".equals(arg)) {
                ssl.setValidateCerts(true);
            }
            else if ("VALIDATE_PEER_CERTS".equals(arg)) {
                ssl.setValidatePeerCerts(true);
            }
            else if ("NEED_CLIENT_AUTH".equals(arg)) {
                ssl.setNeedClientAuth(true);
            }
            else if ("TRUST_CLIENT_CERTS".equals(arg)) {
                ssl.setTrustStorePath(clientTs.toString());
                ssl.setTrustStorePassword("password");
            }
            else if ("TRUST_SERVER_CERTS".equals(arg)) {
                ssl.setTrustStorePath(serverTs.toString());
                ssl.setTrustStorePassword("password");
            } else if ("TRUST_BOTH_CERTS".equals(arg)) {
                ssl.setTrustStorePath(bothTs.toString());
                ssl.setTrustStorePassword("password");
            } else {
                throw new RuntimeException("invalid parameter: " + arg);
            }
        }

        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(ssl, "http/1.1"),
            new HttpConnectionFactory(https));
        sslConnector.setPort(8443);

        server.setConnectors(new Connector[] { sslConnector });

        server.setHandler(new RequestHandler());

        server.start();
    }
}
