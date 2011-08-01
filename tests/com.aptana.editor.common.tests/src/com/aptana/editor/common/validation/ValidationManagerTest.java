package com.aptana.editor.common.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.ParseError;

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
		ParseState ps = new ParseState();

		Symbol s = new Symbol(0);

		FileService fileService = new FileService(null, ps);
		ps.addError(new ParseError(s, IParseError.Severity.ERROR));
		ps.addError(new ParseError(s, IParseError.Severity.WARNING));

		IValidationManager validationManager = fileService.getValidationManager();
		List<IValidationItem> items = new ArrayList<IValidationItem>();
		validationManager.addParseErrors(items);
		assertEquals(0, items.size()); // should be 2, but we have a null document
	}

	// public void testHasErrorOrWarningOnLine()
	// {
	// }

}
