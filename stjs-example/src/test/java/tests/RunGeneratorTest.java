package tests;

import a.b.c.DTO;
import a.b.c.Main;
import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.env.Maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stjs.generator.ClassWithJavascript;
import org.stjs.generator.GenerationDirectory;
import org.stjs.generator.Generator;
import org.stjs.generator.GeneratorConfiguration;
import org.stjs.generator.GeneratorConfigurationBuilder;
import org.stjs.generator.name.DependencyType;

/**
 * https://github.com/st-js/st-js/blob/master/maven-plugin/src/main/java/org/stjs/maven/AbstractSTJSMojo.java#L199
 *
 * @author jjYBdx4IL
 */
public class RunGeneratorTest {

	private static final Logger LOG = LoggerFactory.getLogger(RunGeneratorTest.class);

	private final File sourceFolder = new File(new File(Maven.getBasedir(RunGeneratorTest.class)), "src/main/java");
	private final File tempFolder = FileUtil.createMavenTestDir(RunGeneratorTest.class);

	@Test
	public void test() throws URISyntaxException, IOException, ScriptException {
		GeneratorConfigurationBuilder configBuilder = new GeneratorConfigurationBuilder();
		configBuilder.allowedPackage("a.b.c");
		configBuilder.stjsClassLoader(getClass().getClassLoader());
		configBuilder.targetFolder(
				Paths.get(Maven.getMavenBuildDir(RunGeneratorTest.class).getAbsolutePath(), "classes").toFile());
		GenerationDirectory gendir = new GenerationDirectory(tempFolder, null, new URI("/jsoutput"));
		configBuilder.generationFolder(gendir);

		GeneratorConfiguration configuration = configBuilder.build();
		Generator generator = new Generator(configuration);

		// measure generation time in hot state
		ClassWithJavascript stjsClass = generator.generateJavascript(Main.class.getName(), sourceFolder);
		long durationMs = -System.currentTimeMillis();
		stjsClass = generator.generateJavascript(Main.class.getName(), sourceFolder);
		durationMs += System.currentTimeMillis();

		ClassWithJavascript stjsClass2 = generator.generateJavascript(DTO.class.getName(), sourceFolder);
		
		LOG.info("js generation time (hot, ms): " + durationMs);

		List<URI> outputFiles = new ArrayList<>(stjsClass2.getJavascriptFiles());
		outputFiles.addAll(stjsClass.getJavascriptFiles());
		assertEquals(2, outputFiles.size());

		// run the generated js
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");

		generator.copyJavascriptSupport(tempFolder);
		try (InputStream is = new FileInputStream(new File(tempFolder, "stjs.js"))) {
			engine.eval(new InputStreamReader(is, "UTF-8"));
		}
		try {
			LOG.info("" + outputFiles);
			for (URI jsFileUri : outputFiles) {
				File jsFile = new File(tempFolder, jsFileUri.toString());

				LOG.info("jsFile = " + jsFile.getAbsolutePath());
				String js = IOUtils.toString(jsFile.toURI(), "UTF-8");
				LOG.info(js);
				Object result = engine.eval(js);
				LOG.info("result = " + result);
			}
		} catch (ScriptException ex) {
			LOG.error("", ex);
		}
	}
}
