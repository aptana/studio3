/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import org.osgi.framework.Bundle;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;

/**
 * CSSEditorBasedTests
 */
public class CSSEditorBasedTests extends EditorBasedTests<CSSContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected CSSContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new CSSContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return CSSPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getPluginId()
	 */
	@Override
	protected String getPluginId()
	{
		return CSSPlugin.PLUGIN_ID;
	}
}
