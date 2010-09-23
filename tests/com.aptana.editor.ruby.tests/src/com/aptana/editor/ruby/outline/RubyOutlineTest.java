package com.aptana.editor.ruby.outline;

import junit.framework.TestCase;

import com.aptana.editor.ruby.RubyEditorPlugin;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.parsing.ParseState;

public class RubyOutlineTest extends TestCase
{

	private RubyOutlineContentProvider fContentProvider;
	private RubyOutlineLabelProvider fLabelProvider;

	private RubyParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		fContentProvider = new RubyOutlineContentProvider();
		fLabelProvider = new RubyOutlineLabelProvider();
		fParser = new RubyParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (fContentProvider != null)
		{
			fContentProvider.dispose();
			fContentProvider = null;
		}
		if (fLabelProvider != null)
		{
			fLabelProvider.dispose();
			fLabelProvider = null;
		}
		fParser = null;
	}

	public void testBasic() throws Exception
	{
		String source = "class Test\n\tdef initialize(files)\n\t\t@files = files\n\tend\nend";
		ParseState parseState = new ParseState();
		parseState.setEditState(source, source, 0, 0);
		fParser.parse(parseState);

		Object[] elements = fContentProvider.getElements(parseState.getParseResult());
		assertEquals(1, elements.length); // class Test
		assertEquals("Test", fLabelProvider.getText(elements[0]));
		assertEquals(RubyEditorPlugin.getImage("icons/class_obj.png"), fLabelProvider.getImage(elements[0]));

		Object[] level1 = fContentProvider.getChildren(elements[0]); // initialize(files) and @files
		assertEquals(2, level1.length);
		assertEquals("initialize(files)", fLabelProvider.getText(level1[0]));
		assertEquals(RubyEditorPlugin.getImage("icons/method_protected_obj.png"), fLabelProvider.getImage(level1[0]));
		assertEquals("@files", fLabelProvider.getText(level1[1]));
		assertEquals(RubyEditorPlugin.getImage("icons/instance_var_obj.png"), fLabelProvider.getImage(level1[1]));

		Object[] level2 = fContentProvider.getChildren(level1[0]); // files
		assertEquals(1, level2.length);
		assertEquals("files", fLabelProvider.getText(level2[0]));
		assertEquals(RubyEditorPlugin.getImage("icons/local_var_obj.png"), fLabelProvider.getImage(level2[0]));

		level2 = fContentProvider.getChildren(level1[1]);
		assertEquals(0, level2.length);
	}
}
