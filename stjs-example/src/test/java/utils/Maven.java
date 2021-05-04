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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * This is a copy from github-utils to prevent unnecessary dependencies. Do not
 * edit.
 * 
 * This is an utility class primarily aimed at supporting test units that are
 * part of Maven projects.
 * 
 * <p>
 * One aim is to eliminate boiler-plate code for setting up temporary
 * directories and/or cleaning them. On top of that, the methods should not
 * throw any checked exceptions, ie. to allow for easy initialization in test
 * units.
 * </p>
 *
 * @author jjYBdx4IL
 */
public class Maven {

	public static final String REL_TGT_DIR = "target";

	/**
	 * Please don't use this any more. It only works with Maven, ie. not when
	 * running Maven projects in Eclipse.
	 * 
	 * @return maven basedir
	 */
	@Deprecated
	public static String getMavenBasedir() {
		return System.getProperty("basedir");
	}

	/**
	 * Please don't use this any more. It only works with Maven, ie. not when
	 * running Maven projects in Eclipse.
	 * 
	 * @return maven build directory
	 */
	@Deprecated
	public static File getMavenTargetDir() {
		return new File(getMavenBasedir(), REL_TGT_DIR);
	}

	/**
	 * Determine Maven build directory. This assumes the standard location and has
	 * no knowledge of any reconfiguration happening in pom.xml or elsewhere.
	 * 
	 * @param classRef
	 *            see {@link Maven#getBasedir(Class)}
	 * @return the maven build directory
	 */
	public static File getMavenBuildDir(Class<?> classRef) {
		return new File(new File(getBasedir(classRef)), REL_TGT_DIR);
	}

	/**
	 * Returns a temporary, test-unit-specific directory inside the Maven build
	 * directory. It will create or clean the directory. Do not call this method
	 * more than once if you want to archive your test unit's output.
	 * 
	 * @param classRef
	 *            see {@link Maven#getBasedir(Class)}. This parameter is also used
	 *            to determine the last part of the temporary test directory.
	 * @return the temporary test directory, usually something like
	 *         '$basedir/target/$classRef".
	 */
	public static File getTempTestDir(Class<?> classRef) {
		File tempDir = new File(getMavenBuildDir(classRef), classRef.getName());
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		if (!tempDir.exists() || !tempDir.isDirectory()) {
			throw new RuntimeException("failed to create temporary test directory " + tempDir.getAbsolutePath());
		}
		try {
			FileUtils.cleanDirectory(tempDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tempDir;
	}

	/**
	 * This is an optimized frontend method for
	 * {@link #getBasedirCpOnly(java.lang.Class) }. It tries to determine the maven
	 * project's basedir by calling {@link Surefire#getMavenBasedir() } first, and
	 * only using the classpath resolution if that fails.
	 *
	 * @param classRef
	 *            only used if basedir determination by system properties only fails
	 * @return the URI for the basedir
	 */
	public static URI getBasedir(Class<?> classRef) {
		try {
			return new File(Surefire.getMavenBasedir()).toURI();
		} catch (IllegalStateException ex) { // exception should only be thrown
												// when we go the slower route
												// anyways
		}
		return getBasedirCpOnly(classRef);
	}

	/**
	 * Use class file location on disk to find maven project basedir. This only
	 * works for classes that are loaded from directories below the basedir, ie. not
	 * via classes loaded from jar files. The directory structure is then followed
	 * upwards until the maven project descriptor file pom.xml is found.
	 * 
	 * <p>
	 * Why we do this: because the Eclipse-integrated JUnit test runner does not
	 * provide any information about the maven project's basedir, which is different
	 * from running junit tests via maven-surefire-plugin or maven-failsafe-plugin:
	 * there we have the 'basedir' system property.
	 * </p>
	 *
	 * @param classRef
	 *            the class
	 * @return the Maven basedir, determined via classpath only
	 * @throws IllegalStateException
	 *             if no pom.xml was found
	 */
	public static URI getBasedirCpOnly(Class<?> classRef) {
		URL classLocUrl = classRef.getClassLoader().getResource(classRef.getName().replace('.', '/') + ".class");
		if (classLocUrl == null) {
			throw new IllegalStateException(
					"class " + classRef.getName() + " not found on its classloader's classpath");
		}
		String classLoc = classLocUrl.toExternalForm();
		String classpathEntry = classLoc.substring(0, classLoc.length() - classRef.getName().length() - 6);
		if (!classpathEntry.startsWith("file:/")) {
			throw new IllegalStateException("class " + classRef.getName()
					+ " has been loaded from resource not starting with file:/: " + classpathEntry);
		}
		String cpEntryFileLoc = classpathEntry.substring("file:".length());
		File projectBaseDir = new File(cpEntryFileLoc).getParentFile();
		while (projectBaseDir != null && !new File(projectBaseDir, "pom.xml").exists()) {
			projectBaseDir = projectBaseDir.getParentFile();
		}
		if (projectBaseDir == null) {
			throw new IllegalStateException("maven project basedir not found for " + classRef.getName()
					+ " because no parent directory of " + cpEntryFileLoc + " has a pom.xml entry!");
		}
		return projectBaseDir.toURI();
	}
}
