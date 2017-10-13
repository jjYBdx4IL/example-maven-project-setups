package com.github.jjYBdx4IL.example.solr;

import com.github.jjYBdx4IL.example.solr.Config;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class SpamMain {

    private static final Logger LOG = LoggerFactory.getLogger(SpamMain.class);

    SolrClient solr = null;
    String[] words = null;
    Random r = new Random(0);
    long wordCount = 0;

    public static void main(String[] args) {
        try {
            new SpamMain().run();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
        LOG.info("done");
        System.exit(0);
    }

    public void run() throws SolrServerException, IOException {
        solr = Config.createClient();

        loadWords();

        solr.deleteByQuery("*");
        solr.commit();

        long lastWordCount = wordCount;
        long lastTime = System.currentTimeMillis();
        for (int i = 1; i >= 0; i++) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", "" + i);
            document.addField("title", getText(1 + r.nextInt(10)));
            document.addField("content", getText(3 + r.nextInt(2000)));
            solr.add(document);

            if (i % 1000 == 0) {
                long millis = Math.max(System.currentTimeMillis() - lastTime, 1);
                float wordsPerSecond = (wordCount-lastWordCount) / (millis / 1e3f); 
                LOG.info(String.format("%,d words in %,d documents added (%,d words added per second)",
                    wordCount, i, (int) wordsPerSecond));
                lastWordCount = wordCount;
                lastTime = System.currentTimeMillis();
                
                if (!Config.IS_CLUSTERED) {
                    solr.commit();
                }
            }
        }
        
        // no need for commit because we configured an autocommit max delay
    }

    private void loadWords() throws FileNotFoundException, IOException {
        try (InputStream is = getWordsFileInputStream()) {
            String wordsContent = IOUtils.toString(is, "UTF-8");
            words = wordsContent.split("\\r?\\n");
        }
        LOG.info("read " + words.length + " words");
    }
    
    private InputStream getWordsFileInputStream() throws FileNotFoundException {
        File f = new File(getClass().getResource("/").toExternalForm().substring(5));
        f = f.getParentFile();
        f = new File(f, "words.txt");
        return new FileInputStream(f);
    }

    public String getText(int numWords) {
        StringBuilder sb = new StringBuilder(numWords * 5);
        wordCount += numWords;
        for (int i = 0; i < numWords; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(words[r.nextInt(words.length)]);
        }
        return sb.toString();
    }
}
