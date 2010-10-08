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

import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.erb.Activator;
import com.aptana.editor.erb.html.parsing.ERBScript;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;

public class RHTMLOutlineLabelProvider extends HTMLOutlineLabelProvider
{

	private static final Image ERB_ICON = Activator.getImage("icons/embedded_code_fragment.png"); //$NON-NLS-1$

	private static final int TRIM_TO_LENGTH = 20;

	private IParseState fParseState;

	public RHTMLOutlineLabelProvider(IParseState parseState)
	{
		fParseState = parseState;
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineLabelProvider());
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			if (node instanceof ERBScript)
			{
				return ERB_ICON;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			if (node instanceof ERBScript)
			{
				return getDisplayText((ERBScript) node);
			}
		}
		return super.getText(element);
	}

	private String getDisplayText(ERBScript script)
	{
		StringBuilder text = new StringBuilder();
		text.append(script.getStartTag()).append(" "); //$NON-NLS-1$
		String source = new String(fParseState.getSource());
		// locates the ruby source
		IRubyScript ruby = script.getScript();
		source = source.substring(ruby.getStartingOffset(), ruby.getEndingOffset());
		// gets the first line of the ruby source
		StringTokenizer st = new StringTokenizer(source, "\n\r\f"); //$NON-NLS-1$
		source = st.nextToken();
		if (source.length() <= TRIM_TO_LENGTH)
		{
			text.append(source);
		}
		else
		{
			text.append(source.substring(0, TRIM_TO_LENGTH - 1)).append("..."); //$NON-NLS-1$
		}
		text.append(" ").append(script.getEndTag()); //$NON-NLS-1$
		return text.toString();
	}
}
