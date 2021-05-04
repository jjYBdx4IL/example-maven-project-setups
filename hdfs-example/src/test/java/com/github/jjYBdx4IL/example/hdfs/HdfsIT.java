package com.github.jjYBdx4IL.example.hdfs;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

// https://github.com/apache/hadoop-hdfs/tree/HDFS-265/src/test/hdfs/org/apache/hadoop/hdfs
public class HdfsIT {

    private static final Logger LOG = LoggerFactory.getLogger(HdfsIT.class);
    public static final String TESTCONTENT = "some test text content for writing to HDFS";
    public static final int PORT_WAIT_SECS = 20;
    public static final Random r = new Random();

    @Test
    public void testCRUD() throws Exception {
        try (FileSystem fs = getFS()) {
            Path path = new Path("/1/2/3/bla" + r.nextInt());
            if (fs.exists(path)) {
                fs.delete(path, false);
            }

            //try (FSDataOutputStream out = fs.create(path, (short) 2)) {
            try (FSDataOutputStream out = fs.create(path)) {
                IOUtils.write(TESTCONTENT, out, "UTF-8");
            }

            String content = null;
            try (FSDataInputStream in = fs.open(path)) {
                content = IOUtils.toString(in, "UTF-8");
            }

            assertEquals(TESTCONTENT, content);

            RemoteIterator<LocatedFileStatus> rit = fs.listFiles(new Path("/"), true);
            while (rit.hasNext()) {
                LOG.info("" + rit.next());
            }
        }
    }

    public static FileSystem getFS() throws IOException {
        File hadoopConf = new File(HdfsIT.class.getResource("/").toExternalForm().substring(5)).getParentFile();
        hadoopConf = new File(hadoopConf, "dfsnode1/etc/hadoop");
        LOG.info(hadoopConf.getAbsolutePath());
        Configuration conf = new Configuration();
        conf.addResource(new File(hadoopConf, "core-site.xml").toURI().toURL());
        conf.addResource(new File(hadoopConf, "hdfs-site.xml").toURI().toURL());
        long timeout = System.currentTimeMillis() + PORT_WAIT_SECS * 1000L;
        FileSystem fs = FileSystem.get(conf);
        do {
            try {
                LOG.info(String.format("%,d bytes free", fs.getStatus().getRemaining()));
                return fs;
            } catch (IOException ex) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        } while (System.currentTimeMillis() < timeout);
        throw new RuntimeException();
    }

}
