/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.core.epl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CoreEPLPlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.aptana.core.epl"; //$NON-NLS-1$

    // The shared instance
    private static CoreEPLPlugin plugin;

    /**
     * The constructor
     */
    public CoreEPLPlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static CoreEPLPlugin getDefault() {
        return plugin;
    }

    public static void log(String msg, Throwable e) {
        log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e)); 
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

}
