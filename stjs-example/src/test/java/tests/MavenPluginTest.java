package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import utils.Maven;

public class MavenPluginTest extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(MavenPluginTest.class);

	private final File baseDir = new File(Maven.getBasedir(MavenPluginTest.class));
	private final File classesDir = new File(baseDir, "target/classes");
	private Server server = new Server(0);
	private final List<String> jsFileNames = new ArrayList<>();

	@Before
	public void before() throws Exception {

		jsFileNames.clear();
		jsFileNames.add("stjs.js");
		jsFileNames.add("a/b/c/Global2.js");
		jsFileNames.add("a/b/c/DTO.js");
		jsFileNames.add("a/b/c/Main.js");

		server.setHandler(this);
		server.start();
	}

	@After
	public void after() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Ignore
	@Test
	public void test() throws IOException, InterruptedException {
		
		final CountDownLatch countDown = new CountDownLatch(1);
		
		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setAlertHandler(new AlertHandler() {
				
				@Override
				public void handleAlert(Page arg0, String arg1) {
					LOG.info("alert: " + arg1);
					if ("test log alert".equals(arg1)) {
						countDown.countDown();
					}
				}
			});

			final HtmlPage page = webClient.getPage(getUrl("/"));
			LOG.info("page: " + page.asXml());
		}
		
		assertTrue(countDown.await(10, TimeUnit.SECONDS));
	}

	public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
		ServerConnector connector = (ServerConnector) server.getConnectors()[0];
		InetAddress addr = InetAddress.getLocalHost();
		return new URL(String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(),
				connector.getLocalPort(), path));
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

		if ("/".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.getWriter().print("<!DOCTYPE html><html><head>");
			for (String jsFileName : jsFileNames) {
				response.getWriter().print("<script type=\"text/javascript\" src=\"" + jsFileName + "\"></script>");
			}
			response.getWriter().print("</head><body>" + "</body></html>");
		} else {
			try {
				File jsFile = new File(classesDir, target.substring(1));
				LOG.info("serving file: " + jsFile.getAbsolutePath());
				String js = IOUtils.toString(jsFile.toURI(), "UTF-8");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/javascript");
				response.getWriter().print(js);
			} catch (IOException ex) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.setContentType("text/html");
			}
		}
		baseRequest.setHandled(true);
	}

}
