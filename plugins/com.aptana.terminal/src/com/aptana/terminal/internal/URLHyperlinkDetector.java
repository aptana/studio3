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
package com.aptana.terminal.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.tm.terminal.model.IHyperlinkDetector;

/**
 * Detects URLs or bare hostnames like www.cnn.com and generates hyperlinks for them.
 * 
 * @author cwilliams
 */
public class URLHyperlinkDetector implements IHyperlinkDetector
{

	// Base URL detection
	// private static final Pattern URL_DETECT_PATTERN = Pattern
	//			.compile("\\b(https?|ftp|file)://[\\-A-Z0-9\\+&@#/%\\?=~_\\|!:,\\.;]*[A-Z0-9\\+&@#/%=~_\\|]"); //$NON-NLS-1$

	/**
	 * Detect URLs with protocol, or bare hostnames
	 */
	@SuppressWarnings("nls")
	private static final Pattern URL_DETECT_PATTERN = Pattern.compile("\\b\n"
			+ "  # Match the leading part (proto://hostname, or just hostname)\n" + "  (\n"
			+ "    # http://, or https:// leading part\n" + "    (https?)://[-\\w]+(\\.\\w[-\\w]*)+\n" + "  |\n"
			+ "    # or, try to find a hostname with more specific sub-expression\n"
			+ "    (?i: [a-z0-9] (?:[-a-z0-9]*[a-z0-9])? \\. )+ # sub domains\n"
			+ "    # Now ending .com, etc. For these, require lowercase\n" + "    (?-i: com\\b\n"
			+ "        | edu\\b\n" + "        | biz\\b\n" + "        | gov\\b\n"
			+ "        | in(?:t|fo)\\b # .int or .info\n" + "        | mil\\b\n" + "        | net\\b\n"
			+ "        | org\\b\n" + "        | [a-z][a-z]\\.[a-z][a-z]\\b # two-letter country code\n" + "    )\n"
			+ "  )\n" + "\n" + "  # Allow an optional port number\n" + "  ( : \\d+ )?\n" + "		  \n"
			+ "  # The rest of the URL is optional, and begins with /\n" + "  (\n" + "    /\n"
			+ "    # The rest are heuristics for what seems to work well\n"
			+ "    [^.!,?;\"\\'<>()\\[\\]\\{\\}\\s\\x7F-\\xFF]*\n" + "    (\n"
			+ "      [.!,?]+ [^.!,?;\"\\'<>()\\[\\]\\{\\}\\s\\x7F-\\xFF]+\n" + "    )*\n" + "  )?",
			Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);

	public IHyperlink[] detectHyperlinks(String contents)
	{
		List<IHyperlink> list = new ArrayList<IHyperlink>();
		Matcher m = URL_DETECT_PATTERN.matcher(contents);
		int start = 0;
		while (m.find(start))
		{
			String urlString = new String(m.group().trim());
			start = m.end();
			IRegion region = new Region(m.start(), urlString.length());
			if (!urlString.startsWith("http://")) //$NON-NLS-1$
			{
				urlString = "http://" + urlString; //$NON-NLS-1$
			}
			list.add(new URLHyperlink(region, urlString));
		}
		return list.toArray(new IHyperlink[0]);
	}

}
