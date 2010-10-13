/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
