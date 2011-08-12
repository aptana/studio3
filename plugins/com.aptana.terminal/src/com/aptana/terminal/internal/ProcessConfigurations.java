/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.terminal.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.IProcessConfiguration;
import com.aptana.terminal.TerminalPlugin;

/**
 * @author Max Stepanov
 */
public final class ProcessConfigurations {

	private static final String EXTENSION_POINT_ID = TerminalPlugin.PLUGIN_ID + ".processConfigurations"; //$NON-NLS-1$
	private static final String TAG_CONFIGURATION = "configuration"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_PLATFORM = "platform"; //$NON-NLS-1$

	private static ProcessConfigurations instance = null;
	private List<IProcessConfiguration> configurations = new ArrayList<IProcessConfiguration>();

	/**
	 * 
	 */
	private ProcessConfigurations() {
		readExtensionRegistry();
	}

	public static ProcessConfigurations getInstance() {
		if (instance == null) {
			instance = new ProcessConfigurations();
		}
		return instance;
	}

	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i], TAG_CONFIGURATION);
		}
	}

	private void readElement(IConfigurationElement element, String elementName) {
		if (!elementName.equals(element.getName())) {
			return;
		}
		if (TAG_CONFIGURATION.equals(element.getName())) {
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0) {
				return;
			}

			String name = element.getAttribute(ATT_NAME);
			if (name == null || name.length() == 0) {
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			String platform = element.getAttribute(ATT_PLATFORM);
			if (platform == null || platform.length() == 0) {
				return;
			}
			if (platform.equals(Platform.getOS())) {
				try {
					configurations.add((IProcessConfiguration) element.createExecutableExtension(ATT_CLASS));
				} catch (CoreException e) {
					TerminalPlugin.log("Process configuration instantiation failed.", e); //$NON-NLS-1$
				}
			}
		}
	}

	public IProcessConfiguration[] getProcessConfigurations() {
		return configurations.toArray(new IProcessConfiguration[configurations.size()]);
	}

}
