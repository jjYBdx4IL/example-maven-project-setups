package my.example;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ExampleTestSuite extends GWTTestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for Example Application");
        suite.addTestSuite(ExampleTest.class);
        suite.addTestSuite(TimerTest.class);
        return suite;
    }
}
