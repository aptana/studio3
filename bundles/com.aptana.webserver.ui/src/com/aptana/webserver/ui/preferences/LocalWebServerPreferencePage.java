/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.SocketUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.preferences.IWebServerPreferenceConstants;
import com.aptana.webserver.ui.WebServerUIPlugin;

/**
 * @author Max Stepanov
 */
public class LocalWebServerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private static final Pattern PORTS_PATTERN = Pattern.compile("^(\\d+)(-(\\d+))?$"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setMessage(Messages.LocalWebServerPreferencePage_Message);
		setDescription(Messages.LocalWebServerPreferencePage_Description);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors()
	{
		List<String[]> addresses = new ArrayList<String[]>();
		for (InetAddress i : SocketUtil.getLocalAddresses())
		{
			addresses.add(new String[] { i.getHostAddress(), i.getHostAddress() });
		}
		addField(new ComboFieldEditor(IWebServerPreferenceConstants.PREF_HTTP_SERVER_ADDRESS,
				StringUtil.makeFormLabel(Messages.LocalWebServerPreferencePage_Address_Label),
				addresses.toArray(new String[addresses.size()][]), getFieldEditorParent()));
		addField(new StringFieldEditor(IWebServerPreferenceConstants.PREF_HTTP_SERVER_PORTS,
				StringUtil.makeFormLabel(Messages.LocalWebServerPreferencePage_Port_Label), 11, getFieldEditorParent())
		{
			{
				setErrorMessage(Messages.LocalWebServerPreferencePage_PortError_Message);
				setEmptyStringAllowed(true);
			}

			@Override
			protected boolean doCheckState()
			{
				Matcher matcher = PORTS_PATTERN.matcher(getStringValue());
				if (matcher.matches())
				{
					try
					{
						int start = Integer.parseInt(matcher.group(1));
						if (matcher.group(2) != null)
						{
							int end = Integer.parseInt(matcher.group(3));
							if (start < end)
							{
								return true;
							}
						}
						else
						{
							return true;
						}
					}
					catch (NumberFormatException e)
					{
						IdeLog.logError(WebServerUIPlugin.getDefault(), e);
					}
				}
				return false;
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, WebServerCorePlugin.PLUGIN_ID);
	}

}
