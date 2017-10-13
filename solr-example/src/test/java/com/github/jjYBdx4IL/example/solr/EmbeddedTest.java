package com.github.jjYBdx4IL.example.solr;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.io.FindUtils;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class EmbeddedTest {

    public static File tempDir = Maven.getTempTestDir(EmbeddedTest.class);
    
    @Ignore
    @Test
    public void test() throws Exception {
        FileUtils.cleanDirectory(tempDir);
        File solrHome = FindUtils.globOne(tempDir.getParentFile(), "**/server/solr/solr.xml").getParentFile();
//        System.setProperty("solr.solr.home", solrHome.getAbsolutePath());
//        FileUtils.copyDirectoryToDirectory(solrHome, tempDir);
//        solrHome = new File(tempDir, "solr");
        EmbeddedSolrServer server = new EmbeddedSolrServer(solrHome.toPath(), Config.COLLECTION);
        server.deleteByQuery( "*:*" );
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField( "id", "id1" );
        doc1.addField( "name", "doc1" );
        doc1.addField( "price", 10 );
        server.add(doc1);
        server.commit();
        server.close();
    }
}
