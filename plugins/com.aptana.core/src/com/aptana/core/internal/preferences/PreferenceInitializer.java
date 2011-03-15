/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;

/**
 * @author Max Stepanov
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = new DefaultScope().getNode(CorePlugin.PLUGIN_ID);
		preferences.put(ICorePreferenceConstants.PREF_WEB_FILES, "*.js;*.htm;*.html;*.xhtm;*.xhtml;*.css;*.xml;*.xsl;*.xslt;*.fla;*.gif;*.jpg;*.jpeg;*.php;*.asp;*.jsp;*.png;*.as;*.sdoc;*.swf;*.shtml;*.txt;*.aspx;*.asmx;"); //$NON-NLS-1$
	}

}
