package my.example;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

// http://www.gwtproject.org/doc/latest/DevGuideTesting.html
public class TimerTest extends GWTTestCase {

    private static final Logger LOG = Logger.getLogger(TimerTest.class.getName());

    private int counter = 3;
    private Timer timer = null;
    
    @Test
    public void testTimer() {
        timer = new Timer() {
            public void run() {
                LOG.log(Level.SEVERE, "counter: " + counter);
                if (--counter == 0) {
                    finishTest();
                    LOG.log(Level.SEVERE, "Done.");
                } else {
                    delayTestFinish(5000);
                    timer.schedule(100);
                }
            }
        };

        delayTestFinish(5000);
        timer.schedule(100);
    }

    @Override
    public String getModuleName() {
        return App.class.getName();
    }

}
