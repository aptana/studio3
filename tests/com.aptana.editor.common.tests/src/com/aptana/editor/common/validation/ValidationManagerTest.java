package com.aptana.editor.common.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.ValidationItem;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.parsing.ParseState;

public class ValidationManagerTest extends TestCase
{
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

	// public void testDispose()
	// {
	// }
	//
	// public void testSetDocument()
	// {
	// }
	//
	// public void testSetResource()
	// {
	// }
	//
	// public void testValidate()
	// {
	// }
	//
	// public void testCreateError()
	// {
	// }
	//
	// public void testCreateWarning()
	// {
	// }
	//
	// public void testAddNestedLanguage()
	// {
	// }
	//
	// public void testIsIgnored()
	// {
	// //
	// }

	public void testGetParseState()
	{
		ParseState ps = new ParseState();
		FileService fileService = new FileService(null, ps);
		IValidationManager validationManager = fileService.getValidationManager();
		assertEquals(ps, validationManager.getParseState());
	}

	public void testAddParseErrors()
	{
		FileService fileService = new FileService(null, new ParseState());
		IValidationManager validationManager = fileService.getValidationManager();
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		items.add(new ValidationItem(0, "message", 0, 0, 0, "test.js"));
		validationManager.addParseErrors(items);
	}

	// public void testHasErrorOrWarningOnLine()
	// {
	// }

}
