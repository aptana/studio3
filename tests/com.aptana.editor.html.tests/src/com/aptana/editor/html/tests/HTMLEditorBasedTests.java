/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import org.osgi.framework.Bundle;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor;

/**
 * HTMLEditorBasedTests
 */
public class HTMLEditorBasedTests extends EditorBasedTests<HTMLContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected HTMLContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new HTMLContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return HTMLPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getPluginId()
	 */
	@Override
	protected String getPluginId()
	{
		return HTMLPlugin.PLUGIN_ID;
	}
}
