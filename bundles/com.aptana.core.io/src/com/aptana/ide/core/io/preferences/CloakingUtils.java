/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable questionableAssignment

package com.aptana.ide.core.io.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class CloakingUtils
{

	/**
	 * Adds a file type to be cloaked.
	 * 
	 * @param filetype
	 *            the file type
	 */
	public static void addCloakFileType(String filetype)
	{
		List<String> newList = new ArrayList<String>();
		String[] filetypes = getCloakedFileTypes();
		boolean found = false;
		for (String extension : filetypes)
		{
			if (extension.equals(filetype))
			{
				found = true;
			}
			newList.add(extension);
		}
		if (!found)
		{
			newList.add(filetype);
		}

		setCloakedFileTypes(newList.toArray(new String[newList.size()]));
	}

	/**
	 * Removes a file type from being cloaked.
	 * 
	 * @param filetype
	 *            the file type
	 */
	public static void removeCloakFileType(String filetype)
	{
		List<String> newList = new ArrayList<String>();
		String[] filetypes = getCloakedFileTypes();
		for (String extension : filetypes)
		{
			if (extension.equals(filetype))
			{
				continue;
			}
			newList.add(extension);
		}

		setCloakedFileTypes(newList.toArray(new String[newList.size()]));
	}

	/**
	 * @param fileStore
	 *            the file store to be checked
	 * @return true if the file should be cloaked, false otherwise
	 */
	public static boolean isFileCloaked(IFileStore fileStore)
	{
		String filename = fileStore.getName();
		String filepath = fileStore.toString();
		String[] expressions = getCloakedExpressions();
		for (String expression : expressions)
		{
			if (filename.matches(expression) || filepath.matches(expression))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the array of filetypes being cloaked in regular expression
	 */
	private static String[] getCloakedExpressions()
	{
		String[] filetypes = getCloakedFileTypes();
		String[] expressions = new String[filetypes.length];
		for (int i = 0; i < expressions.length; ++i)
		{
			expressions[i] = convertCloakExpressionToRegex(filetypes[i]);
		}
		return expressions;
	}

	private static String convertCloakExpressionToRegex(String expression)
	{
		if (expression == null)
		{
			return null;
		}
		String result = null;

		if (expression.length() > 1 && expression.charAt(0) == '/' && expression.charAt(expression.length() - 1) == '/')
		{
			// already an regular expression
			return expression.substring(1, expression.length() - 1);
		}

		if (expression.contains("\\")) { //$NON-NLS-1$
			expression = expression.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// escape all '.' characters which aren't followed by '*'
		result = expression.replaceAll("\\.(?=[^\\*])", "\\\\."); //$NON-NLS-1$//$NON-NLS-2$

		// convert all '.*' to regular expression
		result = result.replaceAll("\\.\\*", "^\\\\..*"); //$NON-NLS-1$ //$NON-NLS-2$

		// convert all '*' characters that are not preceded by '.' to ".*"
		result = "(?i)" + result.replaceAll("(?<!\\.)\\*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return result;
	}

	public static String[] getDefaultCloakedFileTypes()
	{
		return PreferenceInitializer.DEFAULT_CLOAK_EXPRESSIONS.split(";"); //$NON-NLS-1$
	}

	public static String[] getCloakedFileTypes()
	{
		String extensions = Platform.getPreferencesService().getString(CoreIOPlugin.PLUGIN_ID,
				IPreferenceConstants.GLOBAL_CLOAKING_EXTENSIONS, PreferenceInitializer.DEFAULT_CLOAK_EXPRESSIONS, null);
		if (StringUtil.EMPTY.equals(extensions))
		{
			return ArrayUtil.NO_STRINGS;
		}
		return extensions.split(";"); //$NON-NLS-1$
	}

	public static void setCloakedFileTypes(String[] filetypes)
	{
		String value = StringUtil.join(";", filetypes); //$NON-NLS-1$
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CoreIOPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.GLOBAL_CLOAKING_EXTENSIONS, value);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
	}
}
