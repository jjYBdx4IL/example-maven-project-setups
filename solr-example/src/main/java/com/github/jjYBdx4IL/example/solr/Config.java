package com.github.jjYBdx4IL.example.solr;

import com.github.jjYBdx4IL.utils.solr.beans.FieldConfig;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static final int ZK_PORT = 2181;
    public static final String COLLECTION = "gettingstarted";
    public static final Object COLLECTION_CFGNAME = "myconfig";
    public static final boolean IS_CLUSTERED = portIsOpen(ZK_PORT);
    public static final String SOLR_COLLECTION_URL = "http://127.0.0.1:8983/solr/" + COLLECTION;

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
            LOG.info("connect: " + SOLR_COLLECTION_URL);
            HttpSolrClient httpSolr = new HttpSolrClient.Builder(SOLR_COLLECTION_URL).build();
            httpSolr.setParser(new XMLResponseParser());
            return httpSolr;
        }
    }

    public static boolean portIsOpen(final int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", port), 1000);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
