/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.ITextHover;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;

public abstract class CommonTextHover implements ITextHover
{
	/**
	 * Checks the common editor plugin to see if the user has enabled hovers on content assist
	 * 
	 * @return
	 */
	public Boolean isHoverEnabled()
	{
		IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.CONTENT_ASSIST_HOVER, true,
				scopes);
	}
}
