package my.example;

import com.google.gwt.dom.client.Element;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// http://www.gwtproject.org/doc/latest/DevGuideTesting.html
public class ExampleTest extends GWTTestCase {

    private static final Logger LOG = Logger.getLogger(ExampleTest.class.getName());
    
    private static native String getNodeName(Element elem)
    /*-{
    return (elem.nodeName || "").toLowerCase();
    }-*/;

    /**
     * Removes all elements in the body, except scripts and iframes.
     */
    @Before
    public void gwtSetUp() {
        Element bodyElem = RootPanel.getBodyElement();

        List<Element> toRemove = new ArrayList<Element>();
        for (int i = 0, n = DOM.getChildCount(bodyElem); i < n; ++i) {
            Element elem = DOM.getChild(bodyElem, i);
            String nodeName = getNodeName(elem);
            if (!"script".equals(nodeName) && !"iframe".equals(nodeName)) {
                toRemove.add(elem);
            }
        }

        for (int i = 0, n = toRemove.size(); i < n; ++i) {
            DOM.removeChild(bodyElem, toRemove.get(i));
        }
    }
    
    @Test
    public void test() {
        assertFalse(isPureJava());
        LOG.log(Level.SEVERE, "this message should get logged");
    }
    
    @DoNotRunWith(Platform.HtmlUnitUnknown)
    @Test
    public void test2() {
        LOG.log(Level.SEVERE, "this message should *NOT* get logged");
        fail();
    }

    @Override
    public String getModuleName() {
        return App.class.getName();
    }

}
