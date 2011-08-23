/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validation;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.text.Document;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.parsing.IParseState;

public class AbstractValidatorTestCase extends TestCase
{

	protected void setEnableParseError(boolean enabled, String language)
	{
		// Set enable parse errors preference
		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		store.putBoolean(getEnableParseErrorPrefKey(language), enabled);
	}

	protected List<IValidationItem> getParseErrors(String source, String language, IParseState ps) throws CoreException
	{
		TestProject project = new TestProject("Test", new String[] { "com.aptana.projects.webnature" });
		IResource file = project.createFile("parseErrorTest", source);

		FileService fileService = new FileService(language, ps);
		fileService.setDocument(new Document(source));
		fileService.setResource(file);
		fileService.parse(new NullProgressMonitor());
		fileService.validate();

		IValidationManager validationManager = fileService.getValidationManager();
		List<IValidationItem> items = validationManager.getValidationItems();

		project.delete();
		return items;
	}

	protected String getEnableParseErrorPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.PARSE_ERROR_ENABLED;
	}
}
