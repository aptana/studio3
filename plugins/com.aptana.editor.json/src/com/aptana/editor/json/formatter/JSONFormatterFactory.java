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
package com.aptana.editor.json.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.JSONSourceConfiguration;
import com.aptana.editor.json.JSONSourceViewerConfiguration;
import com.aptana.editor.json.preferences.IPreferenceConstants;
import com.aptana.editor.json.preferences.JSONFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

public class JSONFormatterFactory extends AbstractScriptFormatterFactory
{
	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(JSONPlugin.PLUGIN_ID,
			IPreferenceConstants.FORMATTER_ID);
	private static final String FORMATTER_PREVIEW_FILE = "formatting-preview.json"; //$NON-NLS-1$
	private static final PreferenceKey[] KEYS = { //
	new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_INDENTATION_SIZE), //
			new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_TAB_CHAR), //
			new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_TAB_SIZE) //
	};

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.IScriptFormatterFactory#createDialog(com.aptana.formatter.ui.IFormatterModifyDialogOwner)
	 */
	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new JSONFormatterModifyDialog(dialogOwner, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#createFormatter(java.lang.String, java.util.Map)
	 */
	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new JSONFormatter(lineSeparator, preferences, getMainContentType());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.IScriptFormatterFactory#createSimpleSourceViewerConfiguration(org.eclipse.jface.text.source
	 * .ISharedTextColors, org.eclipse.jface.preference.IPreferenceStore, org.eclipse.ui.texteditor.ITextEditor,
	 * boolean)
	 */
	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new JSONSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getFormatterPreferenceKey()
	 */
	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPreferenceKeys()
	 */
	public PreferenceKey[] getPreferenceKeys()
	{
		return KEYS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return JSONPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getPreviewContent()
	 */
	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return JSONSourceConfiguration.getDefault();
	}
}
