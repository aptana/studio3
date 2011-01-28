/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.internal.text;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;

import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class JSFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		folder = null;
		super.tearDown();
	}

	public void testScriptdocFolding() throws Exception
	{
		String src = "/**\n * This is a comment.\n **/\n";
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new JSParser().parse(parseState);
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
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
	}

	public void testJSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new JSParser().parse(parseState);
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
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
	}

	public void testJSFunctionFolding() throws Exception
	{
		String src = "function listItems(itemList) \n" + "{\n" + "   document.write(\"<UL>\\n\")\n"
				+ "   for (i = 0;i < itemList.length;i++)\n" + "   {\n"
				+ "      document.write(\"<LI>\" + itemList[i] + \"\\n\")\n" + "   }\n"
				+ "   document.write(\"</UL>\\n\") \n" + "} ";
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new JSParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(2, positions.size()); // FIXME We're getting one too many here. Probably need to check if one already exists on this line!
		assertEquals(new Position(0, src.length()), positions.get(0)); // eats whole line at end
		assertEquals(new Position(63, 96), positions.get(1));
	}
}
