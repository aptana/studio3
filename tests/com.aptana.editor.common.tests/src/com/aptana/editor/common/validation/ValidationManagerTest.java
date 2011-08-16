/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.parsing.ParseState;

public class ValidationManagerTest extends AbstractValidatorTestCase
{
	private static final String JS_LANGUAGE = "com.aptana.contenttype.js";

	public void testValidationManager()
	{
		try
		{
			@SuppressWarnings("unused")
			ValidationManager vm = new ValidationManager(null);
			fail("Null File Service not allowed");
		}
		catch (IllegalArgumentException ex)
		{

		}
	}

	public void testIsIgnored()
	{
		String text = "INGNORED_TEXT";
		List<String> expressions = new ArrayList<String>();
		expressions.add(text);

		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		store.put(getFilterExpressionsPrefKey(JS_LANGUAGE),
				StringUtil.join("####", expressions.toArray(new String[expressions.size()])));

		FileService fileService = new FileService(JS_LANGUAGE, new ParseState());
		ValidationManager manager = (ValidationManager) fileService.getValidationManager();

		assertTrue(manager.isIgnored(text, JS_LANGUAGE));
	}

	public void testGetParseState()
	{
		ParseState ps = new ParseState();
		FileService fileService = new FileService(null, ps);
		IValidationManager validationManager = fileService.getValidationManager();
		assertEquals(ps, validationManager.getParseState());
	}

	public void testAddParseErrorsWithNullDocument() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n}";

		setEnableParseError(true, JS_LANGUAGE);

		List<IValidationItem> items = new ArrayList<IValidationItem>();
		TestProject project = new TestProject("Test", new String[] { "com.aptana.projects.webnature" });
		final IResource file = project.createFile("parseErrorTest", text);

		FileService fileService = new FileService(JS_LANGUAGE, new ParseState());

		fileService.setDocument(null);
		fileService.setResource(file);
		fileService.parse(new NullProgressMonitor());
		fileService.validate();

		ValidationManager validationManager = (ValidationManager) fileService.getValidationManager();
		Collection<List<IValidationItem>> validationLists = validationManager.getValidationItems();

		for (List<IValidationItem> list : validationLists)
		{
			items.addAll(list);
		}

		project.delete();

		assertEquals(0, items.size());
	}

	public void testAddParseErrorsWithPreferenceDisabled() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n}";

		setEnableParseError(false, JS_LANGUAGE);
		List<IValidationItem> items = getParseErrors(text, JS_LANGUAGE, new ParseState());
		assertEquals(0, items.size()); // Should be 0 since we don't have parse errors enabled
	}

	public void testNoErrorOrWarningOnLine() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n}";

		setEnableParseError(true, JS_LANGUAGE);
		List<IValidationItem> items = getParseErrors(text, JS_LANGUAGE, new ParseState());
		assertFalse(ValidationManager.hasErrorOrWarningOnLine(items, 0));

	}

	public void testHasErrorOrWarningOnLine() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n}";

		setEnableParseError(true, JS_LANGUAGE);
		List<IValidationItem> items = getParseErrors(text, JS_LANGUAGE, new ParseState());
		assertTrue(ValidationManager.hasErrorOrWarningOnLine(items, 3));
	}

	private static String getFilterExpressionsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.FILTER_EXPRESSIONS; //$NON-NLS-1$
	}

}
