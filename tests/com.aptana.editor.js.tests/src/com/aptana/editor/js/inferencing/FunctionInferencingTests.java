/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;

public class FunctionInferencingTests extends InferencingTestsBase
{

	public void testReturnsBoolean()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-boolean.js"), "Boolean");
	}

	public void testReturnsFunction()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-function.js"), "Function");
	}

	public void testReturnsNumber()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-number.js"), "Number");
	}

	public void testReturnsRegExp()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-regexp.js"), "RegExp");
	}

	public void testReturnsString()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-string.js"), "String");
	}

	public void testReturnsArray()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array.js"), "Array");
	}

	public void testReturnsArrayOfNumbers()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array-of-numbers.js"),
				"Array<Number>");
	}

	public void testReturnsObject()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-object.js"), "Object");
	}

	public void testReturnsUserObject()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-user-object.js"), "foo");
	}

	// https://jira.appcelerator.org/browse/APSTUD-4207
	public void testCreateTypeNamesBasedOnContext() throws Exception
	{
		String source = "var x = {};\nx.y = {};\nx.y.z = function () {}";
		TestProject project = null;
		try
		{
			// Create a test project and files
			project = new TestProject("APSTUD4207", new String[] { "com.aptana.projects.webnature" });
			IFile number = project.createFile("apstud4207_number.js", source);

			Index index = getIndexManager().getIndex(number.getProject().getLocationURI());

			// Index the file
			IFileStoreIndexingParticipant part = new JSFileIndexingParticipant();
			part.index(new BuildContext(number), index, null);

			// Now query for types
			JSIndexQueryHelper helper = new JSIndexQueryHelper();
			List<TypeElement> types = helper.getTypes(index);
			Map<String, TypeElement> blah = CollectionsUtil.mapFromValues(types, new IMap<TypeElement, String>()
			{
				public String map(TypeElement item)
				{
					return item.getName();
				}
			});

			// Now verify there's an "x" type and an "x.y" type
			assertTrue("Index doesn't contain type 'x'", blah.containsKey("x"));
			assertTrue("Index doesn't contain type 'x.y'", blah.containsKey("x.y"));

			// verify that 'x.y' type has a property 'z'
			TypeElement xy = blah.get("x.y");
			PropertyElement z = xy.getProperty("z");
			assertNotNull("property 'z' doesn't exist on type 'x.y'", z);
			// Verify that z has a Function type
			assertTrue("'z' property doesn't have a return type of 'Function'", z.getTypeNames().contains("Function"));
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	public void testConstructedArray() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-array.js"), "Array");
	}

	public void testConstructedBoolean() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-boolean.js"), "Boolean");
	}

	public void testConstructedDate() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-date.js"), "Date");
	}

	public void testConstructedError() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-error.js"), "Error");
	}

	public void testConstructedFunction() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-function.js"), "Function");
	}

	public void testConstructedNumber() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-number.js"), "Number");
	}

	public void testConstructedRegExp() throws Exception
	{
		EditorTestHelper.joinBackgroundActivities();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-regexp.js"), "RegExp");
	}
}
