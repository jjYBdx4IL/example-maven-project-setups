package com.github.jjYBdx4IL.example.solr;

import static org.junit.Assert.assertEquals;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

// see also https://github.com/apache/lucene-solr/tree/master/solr/solrj/src/test/org/apache/solr/client/solrj
// or http://www.baeldung.com/apache-solrj
public class SolrClientIT {

    HttpSolrClient solr = null;

    @Before
    public void before() {
        String urlString = "http://localhost:8983/solr/bigboxstore";
        solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
    }

    @Test
    public void testCRUD() throws Exception {
        // delete by query
        solr.deleteByQuery("*");
        solr.commit();

        // add a document
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "123456"); // re-using the same id replaces the
                                           // previous document
        document.addField("name", "Kenmore Dishwasher");
        document.addField("price", "599.99");
        solr.add(document);
        solr.commit();

        // add a document by using a bean
        solr.addBean(new ProductBean("888", "Apple iPhone 6s", "299.99"));
        solr.commit();

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
        solr.commit();

        // delete by id
        solr.deleteById("123456");
        solr.commit();
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
        solr.commit();

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "12345");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "one two three four five");
        solr.add(document);
        solr.commit();

        document = new SolrInputDocument();
        document.addField("id", "123457");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "six eighty");
        solr.add(document);
        solr.commit();

        document = new SolrInputDocument();
        document.addField("id", "1234578");
        document.addField("name", "testSubstringSearch");
        document.addField("text", "seven eight");
        solr.add(document);
        solr.commit();

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
        solr.commit();

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "a12345");
        document.addField("name", "testDeleteByQuery");
        document.addField("text", "one");
        solr.add(document);
        solr.commit();

        document = new SolrInputDocument();
        document.addField("id", "a123457");
        document.addField("name", "testDeleteByQuery ABC");
        document.addField("text", "one");
        solr.add(document);
        solr.commit();

        assertQueryMatchCount(2, "text:\"one\"~0");

        // matches are always word-based, therefore the following query will
        // delete both documents:
        solr.deleteByQuery("name:\"testDeleteByQuery\"~0");
        solr.commit();

        assertQueryMatchCount(0, "text:\"one\"~0");
    }

    public void assertQueryMatchCount(int count, String queryText) throws Exception {
        SolrQuery query = new SolrQuery();
        query.set("q", queryText);
        QueryResponse response = solr.query(query);

        SolrDocumentList docList = response.getResults();
        assertEquals(count, docList.getNumFound());
    }
}
