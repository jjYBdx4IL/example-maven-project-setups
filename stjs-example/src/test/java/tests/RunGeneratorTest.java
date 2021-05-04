package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stjs.generator.GenerationDirectory;
import org.stjs.generator.Generator;
import org.stjs.generator.GeneratorConfiguration;
import org.stjs.generator.GeneratorConfigurationBuilder;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;

import a.b.c.DTO;
import a.b.c.Global2;
import a.b.c.Main;
import utils.Maven;

/**
 * https://github.com/st-js/st-js/blob/master/maven-plugin/src/main/java/org/stjs/maven/AbstractSTJSMojo.java#L199
 *
 * @author jjYBdx4IL
 */
public class RunGeneratorTest extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RunGeneratorTest.class);

	private final File baseDir = new File(Maven.getBasedir(RunGeneratorTest.class));
	private final File sourceFolder = new File(baseDir, "src/main/java");
	private final File tempFolder = new File(baseDir, "target/" + RunGeneratorTest.class.getName());
	private Server server = new Server(0);
	private final List<String> jsFiles = new ArrayList<>();

	@After
	public void after() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Before
	public void before() throws Exception {
		tempFolder.mkdirs();
		FileUtils.cleanDirectory(tempFolder);

		server.setHandler(this);
		server.start();
	}

	private void compileAll() throws URISyntaxException {
        GeneratorConfigurationBuilder configBuilder = new GeneratorConfigurationBuilder();
        configBuilder.allowedPackage(Main.class.getPackage().getName());
        configBuilder.stjsClassLoader(getClass().getClassLoader());
        configBuilder.targetFolder(Paths.get(baseDir.getAbsolutePath(), "target/classes").toFile());
        GenerationDirectory gendir = new GenerationDirectory(tempFolder, null, new URI("/jsoutput"));
        configBuilder.generationFolder(gendir);
        GeneratorConfiguration configuration = configBuilder.build();
        Generator generator = new Generator(configuration);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceFolder.getAbsolutePath());
        scanner.setIncludes(new String[] {"**/*.java"});
        scanner.scan();
        for (String sourceFile : scanner.getIncludedFiles()) {
            String className = sourceFile.substring(0,  sourceFile.length()-5).replace(File.separator, ".");
            long durationMs = -System.currentTimeMillis();
            generator.generateJavascript(className, sourceFolder);
            durationMs += System.currentTimeMillis();
            LOG.info(String.format(Locale.ROOT, "js generation time for %s: %d ms", className, durationMs));
            jsFiles.add(sourceFile.substring(0, sourceFile.length()-5) + ".js");
        }
	    
        generator.copyJavascriptSupport(tempFolder);
	}
	
	@Test
	public void test() throws Exception {
	    compileAll();

		final CountDownLatch countDownNative = new CountDownLatch(1);
		final CountDownLatch countDownNativePost = new CountDownLatch(1);
		final CountDownLatch countDownJquery = new CountDownLatch(1);

		// uncomment to test stuff interactively:
		//Thread.sleep(30000000L);

		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(true);
			webClient.setCssErrorHandler(new ErrorHandler() {

				@Override
				public void warning(CSSParseException exception) throws CSSException {
					LOG.info("" + exception);
				}

				@Override
				public void fatalError(CSSParseException exception) throws CSSException {
					LOG.info("" + exception);
				}

				@Override
				public void error(CSSParseException exception) throws CSSException {
					LOG.info("" + exception);
				}
			});
			webClient.setAlertHandler(new AlertHandler() {

				@Override
				public void handleAlert(Page arg0, String arg1) {
					LOG.info("alert: " + arg1);
					if ("test log alert for native ajax".equals(arg1)) {
						countDownNative.countDown();
					}
                    if ("test log alert for native ajax POST".equals(arg1)) {
                        countDownNativePost.countDown();
                    }
					if ("test log alert for jquery ajax".equals(arg1)) {
						countDownJquery.countDown();
					}
				}
			});

			final HtmlPage page = webClient.getPage(getUrl("/"));
			assertEquals(0, webClient.waitForBackgroundJavaScript(10000L));
			LOG.info("page: " + page.asXml());
		}

		assertTrue(countDownNative.await(10, TimeUnit.SECONDS));
		assertTrue(countDownNativePost.await(10, TimeUnit.SECONDS));
		assertTrue(countDownJquery.await(10, TimeUnit.SECONDS));
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
		LOG.info(String.format(Locale.ROOT, "handle(%s %s, ...)", request.getMethod(), target));

		if ("/".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.getWriter().print("<!DOCTYPE html><html><head>");
			response.getWriter().print("<script type=\"text/javascript\" src=\"jquery.js\"></script>");
			response.getWriter().print("<script type=\"text/javascript\" src=\"stjs.js\"></script>");
			for (String jsFile : jsFiles) {
				response.getWriter().print("<script type=\"text/javascript\" src=\""+jsFile+"\"></script>");
			}
			response.getWriter().print("</head><body>" + "</body></html>");
        } else if ("/postDtoNative".equals(target)) {
            String input = IOUtils.toString(request.getReader());
            LOG.info("POST content: " + input);
            DTO dto = new Gson().fromJson(input, DTO.class);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            dto.setText("test " + dto.getText());
            response.getWriter().print(new Gson().toJson(dto));
		} else if ("/getDtoNative".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			DTO dto = new DTO();
			dto.setText("test log alert for native ajax");
			response.getWriter().print(new Gson().toJson(dto));
		} else if ("/getDtoJquery".equals(target)) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			DTO dto = new DTO();
			dto.setText("test log alert for jquery ajax");
			response.getWriter().print(new Gson().toJson(dto));
			// response.getWriter().print(request.getParameter("callback") + "("+new
			// Gson().toJson(dto)+");");
		} else if ("/jquery.js".equals(target)) {
			try (InputStream is = getClass()
					.getResourceAsStream("/META-INF/resources/webjars/jquery/1.11.3/jquery.js")) {
				String js = IOUtils.toString(is, "UTF-8");
				// eliminate jquery source error (some sort of Opera compat thing)
				// js = js.replace("div.querySelectorAll(\"*,:x\");", "throw new Error( \"\"
				// );");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/javascript");
				response.getWriter().print(js);
			}
		} else {
			try {
				File jsFile = new File(tempFolder, target.substring(1));
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
