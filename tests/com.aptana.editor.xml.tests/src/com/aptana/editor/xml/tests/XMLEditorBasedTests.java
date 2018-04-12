/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.tests;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.EditorContentAssistBasedTests;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.contentassist.XMLContentAssistProcessor;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.testing.utils.TestProject;

/**
 * XMLEditorBasedTests
 */
public class XMLEditorBasedTests extends EditorContentAssistBasedTests<XMLContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.EditorContentAssistBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected XMLContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new XMLContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return XMLPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		return XMLPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return null;
	}

	/**
	 * Create a sample web project
	 * 
	 * @param projectNamePrefix
	 * @return
	 * @throws CoreException
	 */
	protected TestProject createWebProject(String projectNamePrefix) throws CoreException
	{
		TestProject project = new TestProject(projectNamePrefix, new String[] { "com.aptana.projects.webnature" });

		project.createFile("file.xml", "");

		return project;
	}

}
