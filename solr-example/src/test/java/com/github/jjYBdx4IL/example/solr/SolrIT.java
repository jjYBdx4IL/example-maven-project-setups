package com.github.jjYBdx4IL.example.solr;

import static org.junit.Assert.assertEquals;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

// https://lucene.apache.org/solr/guide/7_0/using-solrj.html
// https://github.com/apache/lucene-solr/tree/master/solr/solrj/src/test/org/apache/solr/client/solrj
// http://www.baeldung.com/apache-solrj
public class SolrIT {

    private static final Logger LOG = LoggerFactory.getLogger(SolrIT.class);

    SolrClient solr = null;
    static final int ZK_PORT = 2181;
    static boolean clustered = portIsOpen(ZK_PORT);
    static final String COLLECTION = "gettingstarted";

    @Before
    public void before() throws Exception {
        LOG.info("is clustered: " + clustered);

        if (clustered) {
            String zkHostString = "localhost:" + ZK_PORT + ",localhost:" + (ZK_PORT + 1) + ",localhost:"
                + (ZK_PORT + 2);
            CloudSolrClient cloudSolr = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
            cloudSolr.setDefaultCollection(COLLECTION);
            cloudSolr.setParser(new XMLResponseParser());
            solr = cloudSolr;
        } else {
            String urlString = "http://localhost:8983/solr/" + COLLECTION;
            HttpSolrClient httpSolr = new HttpSolrClient.Builder(urlString).build();
            httpSolr.setParser(new XMLResponseParser());
            solr = httpSolr;
        }
    }

    @Test
    public void testCRUD() throws Exception {
        // delete by query
        solr.deleteByQuery("*");

        // add a document
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "123456"); // re-using the same id replaces the
                                           // previous document
        document.addField("name", "Kenmore Dishwasher");
        document.addField("price", "599.99");
        solr.add(document);

        // add a document by using a bean
        solr.addBean(new ProductBean("888", "Apple iPhone 6s", "299.99"));
        commit();

        // query by field
        SolrQuery query = new SolrQuery();
        query.set("q", "price:599.99");
        QueryResponse response = solr.query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);

        assertEquals((String) docList.get(0).getFieldValue("id"), "123456");
        assertEquals((Double) docList.get(0).getFieldValue("price"), (Double) 599.99);

        // query by id
        SolrDocument doc = solr.getById("123456");
        assertEquals((String) doc.getFieldValue("name"), "Kenmore Dishwasher");
        assertEquals((Double) doc.getFieldValue("price"), (Double) 599.99);

        // delete by query
        solr.deleteByQuery("name:\"Kenmore Dishwasher\"");
        commit();

        // delete by id
        solr.deleteById("123456");
        commit();
        query = new SolrQuery();
        query.set("q", "id:123456");
        response = solr.query(query);
        docList = response.getResults();
        assertEquals(docList.getNumFound(), 0);
    }

    @Test
    public void testSearchSyntax() throws Exception {
        // prepare the dataset
        solr.deleteByQuery("*");

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "12345");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "one two three four five");
        solr.add(document);

        document = new SolrInputDocument();
        document.addField("id", "123457");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "six eighty");
        solr.add(document);

        document = new SolrInputDocument();
        document.addField("id", "1234578");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "seven eight");
        solr.add(document);
        commit();

        // http://www.solrtutorial.com/solr-query-syntax.html
        assertQueryMatchCount(1, "text:three");
        assertQueryMatchCount(1, "text:thre*");
        assertQueryMatchCount(0, "text:thre");
        assertQueryMatchCount(0, "text:nine");
        assertQueryMatchCount(1, "text:t*");
        assertQueryMatchCount(2, "text:s*");
        assertQueryMatchCount(1, "text:*hre*");
        assertQueryMatchCount(1, "text:t*ree");
        assertQueryMatchCount(1, "text:\"one two\"");
        assertQueryMatchCount(0, "text:\"two one\"");
        assertQueryMatchCount(0, "text:one -text:two");
        assertQueryMatchCount(2, "text:seven OR text:six");
        assertQueryMatchCount(1, "text:[f TO fz]");
        assertQueryMatchCount(3, "text:[* TO fz]");
        assertQueryMatchCount(2, "text:eighty~1"); // fuzzy search (edit
                                                   // distance)
        assertQueryMatchCount(1, "text:eightyy~1");
        assertQueryMatchCount(2, "text:eightyy~2");

        assertQueryMatchCount(1, "text:\"eighty\"~0"); // exact word/phrase
                                                       // match
        // remark: there seems to be no way to match the entire contents of a
        // field
    }

    @Test
    public void testDeleteByQuery() throws Exception {
        // prepare the dataset
        solr.deleteByQuery("*");
        commit();

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "a12345");
        document.addField("name", "testDeleteByQuery");
        document.addField("text", "one");
        solr.add(document);
        commit();

        document = new SolrInputDocument();
        document.addField("id", "a123457");
        document.addField("name", "testDeleteByQuery ABC");
        document.addField("text", "one");
        solr.add(document);
        commit();

        assertQueryMatchCount(2, "text:\"one\"~0");

        // matches are always word-based, therefore the following query will
        // delete both documents:
        solr.deleteByQuery("name:\"testDeleteByQuery\"~0");
        commit();

        assertQueryMatchCount(0, "text:\"one\"~0");
    }

    // used for manual cluster failover tests
    @Test
    public void testAddDocument() throws Exception {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "b12345" + System.currentTimeMillis());
        document.addField("name", "testAddDocument");
        document.addField("text", "one");
        solr.add(document);
        commit();

        dumpQueryMatchCount("name:testAddDocument~0");
    }

    public void assertQueryMatchCount(int count, String queryText) throws Exception {
        SolrQuery query = new SolrQuery();
        query.set("q", queryText);
        QueryResponse response = solr.query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(count, docList.getNumFound());
    }

    public void dumpQueryMatchCount(String queryText) throws Exception {
        SolrQuery query = new SolrQuery();
        query.set("q", queryText);
        QueryResponse response = solr.query(query);

        SolrDocumentList docList = response.getResults();
        LOG.info("query: " + queryText + ", matches: " + docList.size());
    }

    public void commit() throws SolrServerException, IOException {
        solr.commit(true, true, true);
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
