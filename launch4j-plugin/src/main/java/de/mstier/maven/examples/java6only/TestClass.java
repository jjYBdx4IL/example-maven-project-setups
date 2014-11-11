package de.mstier.maven.examples.java6only;

import java.io.File;
import java.nio.file.Files;

public class TestClass {

    public void test() {
        Files.exists(new File("a").toPath());
    }
}
