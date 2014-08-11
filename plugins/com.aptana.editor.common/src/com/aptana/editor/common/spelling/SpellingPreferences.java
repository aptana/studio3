/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.spelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

/**
 * @author Max Stepanov
 */
public final class SpellingPreferences implements IPreferenceChangeListener
{

	private static final String PREF_DELEIMITER = ","; //$NON-NLS-1$
	private List<IScopeSelector> selectors;

	/**
	 *
	 */
	public SpellingPreferences()
	{
		InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(this);
	}

	public void dispose()
	{
		InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse
	 * .core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	public void preferenceChange(PreferenceChangeEvent event)
	{
		if (IPreferenceConstants.ENABLED_SPELLING_SCOPES.equals(event.getKey()))
		{
			selectors = null;
		}
	}

	public boolean isSpellingEnabledFor(QualifiedContentType contentType)
	{
		for (IScopeSelector selector : getEnabledSelectors())
		{
			if (selector.matches(contentType.getLastPart()))
			{
				return true;
			}
		}
		return false;
	}

	private List<IScopeSelector> getEnabledSelectors()
	{
		if (selectors == null)
		{
			selectors = new ArrayList<IScopeSelector>();
			for (String scope : getEnabledScopes())
			{
				selectors.add(new ScopeSelector(scope));
			}
		}
		return selectors;
	}

	/**
	 * Returns set of enabled scopes in preferences
	 * 
	 * @return
	 */
	public static Set<String> getEnabledScopes()
	{
		Set<String> result = new HashSet<String>();
		String enabledScopes = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.ENABLED_SPELLING_SCOPES, StringUtil.EMPTY,
				new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE });
		for (String scope : enabledScopes.split(PREF_DELEIMITER))
		{
			scope = scope.trim();
			if (scope.length() > 0)
			{
				result.add(scope);
			}
		}
		return result;
	}

	public static void setEnabledScopes(Collection<String> scopes)
	{
		StringBuilder sb = new StringBuilder();
		for (String scope : scopes)
		{
			scope = scope.trim();
			if (scope.length() > 0)
			{
				sb.append(scope).append(PREF_DELEIMITER);
			}
		}
		if (sb.length() > 0)
		{
			sb.setLength(sb.length() - 1);
		}
		InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID).put(IPreferenceConstants.ENABLED_SPELLING_SCOPES,
				sb.toString());
	}

}
