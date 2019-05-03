/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import org.osgi.framework.Bundle;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.tests.EditorContentAssistBasedTests;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor;

/**
 * @author klindsey
 */
public abstract class JSEditorBasedTestCase extends EditorContentAssistBasedTests<JSContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected JSContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new JSContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return JSPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		return JSPlugin.PLUGIN_ID;
	}
}
