package com.aptana.editor.erb.html.outline;

import junit.framework.TestCase;

import com.aptana.editor.erb.Activator;
import com.aptana.editor.erb.html.parsing.RHTMLParser;
import com.aptana.editor.html.parsing.HTMLParseState;

public class RHTMLOutlineTest extends TestCase
{

	private RHTMLParser fParser;
	private HTMLParseState fParseState;

	private RHTMLOutlineContentProvider fContentProvider;
	private RHTMLOutlineLabelProvider fLabelProvider;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new RHTMLParser();
		fParseState = new HTMLParseState();
		fContentProvider = new RHTMLOutlineContentProvider();
		fLabelProvider = new RHTMLOutlineLabelProvider(fParseState);
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		fParseState = null;
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
	}

	public void testBasic() throws Exception
	{
		String source = "<% content_for :stylesheets do %><style type=\"text/css\"></style><% end %>";
		fParseState.setEditState(source, source, 0, 0);
		fParser.parse(fParseState);

		Object[] elements = fContentProvider.getElements(fParseState.getParseResult());
		assertEquals(3, elements.length);
		assertEquals("<% content_for :styles... %>", fLabelProvider.getText(elements[0]));
		assertEquals(Activator.getImage("icons/embedded_code_fragment.png"), fLabelProvider.getImage(elements[0]));
		assertEquals("style", fLabelProvider.getText(elements[1]));
		assertEquals(com.aptana.editor.html.Activator.getImage("icons/element.png"),
				fLabelProvider.getImage(elements[1]));
		assertEquals("<% end %>", fLabelProvider.getText(elements[2]));
		assertEquals(Activator.getImage("icons/embedded_code_fragment.png"), fLabelProvider.getImage(elements[2]));
	}
}
