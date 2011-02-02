/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.internal.text;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;

import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class XMLFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		folder = null;
		super.tearDown();
	}

	public void testSingleLineOpenAndCloseTagDoesntFold() throws Exception
	{
		String src = "<root>some text</root>";
		folder = new XMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new XMLParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(0, positions.size());
	}
	
	public void testBasicXMLFolding() throws Exception
	{
		String src = "<root>\n<child>\n<name>Chris</name>\n<age>103</age>\n</child>\n</root>";
		folder = new XMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new XMLParser().parse(parseState);
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
		assertEquals(new Position(7, src.length() - 14), positions.get(1));
	}

	public void testXMLCommentFolding() throws Exception
	{
		String src = "<!--\n  This is a comment.\n -->\n";
		folder = new XMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new XMLParser().parse(parseState);
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
	
	public void testXMLCDATAFolding() throws Exception
	{
		String src = "<root>\n<![CDATA[\n  This is cdata.\n]]>\n</root>\n";
		folder = new XMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new XMLParser().parse(parseState);
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
		assertEquals(new Position(7, 31), positions.get(1));
	}	
	
	public void testCombinedXMLFolding() throws Exception
	{
		String src = "<yeah>\n<!--\n  This is a comment.\n -->\n<root>\n<![CDATA[\n  This is cdata.\n]]>\n</root>\n</yeah>";
		folder = new XMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get(), null, 0, 0);
				try
				{
					return new XMLParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		List<Position> positions = folder.emitFoldingRegions(new NullProgressMonitor());
		assertEquals(4, positions.size());
		assertEquals(new Position(0, src.length()), positions.get(0));
		assertEquals(new Position(7, 31), positions.get(1));
		assertEquals(new Position(38, 46), positions.get(2));
		assertEquals(new Position(45, 31), positions.get(3));
	}
}
