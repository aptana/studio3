/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import org.osgi.framework.Bundle;

import com.aptana.css.core.index.CSSFileIndexingParticipant;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.EditorContentAssistBasedTests;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.index.core.IFileStoreIndexingParticipant;

/**
 * CSSEditorBasedTests
 */
public class CSSEditorBasedTests extends EditorContentAssistBasedTests<CSSContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.EditorContentAssistBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected CSSContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new CSSContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return CSSPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		return CSSPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new CSSFileIndexingParticipant();
	}
}
