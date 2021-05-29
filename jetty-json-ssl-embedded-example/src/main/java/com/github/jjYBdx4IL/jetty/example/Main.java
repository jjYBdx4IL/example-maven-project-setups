package com.github.jjYBdx4IL.jetty.example;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;

public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);
        server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
        server.setHandler(new RequestHandler());

        server.start();
        server.join();
    }
}
