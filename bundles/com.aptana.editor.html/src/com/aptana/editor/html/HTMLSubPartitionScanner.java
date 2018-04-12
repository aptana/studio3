/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.PartitionScannerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositeSubPartitionScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.html.parsing.HTMLUtils;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.svg.SVGSourceConfiguration;

/**
 * @author Max Stepanov
 */
public class HTMLSubPartitionScanner extends CompositeSubPartitionScanner
{

	private static final int TYPE_JS = 1;
	private static final int TYPE_CSS = 2;
	private static final int TYPE_SVG = 3;

	private static final String[] JS_SWITCH_SEQUENCES = new String[] { "</script>" }; //$NON-NLS-1$
	private static final String[] CSS_SWITCH_SEQUENCES = new String[] { "</style>" }; //$NON-NLS-1$
	private static final String[] SVG_SWITCH_SEQUENCES = new String[] { "</svg>" }; //$NON-NLS-1$

	/**
	 * HTMLSubPartitionScanner
	 */
	public HTMLSubPartitionScanner()
	{
		// @formatter:off
		super(
				new ISubPartitionScanner[] {
				new SubPartitionScanner(
						HTMLSourceConfiguration.getDefault().getPartitioningRules(),
						HTMLSourceConfiguration.CONTENT_TYPES,
						new Token(HTMLSourceConfiguration.DEFAULT)
						),
						JSSourceConfiguration.getDefault().createSubPartitionScanner(),
						CSSSourceConfiguration.getDefault().createSubPartitionScanner(),
						SVGSourceConfiguration.getDefault().createSubPartitionScanner()
				},
				new IPartitionScannerSwitchStrategy[] {
				new PartitionScannerSwitchStrategy(JS_SWITCH_SEQUENCES),
						new PartitionScannerSwitchStrategy(CSS_SWITCH_SEQUENCES),
						new PartitionScannerSwitchStrategy(SVG_SWITCH_SEQUENCES)
				}
		);
		// @formatter:on
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CompositeSubPartitionScanner#setLastToken(org .eclipse.jface.text.rules.IToken)
	 */
	@Override
	public void setLastToken(IToken token)
	{
		super.setLastToken(token);
		if (token == null)
		{
			return;
		}

		Object data = token.getData();
		if (!(data instanceof String))
		{
			current = TYPE_DEFAULT;
			return;
		}

		String contentType = (String) data;

		if (HTMLSourceConfiguration.HTML_SCRIPT.equals(contentType)
				|| SVGSourceConfiguration.SCRIPT.equals(contentType))
		{
			if (!(token instanceof ExtendedToken && ((HTMLUtils.isTagSelfClosing(((ExtendedToken) token).getContents())) || !HTMLUtils
					.isJavaScriptTag(((ExtendedToken) token).getContents()))))
			{
				current = TYPE_JS;
				super.setLastToken(null);
			}
		}
		else if (HTMLSourceConfiguration.HTML_STYLE.equals(contentType)
				|| SVGSourceConfiguration.STYLE.equals(contentType))
		{
			if (!(token instanceof ExtendedToken && (HTMLUtils.isTagSelfClosing(((ExtendedToken) token).getContents()) || !HTMLUtils
					.isTagComplete(((ExtendedToken) token).getContents()))))
			{
				current = TYPE_CSS;
				super.setLastToken(null);
			}
		}
		else if (HTMLSourceConfiguration.HTML_SVG.equals(contentType))
		{
			if (!(token instanceof ExtendedToken && HTMLUtils.isTagSelfClosing(((ExtendedToken) token).getContents()) || !HTMLUtils
					.isTagComplete(((ExtendedToken) token).getContents())))
			{
				current = TYPE_SVG;
				super.setLastToken(null);
			}
		}
		else if (HTMLSourceConfiguration.DEFAULT.equals(contentType)
				|| IDocument.DEFAULT_CONTENT_TYPE.equals(contentType))
		{
			current = TYPE_DEFAULT;
		}
		else
		{
			for (int i = 0; i < subPartitionScanners.length; ++i)
			{
				if (subPartitionScanners[i].hasContentType(contentType))
				{
					current = i;
					break;
				}
			}
		}
	}
}
