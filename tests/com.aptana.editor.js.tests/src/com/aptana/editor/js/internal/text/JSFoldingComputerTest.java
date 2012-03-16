/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.internal.text;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.preferences.IPreferenceConstants;
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
				parseState.setEditState(getDocument().get());
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
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
	}

	public void testJSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
	}

	public void testJSFunctionFolding() throws Exception
	{
		String src = "function listItems(itemList) \n" + //
				"{\n" + //
				"   document.write(\"<UL>\\n\")\n" + //
				"   for (i = 0;i < itemList.length;i++)\n" + //
				"   {\n" + //
				"      document.write(\"<LI>\" + itemList[i] + \"\\n\")\n" + //
				"   }\n" + //
				"   document.write(\"</UL>\\n\") \n" + //
				"} "; //
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(2, positions.size()); // FIXME We're getting one too many here. Probably need to check if one
											// already exists on this line!
		assertTrue(positions.contains(new Position(0, src.length()))); // eats whole line at end
		assertTrue(positions.contains(new Position(63, 96)));
	}

	public void testJSCommentInitiallyFolded() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		// Turn on initially folding comments
		EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_COMMENTS, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	public void testJSFunctionInitiallyFolded() throws Exception
	{
		String src = "function listItems(itemList) \n" + //
				"{\n" + //
				"   document.write(\"<UL>\\n\")\n" + //
				"   document.write(\"</UL>\\n\") \n" + //
				"} "; //
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		// Turn on initially folding functions
		EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_FUNCTIONS, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	public void testArrayInitiallyFolded() throws Exception
	{
		String src = "{\n" + //
				"    \"description\": [\n" + //
				"        \"event object\",\n" + //
				"        \"name\",\n" + //
				"        \"event\"\n" + //
				"    ]\n" + //
				"}"; //
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		// Turn on initially folding arrays
		EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_ARRAYS, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		ProjectionAnnotation annotation = getByPosition(annotations, new Position(21, 64));
		assertTrue(annotation.isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		annotation = getByPosition(annotations, new Position(21, 64));
		assertFalse(annotation.isCollapsed());
	}

	public void testObjectInitiallyFolded() throws Exception
	{
		String src = "object = {\n" + //
				"    \"description\": \"event\"\n" + //
				"};"; //
		folder = new JSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState();
				parseState.setEditState(getDocument().get());
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
		// Turn on initially folding objects
		EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_OBJECTS, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	private ProjectionAnnotation getByPosition(Map<ProjectionAnnotation, Position> annotations, Position position)
	{
		for (Map.Entry<ProjectionAnnotation, Position> entry : annotations.entrySet())
		{
			if (entry.getValue().equals(position))
			{
				return entry.getKey();
			}
		}
		return null;
	}
}
