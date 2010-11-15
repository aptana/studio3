/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.terminal.IProcessConfiguration;

/**
 * @author Max Stepanov
 *
 */
public final class ProcessConfigurations {

	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".processConfigurations"; //$NON-NLS-1$
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
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
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
					Activator.log("Process configuration instantiation failed.", e); //$NON-NLS-1$
				}
			}
		}
	}
	
	public IProcessConfiguration[] getProcessConfigurations() {
		return configurations.toArray(new IProcessConfiguration[configurations.size()]);
	}

}
