/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.session;

import java.io.*;
import java.net.URL;
import java.util.*;
import junit.framework.*;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.tests.harness.CoreTest;
import org.eclipse.core.tests.harness.FileSystemHelper;
import org.eclipse.core.tests.session.SetupManager.SetupException;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationSessionTestSuite extends SessionTestSuite {
	// include configurator as it is required by compatibility, but do not set it to start 	
	public static String[] MINIMAL_BUNDLE_SET = {"org.eclipse.equinox.common@2:start", //
			"org.eclipse.core.runtime@:start", //
			"org.eclipse.core.jobs", //
			"org.eclipse.core.runtime.compatibility.registry", //
			"org.eclipse.equinox.registry", //
			"org.eclipse.equinox.preferences", //
			"org.eclipse.core.contenttype", //
			"org.eclipse.equinox.app", //
			"org.eclipse.core.runtime.compatibility", //
			"org.eclipse.core.runtime.compatibility.auth", //
			"org.eclipse.update.configurator", //
			"org.eclipse.core.tests.harness", //
			"org.eclipse.jdt.junit.runtime", //
			"org.eclipse.jdt.junit4.runtime", //
			"org.eclipse.pde.junit.runtime", //
			"org.hamcrest.core", //
			"org.junit", //
			"org.eclipse.test.performance"};

	private static final String PROP_CONFIG_AREA_READ_ONLY = InternalPlatform.PROP_CONFIG_AREA + ".readOnly";
	private static final String PROP_CONFIG_CASCADED = "osgi.configuration.cascaded";
	private static final String PROP_SHARED_CONFIG_AREA = "osgi.sharedConfiguration.area";
	private Collection bundles = new ArrayList();
	private boolean cascaded;

	// by default we clean-up after ourselves
	private boolean cleanUp = true;

	private IPath configurationPath = FileSystemHelper.getRandomLocation(FileSystemHelper.getTempDir());
	private boolean prime = true;
	private boolean readOnly;
	// should the test cases be run in alphabetical order?
	private boolean shouldSort;

	public ConfigurationSessionTestSuite(String pluginId) {
		super(pluginId);
	}

	public ConfigurationSessionTestSuite(String pluginId, Class theClass) {
		super(pluginId, theClass);
		this.shouldSort = true;
	}

	public ConfigurationSessionTestSuite(String pluginId, Class theClass, String name) {
		super(pluginId, theClass, name);
		this.shouldSort = true;
	}

	public ConfigurationSessionTestSuite(String pluginId, String name) {
		super(pluginId, name);
	}

	public void addBundle(String id) {
		bundles.addAll(getURLs(id));
	}

	private void createConfigINI() throws IOException {
		Assert.assertTrue("1.0", !bundles.isEmpty());
		Properties contents = new Properties();
		StringBuffer osgiBundles = new StringBuffer();
		for (Iterator i = this.bundles.iterator(); i.hasNext();) {
			osgiBundles.append(i.next());
			osgiBundles.append(',');
		}
		osgiBundles.deleteCharAt(osgiBundles.length() - 1);
		contents.put("osgi.bundles", osgiBundles.toString());
		String osgiFramework = (String) getURLs("org.eclipse.osgi").get(0);
		contents.put("osgi.framework", osgiFramework);
		contents.put("osgi.bundles.defaultStartLevel", "4");
		contents.put("osgi.install.area", Platform.getInstallLocation().getURL().toExternalForm());
		contents.put(PROP_CONFIG_CASCADED, Boolean.toString(cascaded));
		if (cascaded)
			contents.put(PROP_SHARED_CONFIG_AREA, Platform.getConfigurationLocation().getURL().toExternalForm());
		contents.put(PROP_CONFIG_AREA_READ_ONLY, Boolean.toString(readOnly));
		// save the properties
		File configINI = configurationPath.append("config.ini").toFile();
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(configINI));
			contents.store(out, null);
		} finally {
			if (out != null)
				out.close();
		}
	}

	protected void fillTestDescriptor(TestDescriptor test) throws SetupException {
		super.fillTestDescriptor(test);
		if (prime) {
			test.getSetup().setSystemProperty(PROP_CONFIG_AREA_READ_ONLY, Boolean.FALSE.toString());
			prime = false;
		}
	}

	public IPath getConfigurationPath() {
		return configurationPath;
	}

	private List getURLs(String id) {
		List result = new ArrayList();
		String suffix = "";
		int atIndex = id.indexOf("@");
		if (atIndex >= 0) {
			suffix = id.substring(atIndex);
			id = id.substring(0, atIndex);
		}
		Bundle[] allVersions = Platform.getBundles(id, null);
		Assert.assertNotNull("0.0.1." + id, allVersions);
		for (int i = 0; i < allVersions.length; i++) {
			Bundle bundle = allVersions[i];
			Assert.assertNotNull("0.1 " + id, bundle);
			URL url = bundle.getEntry("/");
			Assert.assertNotNull("0.2 " + id, url);
			try {
				url = FileLocator.resolve(url);
			} catch (IOException e) {
				CoreTest.fail("0.3 " + url, e);
			}
			String externalForm;
			if (url.getProtocol().equals("jar")) {
				// if it is a JAR'd plug-in, URL is jar:file:/path/file.jar!/ - see bug 86195
				String path = url.getPath();
				// change it to be file:/path/file.jar
				externalForm = path.substring(0, path.length() - 2);
			} else
				externalForm = url.toExternalForm();
			// workaround for bug 88070		
			externalForm = "reference:" + externalForm;
			result.add(externalForm + suffix);
		}
		return result;
	}

	public boolean isCascaded() {
		return cascaded;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Ensures setup uses this suite's instance location.
	 * @throws SetupException
	 */
	protected Setup newSetup() throws SetupException {
		Setup base = super.newSetup();
		// the base implementation will have set this to the host configuration
		base.setEclipseArgument(Setup.CONFIGURATION, null);
		base.setSystemProperty(InternalPlatform.PROP_CONFIG_AREA, configurationPath.toOSString());
		return base;
	}

	/**
	 * Ensures workspace location is empty before running the first test, and after
	 * running the last test. Also sorts the test cases to be run if this suite was
	 * created by reifying a test case class.
	 */
	public void run(TestResult result) {
		configurationPath.toFile().mkdirs();
		try {
			if (prime)
				try {
					createConfigINI();
				} catch (IOException e) {
					CoreTest.fail("0.1", e);
				}
			if (!shouldSort || isSharedSession()) {
				// for shared sessions, we don't control the execution of test cases
				super.run(result);
				return;
			}
			// we have to sort the tests cases
			Test[] allTests = getTests(true);
			// now run the tests in order			
			for (int i = 0; i < allTests.length && !result.shouldStop(); i++) {
				// KLUDGE: this is a  work around update.configurator's
				// class PlatformConfiguration.initializeCurrent(). That method will overwrite
				// config.ini for shared configurations. As there is no switch to alter
				// that behavior and update.configurator is close to be being retired,
				// the kludge here is to generate new config.ini for every test run.
				if (cascaded)
					try {
						createConfigINI();
					} catch (IOException e) {
						CoreTest.fail("0.1", e);
					}
				// end of KLUDGE

				runTest(allTests[i], result);
			}
		} finally {
			if (cleanUp)
				FileSystemHelper.clear(configurationPath.toFile());
		}

	}

	public void setCascaded(boolean cascaded) {
		this.cascaded = cascaded;
	}

	public void setCleanup(boolean cleanUp) {
		this.cleanUp = cleanUp;
	}

	public void setConfigurationPath(IPath configurationPath) {
		this.configurationPath = configurationPath;
	}

	public void setPrime(boolean prime) {
		this.prime = prime;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public static File getConfigurationDir() {
		Location configurationLocation = Platform.getConfigurationLocation();
		URL configurationURL = configurationLocation.getURL();
		if (!"file".equals(configurationURL.getProtocol()))
			// only works if configuration is file: based
			throw new IllegalStateException();
		File configurationDir = new File(configurationURL.getFile());
		return configurationDir;
	}

}
