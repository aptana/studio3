/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.IJSDebugUIConstants;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class JSDebugUIPreferenceInitializer extends AbstractPreferenceInitializer
{
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(JSDebugUIPlugin.PLUGIN_ID);

		// default preferences
		node.putBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, true);
		node.put(IJSDebugUIConstants.PREF_SHOW_DETAILS, IJSDebugUIConstants.DETAIL_PANE);
		node.putBoolean(IJSDebugUIConstants.PREF_SHOW_CONSTANTS, false);
		node.put(IJSDebugUIConstants.CONSOLE_WARN_COLOR, StringConverter.asString(new RGB(255, 215, 0)));

		// override default org.eclipse.debug.ui options
		node = DefaultScope.INSTANCE.getNode(DebugUIPlugin.getDefault().getBundle().getSymbolicName());
		if (MessageDialogWithToggle.NEVER.equals(node.get(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE,
				StringUtil.EMPTY)))
		{
			node.put(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE, MessageDialogWithToggle.PROMPT);
		}
	}
}
