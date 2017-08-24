package tests;

import a.b.c.Main;
import com.github.jjYBdx4IL.test.FileUtil;
import com.github.jjYBdx4IL.utils.env.Maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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
 *
 * @author jjYBdx4IL
 */
public class RunGeneratorTest {

    private static final Logger LOG = LoggerFactory.getLogger(RunGeneratorTest.class);

    private final File sourceFolder = new File(Maven.getMavenBasedir(), "src/main/java");
    private final File tempFolder = FileUtil.createMavenTestDir(RunGeneratorTest.class);

    @Test
    public void test() throws URISyntaxException, IOException, ScriptException {
        GeneratorConfigurationBuilder configBuilder = new GeneratorConfigurationBuilder();
        configBuilder.allowedPackage("a.b.c");
        configBuilder.stjsClassLoader(getClass().getClassLoader());
        configBuilder.targetFolder(Paths.get(Maven.getMavenBasedir(), "target", "classes").toFile());
        GenerationDirectory gendir = new GenerationDirectory(tempFolder, null, new URI("/jsoutput"));
        configBuilder.generationFolder(gendir);

        GeneratorConfiguration configuration = configBuilder.build();
        Generator generator = new Generator(configuration);

        // measure generation time in hot state
        ClassWithJavascript stjsClass = generator.generateJavascript(Main.class.getName(), sourceFolder);
        long durationMs = -System.currentTimeMillis();
        stjsClass = generator.generateJavascript(Main.class.getName(), sourceFolder);
        durationMs += System.currentTimeMillis();
        
        Map<ClassWithJavascript, DependencyType> deps = stjsClass.getDirectDependencyMap();
        for (Map.Entry<ClassWithJavascript, DependencyType> entry : deps.entrySet()) {
        	LOG.info(""+entry);
        }
        
        generator.copyJavascriptSupport(tempFolder);
        
        LOG.info("js generation time (hot, ms): " + durationMs);
        
        List<URI> outputFiles = stjsClass.getJavascriptFiles();
        assertEquals(1, outputFiles.size());

        LOG.info(outputFiles.get(0).toString());
        File jsFile = new File(tempFolder, outputFiles.get(0).toString());

        LOG.info("jsFile = " + jsFile.getAbsolutePath());
        String js = IOUtils.toString(jsFile.toURI());
        LOG.info(js);

        // run the generated js
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        
        try (InputStream is = getClass().getResourceAsStream("/" + Generator.STJS_PATH)) {
            engine.eval(new InputStreamReader(is, "UTF-8"));
        }
        try {
            Object result = engine.eval(js);
        } catch (ScriptException ex) {
            LOG.error("", ex);
        }
    }
}
