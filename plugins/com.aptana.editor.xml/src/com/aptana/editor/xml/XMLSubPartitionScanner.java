/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.xml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.PartitionScannerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositeSubPartitionScanner;
import com.aptana.editor.common.text.rules.ExtendedToken;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.dtd.DTDSourceConfiguration;

/**
 * @author Max Stepanov
 * 
 */
public class XMLSubPartitionScanner extends CompositeSubPartitionScanner {
	
	private static final int TYPE_DTD = 1;

	private static final String[] DTD_SWITCH_SEQUENCES = new String[] { "]>" }; //$NON-NLS-1$

	/**
	 * HTMLSubPartitionScanner
	 */
	public XMLSubPartitionScanner() {
		//formatter:off
		super(
				new ISubPartitionScanner[] {
				new SubPartitionScanner(
						XMLSourceConfiguration.getDefault().getPartitioningRules(),
						XMLSourceConfiguration.CONTENT_TYPES,
						new Token(XMLSourceConfiguration.DEFAULT)
						),
						DTDSourceConfiguration.getDefault().createSubPartitionScanner(),
				},
				new IPartitionScannerSwitchStrategy[] {
				new PartitionScannerSwitchStrategy(DTD_SWITCH_SEQUENCES),
				}
		);
		//@formatter:on
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

		if (XMLSourceConfiguration.DOCTYPE.equals(contentType)) {
			if (token instanceof ExtendedToken && ((ExtendedToken) token).getContents().endsWith("[")) { //$NON-NLS-1$
				current = TYPE_DTD;
				resumeToken = token;
				super.setLastToken(null);
			}
		} else if (XMLSourceConfiguration.DEFAULT.equals(contentType) || IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
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
