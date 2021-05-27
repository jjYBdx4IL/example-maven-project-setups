package testroot;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.apps.rasterizer.Main;
import org.apache.batik.svggen.font.SVGFont;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import java.awt.Desktop;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class Ttf2SvgTest {

    public static final String fontUrl = "https://fonts.google.com/download?family=Pattaya";
    private static final File workDir = new File("./target");
    private static final File fontFile = new File(workDir, "font.ttf");
    private static final File svgOutFile = new File(workDir, "output.svg");
    private static final File svgOutFile2 = new File(workDir, "output2.svg");
    private static final File imgOutFile = new File(workDir, "output.png");

    @Test
    public void testTtf2Svg() throws Exception {
        // convert ttf -> svg (font def)
        // https://xmlgraphics.apache.org/batik/tools/font-converter.html
        SVGFont.main(new String[] { fontFile.getAbsolutePath(), "-o", svgOutFile.getAbsolutePath() });

        String fontFamily = getFontFamily(svgOutFile);

        // lets 'draw' some text
        String appendCommands = String.format(Locale.ROOT, """
                        <g style="font-family:%s;font-size:40;fill:black">
                            <text x="20" y="120">Hello, World!</text>
                        </g>
                        """, fontFamily);
        
        String contents = FileUtils.readFileToString(svgOutFile, UTF_8);
        contents = contents.replace("</svg>", appendCommands + "</svg>");
        FileUtils.writeStringToFile(svgOutFile2, contents, UTF_8);

        // rasterize to image
        // https://xmlgraphics.apache.org/batik/tools/rasterizer.html
        (new Main(new String[] { "-d", imgOutFile.getAbsolutePath(), "-scriptSecurityOff", "-bg", "255.255.255.255",
                "-m", "image/png", svgOutFile2.getAbsolutePath() })).execute();

        if (isEclipseJunitRuner()) {
            Desktop.getDesktop().open(imgOutFile);
        }
    }

    @Before
    public void before() throws IOException, FontFormatException {
        // download TTF from Google Fonts and cache it in ./target/
        File f = new File(workDir, "font.zip");
        if (!f.exists()) {
            FileUtils.copyURLToFile(new URL(fontUrl), f);
        }

        // extract the .ttf file
        ZipFile zf = new ZipFile(f);
        FileUtils.copyInputStreamToFile(zf.getInputStream(zf.getEntry("Pattaya-Regular.ttf")), fontFile);
        zf.close();
    }

    // https://xmlgraphics.apache.org/batik/using/dom-api.html
    private String getFontFamily(File svgFile) throws IOException, XPathExpressionException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        SVGDocument doc = f.createSVGDocument(svgFile.toURI().toASCIIString());

        XPath xPath = XPathFactory.newInstance().newXPath();

        // now for the funny part...
        xPath.setNamespaceContext(new NamespaceContext() {
            private final Map<String, String> map = new HashMap<>();
            {
                map.put("svg", "http://www.w3.org/2000/svg");
            }

            public String getNamespaceURI(String prefix) {
                return map.get(prefix);
            }

            public String getPrefix(String uri) {
                throw new UnsupportedOperationException();
            }

            public Iterator<String> getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }
        });
        // ... lol.

        return (String) xPath.evaluate("string(/svg:svg[1]/svg:defs[1]/svg:font[1]/svg:font-face[1]/@font-family[1])",
                doc, XPathConstants.STRING);
    }

    private static boolean isEclipseJunitRuner() {
        return System.getProperty("sun.java.command", "").contains("org.eclipse.jdt.internal.junit.runner");
    }
}
