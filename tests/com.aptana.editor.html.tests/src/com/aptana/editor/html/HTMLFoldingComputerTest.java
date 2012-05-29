package com.aptana.editor.html;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		folder = null;
		super.tearDown();
	}

	public void testAPSTUD3151_JSComment() throws Exception
	{
		String src = "<!DOCTYPE html>\n" + //
				"<html>\n" + //
				"  <head>\n" + //
				"    <script>\n" + //
				"/* This is a multiline\n" + //
				" * comment\n" + //
				" */\n" + //
				"    </script>\n" + //
				"  </head>\n" + //
				"</html>\n";
		Map<ProjectionAnnotation, Position> annotations = fold(src);
		Collection<Position> positions = annotations.values();
		assertEquals(4, positions.size());
		// html
		assertTrue(positions.contains(new Position(16, 99))); // eats whole line at end
		// head
		assertTrue(positions.contains(new Position(25, 82)));
		// script
		assertTrue(positions.contains(new Position(36, 61)));

		// doesn't mess up JS comment folding
		assertTrue(positions.contains(new Position(45, 38)));
		assertFalse(positions.contains(new Position(1, 44)));
	}

	public void testAPSTUD3151_CSSComment() throws Exception
	{
		String src = "<!DOCTYPE html>\n" + //
				"<html>\n" + //
				"  <head>\n" + //
				"    <style>\n" + //
				"/* This is a multiline\n" + //
				" * comment\n" + //
				" */\n" + //
				"    </style>\n" + //
				"  </head>\n" + //
				"</html>\n";
		Map<ProjectionAnnotation, Position> annotations = fold(src);
		Collection<Position> positions = annotations.values();
		assertEquals(4, positions.size());
		// html
		assertTrue(positions.contains(new Position(16, 97))); // eats whole line at end
		// head
		assertTrue(positions.contains(new Position(25, 80)));
		// style
		assertTrue(positions.contains(new Position(36, 59)));

		// doesn't mess up CSS comment folding
		assertTrue(positions.contains(new Position(44, 38)));
		assertFalse(positions.contains(new Position(1, 44)));
	}

	protected Map<ProjectionAnnotation, Position> fold(String src) throws BadLocationException
	{
		folder = new HTMLFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new HTMLParseState(getDocument().get());
				try
				{
					return new HTMLParser().parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		return annotations;
	}
}
