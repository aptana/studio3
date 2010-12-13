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

package com.aptana.webserver.ui.preferences;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.util.SocketUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.preferences.IWebServerPreferenceConstants;

/**
 * @author Max Stepanov
 *
 */
public class LocalWebServerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Pattern PORTS_PATTERN = Pattern.compile("^(\\d+)(-(\\d+))?$"); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setMessage("Built-in Web Server Preferences");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		List<String[]> addresses = new ArrayList<String[]>();
		for (InetAddress i : SocketUtil.getLocalAddresses()) {
			addresses.add(new String[] { i.getHostAddress(), i.getHostAddress() });
		}
		addField(new ComboFieldEditor(IWebServerPreferenceConstants.PREF_HTTP_SERVER_ADDRESS, StringUtil.makeFormLabel("IP Address"), addresses.toArray(new String[addresses.size()][]), getFieldEditorParent()));
		addField(new StringFieldEditor(IWebServerPreferenceConstants.PREF_HTTP_SERVER_PORTS, StringUtil.makeFormLabel("Port(s)"), 11, getFieldEditorParent()) {
			{
				setErrorMessage("Enter a valid port number or range");
				setEmptyStringAllowed(true);
			}
			@Override
			protected boolean doCheckState() {
				Matcher matcher = PORTS_PATTERN.matcher(getStringValue());
				if (matcher.matches()) {
					try {
						int start = Integer.parseInt(matcher.group(1));
						if ( matcher.group(2) != null ) {
							int end = Integer.parseInt(matcher.group(3));
							if ( start < end ) {
								return true;
							}
						} else {
							return true;
						}
					} catch (NumberFormatException e) {
					}					
				}
				return false;
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(new InstanceScope(), WebServerCorePlugin.PLUGIN_ID);
	}


}
