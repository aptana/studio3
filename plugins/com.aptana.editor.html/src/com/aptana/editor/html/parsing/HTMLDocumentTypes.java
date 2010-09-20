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
package com.aptana.editor.html.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLDocumentTypes
{
	public enum Type
	{
		OTHER,
		HTML_2_0,
		HTML_3_2,
		HTML_4_0_1_STRICT,
		HTML_4_0_1_TRANSITIONAL,
		HTML_4_0_1_FRAMESET,
		HTML_5_0,
		XHTML_1_0_STRICT,
		XHTML_1_0_TRANSITIONAL,
		XHTML_1_0_FRAMESET,
		XHTML_1_1_STRICT,
	}

	private static final String HTML_2_0 = "-//IETF//DTD HTML//EN"; //$NON-NLS-1$
	private static final String HTML_3_2 = "-//W3C//DTD HTML 3.2 Final//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_STRICT = "-//W3C//DTD HTML 4.01//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_TRANSITIONAL = "-//W3C//DTD HTML 4.01 Transitional//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_FRAMESET = "-//W3C//DTD HTML 4.01 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_STRICT = "-//W3C//DTD XHTML 1.0 Strict//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_TRANSITIONAL = "-//W3C//DTD XHTML 1.0 Transitional//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_FRAMESET = "-//W3C//DTD XHTML 1.0 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_1_STRICT = "-//W3C//DTD XHTML 1.1//EN"; //$NON-NLS-1$

	private static final Pattern DOCTYPE_PATTERN = Pattern
			.compile("<!DOCTYPE\\s+(\\S+)\\s+PUBLIC\\s+((?:'[^']+')|(?:\"[^\"]+\"))(?:\\s+((?:'[^']+')|(?:\"[^\"]+\")))?"); //$NON-NLS-1$

	private static final Map<String, Type> fDocTypes;
	static
	{
		fDocTypes = new HashMap<String, Type>();
		fDocTypes.put(HTML_2_0, Type.HTML_2_0);
		fDocTypes.put(HTML_3_2, Type.HTML_3_2);
		fDocTypes.put(HTML_4_0_1_STRICT, Type.HTML_4_0_1_STRICT);
		fDocTypes.put(HTML_4_0_1_TRANSITIONAL, Type.HTML_4_0_1_TRANSITIONAL);
		fDocTypes.put(HTML_4_0_1_FRAMESET, Type.HTML_4_0_1_FRAMESET);
		fDocTypes.put(XHTML_1_0_STRICT, Type.XHTML_1_0_STRICT);
		fDocTypes.put(XHTML_1_0_TRANSITIONAL, Type.XHTML_1_0_TRANSITIONAL);
		fDocTypes.put(XHTML_1_0_FRAMESET, Type.XHTML_1_0_FRAMESET);
		fDocTypes.put(XHTML_1_1_STRICT, Type.XHTML_1_1_STRICT);
	}

	public static Type getType(String source)
	{
		// assumes we don't know the document type
		Type documentType = Type.OTHER;
		int indexOf = source.indexOf("<!DOCTYPE");//$NON-NLS-1$
		if (indexOf > -1)
		{
			Matcher match = DOCTYPE_PATTERN.matcher(source.substring(indexOf));

			if (match.find())
			{
				// grabs various pieces of the doctype string
				String rootElement = match.group(1);
				String pubId = match.group(2);

				// strips opening and closing quotes
				pubId = pubId.substring(1, pubId.length() - 1);

				// sees if we could determine the document type
				if (rootElement.equals("html") || rootElement.equals("HTML")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if (fDocTypes.containsKey(pubId))
					{
						documentType = fDocTypes.get(pubId);
					}
				}
			}
		}
		return documentType;
	}
}
