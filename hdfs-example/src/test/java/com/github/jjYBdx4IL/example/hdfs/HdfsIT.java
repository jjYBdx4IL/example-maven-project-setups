package com.github.jjYBdx4IL.example.hdfs;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// https://github.com/apache/hadoop-hdfs/tree/HDFS-265/src/test/hdfs/org/apache/hadoop/hdfs
public class HdfsIT {

    private static final Logger LOG = LoggerFactory.getLogger(HdfsIT.class);
    public static final String FS_LOCATION = "hdfs://localhost:9000";
    public static final String TESTCONTENT = "some test text content for writing to HDFS";
    public static final int PORT_WAIT_SECS = 20;
    
    @Test
    public void testCRUD() throws Exception {
        try (FileSystem fileSystem = getFS()) {
            Path path = new Path("/1/2/3/bla");
            if (fileSystem.exists(path)) {
                fileSystem.delete(path, false);
            }

            try (FSDataOutputStream out = fileSystem.create(path)) {
                IOUtils.write(TESTCONTENT, out, "UTF-8");
            }
            
            String content = null;
            try (FSDataInputStream in = fileSystem.open(path)) {
                content = IOUtils.toString(in, "UTF-8");
            }
            
            assertEquals(TESTCONTENT, content);
        }
    }
    
    public static FileSystem getFS() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", FS_LOCATION);
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
