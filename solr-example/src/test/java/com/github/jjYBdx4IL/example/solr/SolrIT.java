package com.github.jjYBdx4IL.example.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.example.solr.beans.ProductBean;
import com.github.jjYBdx4IL.utils.solr.ClusterStatusResponse;
import com.github.jjYBdx4IL.utils.solr.SolrUtils;
import com.github.jjYBdx4IL.utils.solr.ClusterStatusResponse.CollectionStatus;
import com.github.jjYBdx4IL.utils.solr.ClusterStatusResponse.RedundancyState;
import com.github.jjYBdx4IL.utils.solr.ClusterStatusResponse.RedundancyStatus;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.ClusterStatus;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

// https://lucene.apache.org/solr/guide/7_0/using-solrj.html
// https://github.com/apache/lucene-solr/tree/master/solr/solrj/src/test/org/apache/solr/client/solrj
// http://www.baeldung.com/apache-solrj
// https://github.com/apache/lucene-solr/blob/master/solr/solrj/src/test/org/apache/solr/client/solrj/request/SchemaTest.java
public class SolrIT {

    private static final Logger LOG = LoggerFactory.getLogger(SolrIT.class);

    SolrClient solr = null;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (Config.IS_CLUSTERED) {
            SolrUtils.verifyOrEnableAutoCommit(Config.SOLR_COLLECTION_URL);
        }
        SolrUtils.verifyOrDisableAutoCreateFields(Config.SOLR_COLLECTION_URL);
        try (SolrClient client = Config.createClient()) {
            SolrUtils.verifyOrCreateSchema(client, ProductBean.class);
        }
    }

    @Before
    public void before() throws Exception {
        solr = Config.createClient();
    }

    @After
    public void after() {
        IOUtils.closeQuietly(solr);
    }

    @Test
    public void testAddBean() throws IOException, SolrServerException {
        solr.addBean(new ProductBean("888", "Apple iPhone 6s", 299.99));
        commit();
    }

    @Test
    public void testIdField() throws Exception {
        // delete by query
        solr.deleteByQuery("*");

        // add a document
        SolrInputDocument document = new SolrInputDocument();
        document.addField("name", "someRandomStuffTestIdField");
        document.addField("price", "512.34");
        try {
            solr.add(document);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("Document is missing mandatory uniqueKey field"));
        }
    }

    @Test
    public void testNotQuery() throws Exception {
        // delete by query
        solr.deleteByQuery("*");

        // add a document
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1");
        document.addField("name", "someRandomStuffTestIdField");
        solr.add(document);
        document = new SolrInputDocument();
        document.addField("id", "2");
        document.addField("name", "someRandomStuffTestIdField");
        document.addField("price", 1.23);
        solr.add(document);
        commit();
        
        assertQueryMatchCount(2, "NOT price:*");
        assertQueryMatchCount(2, "*:* NOT price:*");
        assertQueryMatchCount(2, "(*:* NOT price:*)");
        assertQueryMatchCount(2, "(*:* AND NOT price:*)");
        assertQueryMatchCount(2, "(*:* and not price:*)");
        assertQueryMatchCount(2, "(*:* and not price)");
        
        assertQueryMatchCount(0, "price:*");
        assertQueryMatchCount(1, "price:[* TO *]");
        assertQueryMatchCount(1, "NOT price:[* TO *]");
        
        SolrQuery query = new SolrQuery();
        query.set("q", "NOT price:[* TO *]");
        QueryResponse response = solr.query(query);
        SolrDocumentList docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        assertEquals((String) docList.get(0).getFieldValue("id"), "1");
        
        query = new SolrQuery();
        query.set("q", "price:[* TO *]");
        response = solr.query(query);
        docList = response.getResults();
        assertEquals(docList.getNumFound(), 1);
        assertEquals((String) docList.get(0).getFieldValue("id"), "2");
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
        solr.addBean(new ProductBean("888", "Apple iPhone 6s", 299.99));
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
        // text-field (use 'string' type for that, not 'text_general')
    }

    @Test
    public void testOrderedResultPages() throws Exception {
        // prepare the dataset
        solr.deleteByQuery("*");

        for (int i = 0; i < 30; i++) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", "" + i);
            document.addField("price", i * 1.5);
            solr.add(document);
        }
        commit();

        SolrQuery query = new SolrQuery();
        query.set("q", "price:[* TO *]");
        query.set("rows", 3);
        query.set("start", 0);
        query.set("sort", "price asc");
        QueryResponse response = solr.query(query);
        List<ProductBean> docList = response.getBeans(ProductBean.class);
        assertEquals(3, docList.size());
        assertEquals(0, docList.get(0).getPrice(), 1e-6);
        assertEquals(1.5, docList.get(1).getPrice(), 1e-6);
        assertEquals(3, docList.get(2).getPrice(), 1e-6);

        query = new SolrQuery();
        query.set("q", "price:[* TO *]");
        query.set("rows", 3);
        query.set("start", 3);
        query.set("sort", "price asc");
        response = solr.query(query);
        docList = response.getBeans(ProductBean.class);
        assertEquals(3, docList.size());
        assertEquals(4.5, docList.get(0).getPrice(), 1e-6);
        assertEquals(6, docList.get(1).getPrice(), 1e-6);
        assertEquals(7.5, docList.get(2).getPrice(), 1e-6);

        query = new SolrQuery();
        query.set("q", "price:[* TO 40]");
        query.set("rows", 3);
        query.set("start", 0);
        query.set("sort", "price desc");
        response = solr.query(query);
        docList = response.getBeans(ProductBean.class);
        assertEquals(3, docList.size());
        assertEquals(39, docList.get(0).getPrice(), 1e-6);
        assertEquals(37.5, docList.get(1).getPrice(), 1e-6);
        assertEquals(36, docList.get(2).getPrice(), 1e-6);

        query = new SolrQuery();
        query.set("q", "price:[* TO 40]");
        query.set("rows", 3);
        query.set("start", 2);
        query.set("sort", "price desc");
        response = solr.query(query);
        docList = response.getBeans(ProductBean.class);
        assertEquals(3, docList.size());
        assertEquals(36, docList.get(0).getPrice(), 1e-6);
        assertEquals(34.5, docList.get(1).getPrice(), 1e-6);
        assertEquals(33, docList.get(2).getPrice(), 1e-6);
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

    @Test
    public void testClusterStatusResponse() throws Exception {
        assumeTrue(Config.IS_CLUSTERED);

        ClusterStatus status = new ClusterStatus();
        NamedList<Object> res = solr.request(status);
        ClusterStatusResponse response = new ClusterStatusResponse(res);

        assertEquals(1, response.getCollections().size());
        CollectionStatus collectionStatus = response.getCollections().get(Config.COLLECTION);
        assertNotNull(collectionStatus);
        assertEquals(2, collectionStatus.getReplicationFactor());
        assertEquals(Config.COLLECTION_CFGNAME, collectionStatus.getConfigName());

        assertEquals(3, collectionStatus.getShards().size());

        RedundancyStatus dataStatus = collectionStatus.getRedundancyStatus();
        LOG.info(dataStatus.getMessage());
        LOG.info(dataStatus.getLongMessage());
        LOG.info(dataStatus.getState().name());
        assertEquals(RedundancyState.HEALTHY, dataStatus.getState());

        assertEquals(3, response.getLiveNodes().size());
        assertTrue(response.getLiveNodes().contains("localhost:8983_solr"));
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

}
