package com.github.jjYBdx4IL.jetty.example;

import static org.junit.Assert.assertEquals;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.Thread.UncaughtExceptionHandler;

public class SslMainTest {

    public static final String url = "https://localhost:8443/";
    static Thread t;

    @BeforeClass
    public static void beforeClass() {
        t = new Thread("https server") {
            @Override
            public void run() {
                try {
                    SslMain.main(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        t.start();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        int dataSize = 1024 * 1024;

        System.out.println("Used Memory   : "
            + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / dataSize + " MB");
        System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory() / dataSize + " MB");
        System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory() / dataSize + " MB");
        System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory() / dataSize + " MB");
        
        t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
            }
        });
        t.interrupt();
    }

    @Test
    public void testMain() throws Exception {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        //sslContextFactory.setTrustAll(true);
        sslContextFactory.setTrustStorePath(SslMain.keystoreLoc.toString());
        sslContextFactory.setTrustStorePassword("password");

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(sslContextFactory);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.start();        

        ContentResponse response = httpClient.GET(url);
        assertEquals("<h1>ehlo</h1>", response.getContentAsString());

        response = httpClient.POST(url).param("p", "value").send();
        assertEquals("value", response.getContentAsString());
    }
}
