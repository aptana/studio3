/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class BundleTestingHelper {

	public static Bundle[] getBundles(BundleContext context, String symbolicName, String version) {
		ServiceReference<?> packageAdminReference = context.getServiceReference(PackageAdmin.class.getName());
		if (packageAdminReference == null) {
			throw new IllegalStateException("No package admin service found");
		}
		PackageAdmin packageAdmin = (PackageAdmin) context.getService(packageAdminReference);
		Bundle[] result = packageAdmin.getBundles(symbolicName, version);
		context.ungetService(packageAdminReference);
		return result;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static Bundle installBundle(BundleContext context, String location) throws BundleException, MalformedURLException, IOException {
		return installBundle("", context, location);
	}

	public static Bundle installBundle(String tag, BundleContext context, String location) throws BundleException, MalformedURLException, IOException {
		URL entry = context.getBundle().getEntry(location);
		if (entry == null) {
			Assert.fail(tag + " entry " + location + " could not be found in " + context.getBundle().getSymbolicName());
		}
		Bundle installed = context.installBundle(Platform.asLocalURL(entry).toExternalForm());
		return installed;
	}

	/**
	 * Do PackageAdmin.refreshPackages() in a synchronous way.  After installing
	 * all the requested bundles we need to do a refresh and want to ensure that
	 * everything is done before returning.
	 * @param bundles
	 * TODO remove this since all we wanted was to resolve bundles, what is done by #resolveBundles in this class
	 */
	//copied from EclipseStarter
	public static void refreshPackages(BundleContext context, Bundle[] bundles) {
		if (bundles.length == 0) {
			return;
		}
		ServiceReference<PackageAdmin> packageAdminRef = context.getServiceReference(PackageAdmin.class);
		PackageAdmin packageAdmin = null;
		if (packageAdminRef != null) {
			packageAdmin = context.getService(packageAdminRef);
			if (packageAdmin == null) {
				return;
			}
		}
		// TODO this is such a hack it is silly.  There are still cases for race conditions etc
		// but this should allow for some progress...
		// (patch from John A.)
		final boolean[] flag = new boolean[] {false};
		FrameworkListener listener = new FrameworkListener() {
			@Override
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
					synchronized (flag) {
						flag[0] = true;
						flag.notifyAll();
					}
				}
			}
		};
		context.addFrameworkListener(listener);
		packageAdmin.refreshPackages(bundles);
		synchronized (flag) {
			while (!flag[0]) {
				try {
					flag.wait();
				} catch (InterruptedException e) {
					// who cares....
				}
			}
		}
		context.removeFrameworkListener(listener);
		context.ungetService(packageAdminRef);
	}

	public static boolean  resolveBundles(BundleContext context, Bundle[] bundles) {
		ServiceReference<PackageAdmin> packageAdminRef = context.getServiceReference(PackageAdmin.class);
		PackageAdmin packageAdmin = null;
		if (packageAdminRef != null) {
			packageAdmin = context.getService(packageAdminRef);
			if (packageAdmin == null) {
				return false;
			}
		}
		boolean result = packageAdmin.resolveBundles(bundles);
		context.ungetService(packageAdminRef);
		return result;
	}

	public static void runWithBundles(String tag, Runnable runnable, BundleContext context, String[] locations, TestRegistryChangeListener listener) {
		if (listener != null) {
			listener.register();
		}
		try {
			Bundle[] installed = new Bundle[locations.length];
			for (int i = 0; i < locations.length; i++) {
				try {
					installed[i] = installBundle(tag + ".setup.0", context, locations[i]);
					Assert.assertEquals(tag + ".setup.1." + locations[i], Bundle.INSTALLED, installed[i].getState());
				} catch (BundleException e) {
					CoreTest.fail(tag + ".setup.2" + locations[i], e);
				} catch (IOException e) {
					CoreTest.fail(tag + ".setup.3" + locations[i], e);
				}
			}
			if (listener != null) {
				listener.reset();
			}
			if (!BundleTestingHelper.resolveBundles(context, installed)) {
				Assert.fail(tag + ".setup.resolveBundles");
			}
			if (listener != null) {
				// ensure the contributions were properly added
				Assert.assertTrue(tag + ".setup.4", listener.eventReceived(installed.length * 10000));
			}
			try {
				runnable.run();
			} finally {
				if (listener != null) {
					listener.reset();
				}
				// remove installed bundles
				for (int i = 0; i < installed.length; i++) {
					try {
						installed[i].uninstall();
					} catch (BundleException e) {
						CoreTest.fail(tag + ".tearDown.1." + locations[i], e);
					}
				}
				BundleTestingHelper.resolveBundles(context, installed);
				if (listener != null) {
					// ensure the contributions were properly added
					Assert.assertTrue(tag + ".tearDown.2", listener.eventReceived(installed.length * 10000));
				}
			}
		} finally {
			if (listener != null) {
				listener.unregister();
			}
		}
	}

}
