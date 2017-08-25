/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.IOException;

/**
 * This is a copy from github-utils to prevent unnecessary dependencies. Do not
 * edit.
 * 
 * @author jjYBdx4IL
 */
public class Surefire extends Maven {

	public static final String PROPNAME_SUREFIRE_TEST_CLASS_PATH = "surefire.test.class.path";
	public static final String PROPNAME_SUN_JAVA_CMD = "sun.java.command";
	public static final String PROPNAME_BASEDIR = "basedir";
	public static final String ECLIPSE_TESTRUNNER_PREFIX = "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner ";

	public static String getSurefireTestClassPath() {
		String s = System.getProperty(PROPNAME_SUREFIRE_TEST_CLASS_PATH);
		if (s == null) {
			throw new IllegalStateException();
		}
		return s;
	}

	/**
	 * JUnit test being run inside Eclipse without calling maven?
	 *
	 * @return true iff being run as direct junit test in Eclipse without maven
	 */
	@SuppressWarnings("deprecation")
	public static boolean isEclipseDirectJUnit() {
		if (Maven.getMavenBasedir() == null
				&& System.getProperty(PROPNAME_SUN_JAVA_CMD).startsWith(ECLIPSE_TESTRUNNER_PREFIX)) {
			return true;
		}
		return false;
	}

	/**
	 * <b>Single</b> JUnit test being run inside Eclipse without calling maven?
	 *
	 * @return iff being run as <b>Single</b> JUnit test inside Eclipse without
	 *         maven
	 */
	public static boolean isEclipseDirectSingleJUnit() {
		if (System.getProperty(PROPNAME_BASEDIR) == null
				&& System.getProperty(PROPNAME_SUN_JAVA_CMD).startsWith(ECLIPSE_TESTRUNNER_PREFIX)
				&& System.getProperty(PROPNAME_SUN_JAVA_CMD).contains(" -test ")) {
			return true;
		}
		return false;
	}

	/**
	 * Resolve maven basedir by:
	 *
	 * <ol>
	 * <li>returning the value of the basedir system property,
	 * <li>or - if basedir prop does not exist or is null, return user.dir (current
	 * working directory) if eclipse junit test runner is detected via system
	 * properties.
	 * </ol>
	 *
	 * @return the maven base directory
	 * @throws IllegalStateException
	 *             if all fails
	 */
	@SuppressWarnings("deprecation")
	public static String getMavenBasedir() {
		if (Maven.getMavenBasedir() != null) {
			return Maven.getMavenBasedir();
		}
		if (isEclipseDirectJUnit()) {
			return System.getProperty("user.dir");
		}
		throw new IllegalStateException("maven basedir cannot be determined from system properties!");
	}

	/**
	 * Some junit tests might require user interaction. You may use this function
	 * together with a junit assumption to prevent running those tests in junit
	 * batch runs.
	 *
	 * @return true iff being run as a single test (method, not a test unit!)
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSingleTestExecution() {
		return isEclipseDirectSingleJUnit()
				|| Maven.getMavenBasedir() != null && System.getProperty("test", "").contains("#");
	}

	public static File getMavenTargetDir() {
		return new File(getMavenBasedir(), REL_TGT_DIR);
	}

	public static File getTempDirForClass(Class<?> clazz) throws IOException {
		File dir = new File(getMavenTargetDir(), clazz.getCanonicalName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!dir.exists()) {
			throw new IOException("failed to create directory: " + dir.getAbsolutePath());
		}
		return dir;
	}

	/**
	 * Same as {@link #getTempDirForClass(Class)} but wraps the IOException inside a
	 * RuntimeException.
	 *
	 * @param clazz
	 *            the class
	 * @return the temporary directory
	 */
	public static File getTempDirForClassRT(Class<?> clazz) {
		try {
			return getTempDirForClass(clazz);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
