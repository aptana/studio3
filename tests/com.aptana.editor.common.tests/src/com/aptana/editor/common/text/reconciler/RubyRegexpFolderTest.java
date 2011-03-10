/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;
import org.jruby.util.RegexpOptions;

public class RubyRegexpFolderTest extends TestCase
{

	private Ruby runtime;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		runtime = Ruby.newInstance();
	}

	@Override
	protected void tearDown() throws Exception
	{
		runtime = null;

		super.tearDown();
	}

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
				return RubyRegexp.newRegexp(runtime, "\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.css";
			}
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(3, positions.size());
		assertEquals(new Position(0, 22), positions.get(0)); // eats whole line at end
		assertEquals(new Position(23, 36), positions.get(1)); // eats whole line at end
		assertEquals(new Position(91, 33), positions.get(2)); // only can go so far as EOF
	}
	
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
				return RubyRegexp.newRegexp(runtime, "\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
	}
	
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
				return RubyRegexp.newRegexp(runtime, "\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
	}
	
	public void testJSFunctionFolding() throws Exception
	{
		String src = "function listItems(itemList) \n" +
"{\n" +
"   document.write(\"<UL>\\n\")\n" +
"   for (i = 0;i < itemList.length;i++)\n" +
"   {\n" +
"      document.write(\"<LI>\" + itemList[i] + \"\\n\")\n" +
"   }\n" +
"   document.write(\"</UL>\\n\") \n" +
"} ";
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
				return RubyRegexp.newRegexp(runtime, "\\/\\*+|^.*\\bfunction\\s*(\\w+\\s*)?\\([^\\)]*\\)(\\s*\\{[^\\}]*)?\\s*$", RegexpOptions.NULL_OPTIONS);
			}

			@Override
			protected String getScopeAtOffset(int offset) throws BadLocationException
			{
				return "source.js";
			}
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
	}
}
