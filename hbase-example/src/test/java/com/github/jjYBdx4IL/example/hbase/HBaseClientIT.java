package com.github.jjYBdx4IL.example.hbase;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HBaseClientIT {

    private static final Logger LOG = LoggerFactory.getLogger(HBaseClientIT.class);

    // see http://www.baeldung.com/hbase to provide a non-default hbase config.
    static Configuration config = HBaseConfiguration.create();
    Connection connection = null; 

    @BeforeClass
    public static void beforeClass() throws Exception {
        // wait for server start
        for (int i = 0; i < 60; i++) {
            try {
                HBaseAdmin.checkHBaseAvailable(config);
                return;
            } catch (Exception ex) {
                Thread.sleep(1000);
            }
        }
        HBaseAdmin.checkHBaseAvailable(config);
    }

    @Before
    public void before() throws IOException {
        connection = ConnectionFactory.createConnection(config);
    }
    
    @After
    public void after() {
        IOUtils.closeQuietly(connection);
    }
    
    @Test
    public void test() throws Exception {
        // dropping and creating the table
        Admin admin = connection.getAdmin();

        TableName table1 = TableName.valueOf("Table1");
        String family1 = "Family1";
        String family2 = "Family2";
        String qualifier1 = "Qualifier1";
        String testValue = "test value";

        if (admin.tableExists(table1)) {
            admin.disableTable(table1);
            admin.deleteTable(table1);
        }

        HTableDescriptor desc = new HTableDescriptor(table1);
        desc.addFamily(new HColumnDescriptor(family1));
        desc.addFamily(new HColumnDescriptor(family2));
        admin.createTable(desc);

        // get a table handle
        Table table = connection.getTable(table1);

        // add some data
        byte[] row1 = Bytes.toBytes("row1");
        Put p = new Put(row1);
        p.addImmutable(family1.getBytes(), qualifier1.getBytes(), Bytes.toBytes(testValue));
        table.put(p);

        // retrieve some data
        Get g = new Get(row1);
        Result r = table.get(g);
        byte[] value = r.getValue(family1.getBytes(), qualifier1.getBytes());
        assertEquals(testValue, Bytes.toString(value));

        // scan data
        Scan scan = new Scan();
        scan.addColumn(family1.getBytes(), qualifier1.getBytes());
        int scanResultCount = 0;
        Cell resultCell = null;
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                LOG.info("Found row: " + result);
                resultCell = result.getColumnLatestCell(family1.getBytes(), qualifier1.getBytes());
                scanResultCount++;
            }
        }
        assertEquals(1, scanResultCount);
        assertEquals(testValue,
            Bytes.toString(resultCell.getValueArray(), resultCell.getValueOffset(), resultCell.getValueLength()));

        // use a filter for scanning the data
        Filter filter1 = new PrefixFilter(row1);
        Filter filter2 = new QualifierFilter(
            CompareOp.GREATER_OR_EQUAL,
            new BinaryComparator(qualifier1.getBytes()));
        List<Filter> filters = Arrays.asList(filter1, filter2);
        
        scan = new Scan();
        scan.setFilter(new FilterList(Operator.MUST_PASS_ALL, filters));
        scanResultCount = 0;
        resultCell = null;
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                LOG.info("Found row: " + result);
                resultCell = result.getColumnLatestCell(family1.getBytes(), qualifier1.getBytes());
                scanResultCount++;
            }
        }
        assertEquals(1, scanResultCount);
        assertEquals(testValue,
            Bytes.toString(resultCell.getValueArray(), resultCell.getValueOffset(), resultCell.getValueLength()));

        // delete a row
        Delete delete = new Delete(row1);
        delete.addColumn(family1.getBytes(), qualifier1.getBytes());
        table.delete(delete);
    }
}
