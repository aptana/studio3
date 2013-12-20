/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;
import org.jruby.util.RegexpOptions;

public class RubyRegexpFolderTest
{

	private Ruby runtime;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		runtime = Ruby.newInstance();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		runtime = null;

//		super.tearDown();
	}

	@Test
	public void testBasicCSSFolding() throws Exception
	{
		String src = "body {\n" + "	color: red;\n" + "}\n" + "\n" + "div p {\n" + "	background-color: green;\n" + "}\n"
				+ "\n" + ".one-liner { color: orange; }\n" + "\n" + "#id { \n" + "	font-family: monospace;\n" + "}";
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(null, document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))",
						RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.css";
			}
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor(),
				null);
		Collection<Position> positions = annotations.values();
		assertEquals(3, positions.size());
		assertTrue(positions.contains(new Position(0, 22))); // eats whole line at end
		assertTrue(positions.contains(new Position(23, 36))); // eats whole line at end
		assertTrue(positions.contains(new Position(91, 33))); // only can go so far as EOF
	}

	@Test
	public void testScriptdocFolding() throws Exception
	{
		String src = "/**\n * This is a comment.\n **/\n";
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(null, document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "\\*+\\/|^\\s*\\}", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime,
						"\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$",
						RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor(),
				null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
	}

	@Test
	public void testJSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(null, document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "\\*+\\/|^\\s*\\}", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime,
						"\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$",
						RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor(),
				null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
	}

	@Test
	public void testJSFunctionFolding() throws Exception
	{
		String src = "function listItems(itemList) \n" + "{\n" + "   document.write(\"<UL>\\n\")\n"
				+ "   for (i = 0;i < itemList.length;i++)\n" + "   {\n"
				+ "      document.write(\"<LI>\" + itemList[i] + \"\\n\")\n" + "   }\n"
				+ "   document.write(\"</UL>\\n\") \n" + "} ";
		IDocument document = new Document(src);
		RubyRegexpFolder folder = new RubyRegexpFolder(null, document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "\\*+\\/|^\\s*\\}", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime,
						"\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$",
						RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor(),
				null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
	}
}
