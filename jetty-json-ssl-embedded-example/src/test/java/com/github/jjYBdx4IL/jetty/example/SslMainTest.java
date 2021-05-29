package com.github.jjYBdx4IL.jetty.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Test conclusions:
 * 
 * <p>setValidatePeerCerts has no effect on client side
 * 
 * <p>setValidateCerts makes server and client check their keys against the
 * configured trust store
 * 
 * <p>setValidatePeerCerts has no discernible effect at all, neither on client
 * nor on server side
 * 
 * <p>setNeedClientAuth (server): it seems impossible to have optional client
 * cert authentication in order to let the servlet decide via
 * session.getAttributes()
 * 
 * <p>There is no need to set up a CA (ie. sign certs) in order to use
 * client/server authentication via cert (both ways - the client has remote cert
 * authentication basically enabled by default). One can simply export client
 * cert from their respective key stores via keytool and then import these certs
 * into the server's trust store. Or maintain a central trust store and
 * distribute it to all participants.
 */
public class SslMainTest {

    private static final Logger LOG = LoggerFactory.getLogger(SslMainTest.class);

    public static final String url = "https://localhost:8443/";

    @BeforeClass
    public static void setupKeystore() throws Exception {
        if (Files.exists(SslMain.serverKs)) {
            return;
        }
        // generate server key and server keystore
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.serverKs.toString(),
            "-storepass", "password", "-genkey", "-alias", "server", "-dname", "CN=localhost", "-keyalg", "rsa")
                .inheritIO().start().waitFor());
        // export cert
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.serverKs.toString(),
            "-storepass", "password", "-export", "-alias", "server", "-file", SslMain.serverKs.toString() + ".cert")
                .inheritIO().start().waitFor());
        // re-import cert (key entries aren't accepted as cert entries for
        // remote authentication)
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.serverTs.toString(),
            "-storepass", "password", "-import", "-alias", "server-cert", "-file",
            SslMain.serverKs.toString() + ".cert", "-noprompt")
                .inheritIO().start().waitFor());

        // generate client key and client keystore
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.clientKs.toString(),
            "-storepass", "password", "-genkey", "-alias", "client", "-dname", "CN=client", "-keyalg", "rsa")
                .inheritIO().start().waitFor());
        // export cert
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.clientKs.toString(),
            "-storepass", "password", "-export", "-alias", "client", "-file", SslMain.clientKs.toString() + ".cert")
                .inheritIO().start().waitFor());
        // re-import cert (key entries aren't accepted as cert entries for
        // remote authentication)
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.clientTs.toString(),
            "-storepass", "password", "-import", "-alias", "client-cert", "-file",
            SslMain.clientKs.toString() + ".cert", "-noprompt")
                .inheritIO().start().waitFor());

        // trust store with both certs:
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.bothTs.toString(),
            "-storepass", "password", "-import", "-alias", "server-cert", "-file",
            SslMain.serverKs.toString() + ".cert", "-noprompt")
                .inheritIO().start().waitFor());
        assertEquals(0, new ProcessBuilder("keytool", "-keystore", SslMain.bothTs.toString(),
            "-storepass", "password", "-import", "-alias", "client-cert", "-file",
            SslMain.clientKs.toString() + ".cert", "-noprompt")
                .inheritIO().start().waitFor());
    }

    @After
    public void stopServer() throws Exception {
        int dataSize = 1024 * 1024;

        System.out.println("Used Memory   : "
            + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / dataSize + " MB");
        System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory() / dataSize + " MB");
        System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory() / dataSize + " MB");
        System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory() / dataSize + " MB");

        System.out.println("** After GC: **");
        Runtime.getRuntime().gc();

        System.out.println("Used Memory   : "
            + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / dataSize + " MB");
        System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory() / dataSize + " MB");
        System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory() / dataSize + " MB");
        System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory() / dataSize + " MB");

        SslMain.server.stop();
    }

    @Test
    public void testNoClientAuth() throws Exception {
        SslMain.main(new String[] {});

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        // sslContextFactory.setTrustAll(true);
        sslContextFactory.setTrustStorePath(SslMain.serverKs.toString());
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

    @Test
    public void test() throws Exception {
        SslMain.main(new String[] { "TRUST_CLIENT_CERTS" });

        // use client key and trust server cert:
        assertClientAccess(true, "client", SslMain.serverTs, false);
        // server not trusted:
        assertClientAccess(false, "client", SslMain.clientTs, false);
        // fails because client tries to build cert path to target:
        assertClientAccess(false, "client", null, false);
        // trust server:
        assertClientAccess(true, "server", SslMain.serverTs, false);
        assertClientAccess(true, "server", null, false);
    }

    // validate peer certs on server side seems to have no effect:
    @Test
    public void testValidatePeerCerts() throws Exception {
        SslMain.main(new String[] { "TRUST_CLIENT_CERTS", "VALIDATE_PEER_CERTS" });

        // use client key and trust server cert:
        assertClientAccess(true, "client", SslMain.serverTs, false);
        // server not trusted:
        assertClientAccess(false, "client", SslMain.clientTs, false);
        // fails because client tries to build cert path to target:
        assertClientAccess(false, "client", null, false);
        // trust server:
        assertClientAccess(true, "server", SslMain.serverTs, false);
        assertClientAccess(true, "server", null, false);
    }

    @Test
    public void testNeedClientAuth() throws Exception {
        SslMain.main(new String[] { "TRUST_CLIENT_CERTS", "NEED_CLIENT_AUTH" });

        // use client key and trust server cert:
        assertClientAccess(true, "client", SslMain.serverTs, false);
        // use invalid trust settings on client side
        assertClientAccess(false, "client", SslMain.clientTs, false);
        assertClientAccess(false, "client", null, false);
        // use server key
        assertClientAccess(false, "server", SslMain.serverTs, false);
        // use server key
        assertClientAccess(false, "server", null, false);
    }

    @Test
    public void testNeedClientAuthAndValidatePeer() throws Exception {
        SslMain.main(new String[] { "VALIDATE_PEER_CERTS", "TRUST_CLIENT_CERTS", "NEED_CLIENT_AUTH" });

        // use client key and trust server cert:
        assertClientAccess(true, "client", SslMain.serverTs, false);
        // use invalid trust settings on client side
        assertClientAccess(false, "client", SslMain.clientTs, false);
        assertClientAccess(false, "client", null, false);
        // use server key
        assertClientAccess(false, "server", SslMain.serverTs, false);
        // use server key
        assertClientAccess(false, "server", null, false);
    }

    @Test
    public void testSharedKeyAndCert() throws Exception {
        SslMain.main(new String[] { "TRUST_SERVER_CERTS", "NEED_CLIENT_AUTH" });

        assertClientAccess(true, "server", SslMain.serverTs, false);
    }

    @Test
    public void testServerValidateCerts() throws Exception {
        SslMain.main(new String[] { "VALIDATE_CERTS", "TRUST_SERVER_CERTS" });

        assertClientAccess(true, "server", SslMain.serverTs, false);
    }

    @Test(expected = CertificateException.class)
    public void testServerValidateCertsFail() throws Exception {
        SslMain.main(new String[] { "VALIDATE_CERTS", "TRUST_CLIENT_CERTS" });
    }

    @Test
    public void testClientValidateCerts() throws Exception {
        SslMain.main(new String[] { "TRUST_CLIENT_CERTS" });

        // fails because there is no client cert in the client's trust store:
        assertClientAccess(false, "client", SslMain.serverTs, true);
        // fails because auf missing server cert on client side:
        assertClientAccess(false, "client", SslMain.clientTs, true);
        assertClientAccess(true, "client", SslMain.bothTs, true);
    }

    public void assertClientAccess(boolean expSuccess, String alias, Path truststore, boolean validateCerts)
        throws Exception {
        Path keystore;
        if ("client".equals(alias)) {
            keystore = SslMain.clientKs;
        } else if ("server".equals(alias)) {
            keystore = SslMain.serverKs;
        } else {
            throw new RuntimeException();
        }

        SslContextFactory.Client ssl = new SslContextFactory.Client();
        assertFalse(ssl.isValidateCerts());
        assertFalse(ssl.isValidatePeerCerts());

        ssl.setValidateCerts(validateCerts);
        // sslContextFactory.setTrustAll(true);

        if (truststore != null) {
            ssl.setValidatePeerCerts(true);
            ssl.setTrustStorePath(truststore.toString());
            ssl.setTrustStorePassword("password");
        } else {
            // disable remote cert verification completely if not using a
            // truststore:
            ssl.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    LOG.info("verifying: {}", hostname);
                    return true;
                }
            });
        }

        if (alias != null) {
            ssl.setCertAlias(alias);
        }
        if (keystore != null) {
            ssl.setKeyStorePath(keystore.toString());
            ssl.setKeyStorePassword("password");
        }

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(ssl);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));

        ContentResponse response = null;
        try {
            httpClient.start();
            response = httpClient.GET(url);
        } catch (Exception ex) {
            if (expSuccess) {
                LOG.error("", ex);
            }
        }
        if (!expSuccess) {
            assertNull(response);
            return;
        }
        assertNotNull(response);
        assertEquals("<h1>ehlo</h1>", response.getContentAsString());
    }

    public void dumpKeystore() throws Exception {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        assertNotNull(store);
        try (InputStream is = new FileInputStream(SslMain.serverKs.toFile())) {
            store.load(is, "password".toCharArray());
        }
        Enumeration<String> aliases = store.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            LOG.info("alias: {}", alias);
            LOG.info("is cert: {}", store.isCertificateEntry(alias));
            LOG.info("is key: {}", store.isKeyEntry(alias));
        }
    }
}
