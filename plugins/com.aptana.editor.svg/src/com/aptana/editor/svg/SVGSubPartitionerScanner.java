/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.PartitionScannerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositeSubPartitionScanner;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.js.JSSourceConfiguration;

/**
 * SVGSubPartitionerScanner
 */
public class SVGSubPartitionerScanner extends CompositeSubPartitionScanner {
	
	private static final int TYPE_JS = 1;
	private static final int TYPE_CSS = 2;

	private static final String[] JS_SWITCH_SEQUENCES = new String[] { "</script>" }; //$NON-NLS-1$
	private static final String[] CSS_SWITCH_SEQUENCES = new String[] { "</style>" }; //$NON-NLS-1$

	/**
	 * SVGSubPartitionerScanner
	 */
	public SVGSubPartitionerScanner() {
		super( //
				new ISubPartitionScanner[] { //
						new SubPartitionScanner(SVGSourceConfiguration.getDefault().getPartitioningRules(), SVGSourceConfiguration.CONTENT_TYPES, new Token(
								SVGSourceConfiguration.DEFAULT)), //
						JSSourceConfiguration.getDefault().createSubPartitionScanner(), //
						CSSSourceConfiguration.getDefault().createSubPartitionScanner() //
				}, //
				new IPartitionScannerSwitchStrategy[] { //
				new PartitionScannerSwitchStrategy(JS_SWITCH_SEQUENCES), //
						new PartitionScannerSwitchStrategy(CSS_SWITCH_SEQUENCES) //
				}
		);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.CompositeSubPartitionScanner#setLastToken(org.eclipse.jface.text.rules.IToken)
	 */
	@Override
	public void setLastToken(IToken token) {
		super.setLastToken(token);
		if (token == null) {
			return;
		}
		if (!(token.getData() instanceof String)) {
			current = TYPE_DEFAULT;
			return;
		}

		String contentType = (String) token.getData();

		if (SVGSourceConfiguration.SCRIPT.equals(contentType)) {
			current = TYPE_JS;
			super.setLastToken(null);
		} else if (SVGSourceConfiguration.STYLE.equals(contentType)) {
			current = TYPE_CSS;
			super.setLastToken(null);
		} else if (SVGSourceConfiguration.DEFAULT.equals(contentType) || IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
			current = TYPE_DEFAULT;
		} else {
			for (int i = 0; i < subPartitionScanners.length; ++i) {
				if (subPartitionScanners[i].hasContentType(contentType)) {
					current = i;
					break;
				}
			}
		}
	}
}
