/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.launchConfigurations;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * Launch configuration tab group
 */
public class JSLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
	 *      java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs;
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			tabs = new ILaunchConfigurationTab[] { new LaunchBrowserSettingsTab(), new HttpSettingsTab(),
					new CommonTab() };
		} else {
			tabs = new ILaunchConfigurationTab[] { new LaunchBrowserSettingsTab(), new HttpSettingsTab(),
					new AdvancedSettingsTab(), new CommonTab() };
		}
		setTabs(tabs);
	}
}
