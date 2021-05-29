package com.github.jjYBdx4IL.jetty.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.jetty.example.RequestHandler.JsonReply;
import com.github.jjYBdx4IL.jetty.example.RequestHandler.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request.Content;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.URL;

import jakarta.ws.rs.core.MediaType;

public class MainTest {

    public static final String url = "http://localhost:8081/";
    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);
    private static final Gson gson = createGson();
    static Thread t;

    @BeforeClass
    public static void beforeClass() throws InterruptedException, IOException {
        t = new Thread("http server") {
            @Override
            public void run() {
                try {
                    Main.main(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        t.start();
        waitForServer();
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
        HttpClient httpClient = new HttpClient();
        httpClient.start();

        ContentResponse response = httpClient.GET(url);
        assertEquals("<h1>ehlo</h1>", response.getContentAsString());
        assertEquals("text/html", response.getHeaders().get(HttpHeader.CONTENT_TYPE));

        response = httpClient.POST(url).param("p", "value").send();
        assertEquals("value", response.getContentAsString());
        assertEquals("text/plain", response.getHeaders().get(HttpHeader.CONTENT_TYPE));
    }
    
    @Test
    public void testJson() throws Exception {
        HttpClient httpClient = new HttpClient();
        
        httpClient.start();
        
        JsonRequest jr = new JsonRequest("mycmd", "arg1", "arg2");
        String json = gson.toJson(jr);
        LOG.info("json: {}", json);
        Content content = new StringRequestContent(MediaType.APPLICATION_JSON, json);
        
        
        ContentResponse response = httpClient.newRequest(url).method(HttpMethod.PUT).body(content).send();
        LOG.info("reply: {}", response.getContentAsString());
        
        JsonReply reply = gson.fromJson(response.getContentAsString(), JsonReply.class);
        LOG.info(reply.toString());
        
        assertEquals(3, reply.status);
        assertEquals("mycmd arg1,arg2", reply.content);
    }
    
    public static void waitForServer() throws InterruptedException, IOException {
        URL u = new URL(url);
        String r = null;
        do {
            try {
                r = IOUtils.toString(u, UTF_8);
            } catch (ConnectException e) {
                Thread.sleep(100);
            }
        } while (r == null);
    }

    static Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.disableHtmlEscaping();
        b.setPrettyPrinting();
        return b.create();
    }
}
