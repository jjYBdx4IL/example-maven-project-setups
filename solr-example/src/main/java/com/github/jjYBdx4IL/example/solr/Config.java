package com.github.jjYBdx4IL.example.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Config {
    
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);
    
    public static final int ZK_PORT = 2181;
    public static final String COLLECTION = "gettingstarted";
    public static final Object COLLECTION_CFGNAME = "myconfig";
    public static final boolean IS_CLUSTERED = portIsOpen(ZK_PORT);

    public static SolrClient createClient() {
        LOG.info("is clustered: " + IS_CLUSTERED);

        if (IS_CLUSTERED) {
            String zkHostString = "localhost:" + ZK_PORT + ",localhost:" + (ZK_PORT + 1) + ",localhost:"
                + (ZK_PORT + 2);
            LOG.info("connect: " + zkHostString);
            CloudSolrClient cloudSolr = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
            cloudSolr.setDefaultCollection(COLLECTION);
            cloudSolr.setParser(new XMLResponseParser());
            return cloudSolr;
        } else {
            String urlString = "http://localhost:8983/solr/" + COLLECTION;
            LOG.info("connect: " + urlString);
            HttpSolrClient httpSolr = new HttpSolrClient.Builder(urlString).build();
            httpSolr.setParser(new XMLResponseParser());
            return httpSolr;
        }
    }

    public static boolean portIsOpen(final int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", port), 1000);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
