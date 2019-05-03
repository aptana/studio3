/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.tests.EditorContentAssistBasedTests;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor;
import com.aptana.editor.html.contentassist.index.HTMLFileIndexingParticipant;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.testing.utils.TestProject;

/**
 * HTMLEditorBasedTests
 */
public class HTMLEditorBasedTests extends EditorContentAssistBasedTests<HTMLContentAssistProcessor>
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#createContentAssistProcessor(com.aptana.editor.common.
	 * AbstractThemeableEditor)
	 */
	@Override
	protected HTMLContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new HTMLContentAssistProcessor(editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return HTMLPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		return HTMLPlugin.PLUGIN_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorContentAssistBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new HTMLFileIndexingParticipant();
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

		project.createFile("file.html", "");
		project.createFile("root.css", "");
		project.createFolder("folder");
		project.createFile("folder/inside_folder.css", "");

		return project;
	}

}
