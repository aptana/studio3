/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.core.IMap;
import com.aptana.core.tests.TestProject;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexReader;

public class FunctionInferencingTest extends InferencingTestsBase
{

	@Test
	public void testReturnsBoolean()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-boolean.js"), "Boolean");
	}

	@Test
	public void testReturnsFunction()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-function.js"), "Function");
	}

	@Test
	public void testReturnsNumber()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-number.js"), "Number");
	}

	@Test
	public void testReturnsRegExp()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-regexp.js"), "RegExp");
	}

	@Test
	public void testReturnsString()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-string.js"), "String");
	}

	@Test
	public void testReturnsArray()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array.js"), "Array");
	}

	@Test
	public void testReturnsArrayOfNumbers()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array-of-numbers.js"),
				"Array<Number>");
	}

	@Test
	public void testReturnsObject()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-object.js"), "Object");
	}

	@Test
	public void testReturnsUserObject()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-user-object.js"), "foo");
	}

	// https://jira.appcelerator.org/browse/APSTUD-4207
	@Test
	public void testCreateTypeUsingObjectLiteralsAndChainedPropertyAccess() throws Exception
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
			JSIndexReader helper = new JSIndexReader();
			List<TypeElement> types = helper.getTypes(index, true);
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

	@Test
	public void testCreateTypeUsingNestedObjectLiterals() throws Exception
	{
		String source = "var x = { y: { z: function () {} } };";
		TestProject project = null;
		try
		{
			// Create a test project and files
			project = new TestProject("nested", new String[] { "com.aptana.projects.webnature" });
			IFile number = project.createFile("nested.js", source);

			Index index = getIndexManager().getIndex(number.getProject().getLocationURI());

			// Index the file
			IFileStoreIndexingParticipant part = new JSFileIndexingParticipant();
			part.index(new BuildContext(number), index, null);

			// Now query for types
			JSIndexReader helper = new JSIndexReader();
			List<TypeElement> types = helper.getTypes(index, true);
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

	// http://www.phpied.com/3-ways-to-define-a-javascript-class/
	@Test
	public void testCreateTypesWithConstructorUsingThisPropertyAssignment() throws Exception
	{
		String source = "function x() = { this.y = \"string\" };";
		TestProject project = null;
		try
		{
			// Create a test project and files
			project = new TestProject("nested", new String[] { "com.aptana.projects.webnature" });
			IFile number = project.createFile("nested.js", source);

			Index index = getIndexManager().getIndex(number.getProject().getLocationURI());

			// Index the file
			IFileStoreIndexingParticipant part = new JSFileIndexingParticipant();
			part.index(new BuildContext(number), index, null);

			// Now query for types
			JSIndexReader helper = new JSIndexReader();
			List<TypeElement> types = helper.getTypes(index, true);
			Map<String, TypeElement> blah = CollectionsUtil.mapFromValues(types, new IMap<TypeElement, String>()
			{
				public String map(TypeElement item)
				{
					return item.getName();
				}
			});

			// Now verify there's an "x" type
			assertTrue("Index doesn't contain type 'Function<x>'", blah.containsKey("Function<x>"));

			// verify that 'x' type has a property 'y'
			TypeElement x = blah.get("Function<x>");
			PropertyElement y = x.getProperty("y");
			assertNotNull("property 'y' doesn't exist on type 'Function<x>'", y);
			// Verify that z has a Function type
			assertTrue("'y' property doesn't have a return type of 'String'", y.getTypeNames().contains("String"));
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	@Test
	public void testCreateTypeWhenFunctionConstructorDeclaresThisProperties() throws Exception
	{
		String source = "var x = new function() { this.color = \"red\"; }";
		TestProject project = null;
		try
		{
			// Create a test project and files
			project = new TestProject("TISTUD-6735", new String[] { "com.aptana.projects.webnature" });
			IFile number = project.createFile("tistud6735_apple.js", source);

			Index index = getIndexManager().getIndex(number.getProject().getLocationURI());

			// Index the file
			IFileStoreIndexingParticipant part = new JSFileIndexingParticipant();
			part.index(new BuildContext(number), index, null);

			// Now query for types
			JSIndexReader helper = new JSIndexReader();
			List<TypeElement> types = helper.getTypes(index, true);
			System.out.println(types);
			Map<String, TypeElement> blah = CollectionsUtil.mapFromValues(types,
					new IMap<TypeElement, String>()
					{
						public String map(TypeElement item)
						{
							return item.getName();
						}
					});

			// Now verify there's an "x" type
			assertTrue("Index doesn't contain type 'Function<x>'", blah.containsKey("Function<x>"));

			// verify that 'x' type has a property 'y'
			TypeElement x = blah.get("Function<x>");
			PropertyElement y = x.getProperty("y");
			assertNotNull("property 'y' doesn't exist on type 'Function<x>'", y);
			// Verify that z has a Function type
			assertTrue("'y' property doesn't have a return type of 'String'", y.getTypeNames().contains("String"));
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	@Test
	public void testConstructedArray() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-array.js"), "Array");
	}

	@Test
	public void testConstructedBoolean() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-boolean.js"), "Boolean");
	}

	@Test
	public void testConstructedDate() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-date.js"), "Date");
	}

	@Test
	public void testConstructedError() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-error.js"), "Error");
	}

	@Test
	public void testConstructedFunction() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-function.js"), "Function");
	}

	@Test
	public void testConstructedNumber() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-number.js"), "Number");
	}

	@Test
	public void testConstructedRegExp() throws Exception
	{
		loadJSMetadata();
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/constructed-regexp.js"), "RegExp");
	}
}
