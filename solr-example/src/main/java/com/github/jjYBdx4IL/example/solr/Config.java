package com.github.jjYBdx4IL.example.solr;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class Config {
    public static final int ZK_PORT = 2181;
    public static final String COLLECTION = "gettingstarted";
    public static final Object COLLECTION_CFGNAME = "myconfig";

    public static CloudSolrClient createCloudClient() {
        String zkHostString = "localhost:" + ZK_PORT + ",localhost:" + (ZK_PORT + 1) + ",localhost:"
            + (ZK_PORT + 2);
        CloudSolrClient cloudSolr = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
        cloudSolr.setDefaultCollection(COLLECTION);
        cloudSolr.setParser(new XMLResponseParser());
        return cloudSolr;
    }
}
