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
		RubyRegexpFolder folder = new RubyRegexpFolder(document)
		{
			@Override
			protected RubyRegexp getEndFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "(?<!\\*)\\*\\*\\/|^\\s*\\}", 0);
			}

			@Override
			protected RubyRegexp getStartFoldRegexp(String scope)
			{
				return RubyRegexp.newRegexp(runtime, "\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))", 0);
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
}
