package com.aptana.editor.coffee.internal.text;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.coffee.parsing.CoffeeParser;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class CoffeeFoldingComputerTest extends TestCase
{
	private IFoldingComputer folder;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception
	{
		try
		{
			// EclipseUtil.instanceScope().getNode(CoffeeEditorPlugin.PLUGIN_ID).remove(IPreferenceConstants.INITIALLY_FOLD_COMMENTS);
		}
		finally
		{
			folder = null;
			super.tearDown();
		}
	}

	protected synchronized IFoldingComputer getFoldingComputer(IDocument document)
	{
		if (folder == null)
		{
			folder = new CoffeeFoldingComputer(null, document)
			{
				@Override
				protected IParseNode getAST()
				{
					IParseState parseState = new ParseState();
					parseState.setEditState(getDocument().get(), null, 0, 0);
					try
					{
						return new CoffeeParser().parse(parseState);
					}
					catch (Exception e)
					{
						fail(e.getMessage());
					}
					return null;
				};
			};
		}
		return folder;
	}

	@Test
	public void testFoldingObject() throws Exception
	{
		String src = "math =\n" + //
				"  root:   Math.sqrt\n" + //
				"  square: square\n" + //
				"  cube:   (x) -> x * square x";
		IDocument document = new Document(src);
		Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length()))); // only can go so far as EOF
	}

	@Test
	public void testFoldingFunction() throws Exception
	{
		String src = "race = (winner, runners...) ->\n" + //
				"  print winner, runners"; //
		IDocument document = new Document(src);
		Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		// folding from args to end of function body
		assertTrue("Folding incorrect for anonymous function", positions.contains(new Position(7, src.length() - 7)));
	}

	@Test
	public void testFoldingIndentedObjectWithAssignment() throws Exception
	{
		String src = "kids =\n" + //
				"  brother:\n" + //
				"    name: \"Max\"\n" + //
				"    age:  11\n" + //
				"  sister:\n" + //
				"    name: \"Ida\"\n" + //
				"    age:  9"; //
		IDocument document = new Document(src);
		Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
		Collection<Position> positions = annotations.values();
		assertEquals(3, positions.size());
		assertTrue("Folding incorrect for 'kids' object", positions.contains(new Position(0, src.length())));
		assertTrue("Folding incorrect for 'brother'", positions.contains(new Position(9, 38)));
		assertTrue("Folding incorrect for 'sister'", positions.contains(new Position(49, src.length() - 49)));
	}

	@Test
	public void testMultilineArrayLiteral() throws Exception
	{
		String src = "bitlist = [\n" + //
				"  1, 0, 1\n" + //
				"  0, 0, 1\n" + //
				"  1, 1, 0\n" + //
				"]"; //
		IDocument document = new Document(src);
		Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue("Folding incorrect for multieline array literal", positions.contains(new Position(0, src.length())));
	}

	@Test
	public void testIfBody() throws Exception
	{
		String src = "if this.studyingEconomics\n" + //
				"  buy()  while supply > demand\n" + //
				"  sell() until supply > demand"; //
		IDocument document = new Document(src);
		Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue("Folding incorrect for if block", positions.contains(new Position(0, src.length())));
	}

	// TODO Do we want folding on try/catch/finally blocks?
	// @Test
	// public void testTryCatch() throws Exception
	// {
	// String src = "alert(\n" + //
	// "  try\n" + //
	// "    nonexistent / undefined\n" + //
	// "  catch error\n" + //
	// "    \"And the error is ... \" + error\n" + //
	// ")"; //
	// IDocument document = new Document(src);
	// Map<ProjectionAnnotation, Position> annotations = getFoldingComputer(document).emitFoldingRegions(true, null);
	// Collection<Position> positions = annotations.values();
	// assertEquals(2, positions.size());
	// assertTrue("Folding incorrect for try block", positions.contains(new Position(9, 32)));
	// assertTrue("Folding incorrect for catch block", positions.contains(new Position(43, 48)));
	// }

}
