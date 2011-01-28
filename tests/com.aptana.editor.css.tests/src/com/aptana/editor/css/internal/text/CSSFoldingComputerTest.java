/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;

import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class CSSFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		folder = null;
		super.tearDown();
	}

	public void testBasicCSSFolding() throws Exception
	{
		String src = "body {\n" + "	color: red;\n" + "}\n" + "\n" + "div p {\n" + "	background-color: green;\n" + "}\n"
				+ "\n" + ".one-liner { color: orange; }\n" + "\n" + "#id { \n" + "	font-family: monospace;\n" + "}";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new CSSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(3, positions.size());
		assertEquals(new Position(0, 22), positions.get(0)); // eats whole line at end
		assertEquals(new Position(23, 36), positions.get(1)); // eats whole line at end
		assertEquals(new Position(91, 33), positions.get(2)); // only can go so far as EOF
	}

	public void testCSSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new CSSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0));
	}
	
	public void testMediaFolding() throws Exception
	{
		String src = "@media print {\n  body {\n    color: red;\n  }\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new CSSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(2, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0));
		assertEquals(new Position(17, 27), positions.get(1));
	}
	
	public void testPageFolding() throws Exception
	{
		String src = "@page {\n  margin: 3cm;\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new CSSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0));
	}
	
	public void testFontFaceFolding() throws Exception
	{
		String src = "@font-face {\n  font-family: Gentium;\n  src: url(http://site/fonts/Gentium.ttf);\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new CSSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(1, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0));
	}
}
