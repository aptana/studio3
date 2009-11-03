/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.radrails.editor.html;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.radrails.editor.common.CommonEditorPlugin;
import com.aptana.radrails.editor.common.IPartitioningConfiguration;
import com.aptana.radrails.editor.common.ISourceViewerConfiguration;

/**
 * @author Max Stepanov
 *
 */
public class HTMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {

	public final static String HTML_COMMENT = "__html_comment";
	public final static String HTML_TAG = "__html_tag";

	public static final String[] CONTENT_TYPES = new String[] {
		HTML_COMMENT,
		HTML_TAG
	};

	private IToken htmlCommentToken = new Token(HTML_COMMENT);
	private IToken tagToken = new Token(HTML_TAG);
	
	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new MultiLineRule("<!--", "-->", htmlCommentToken),
			new TagRule(tagToken)
	};

	private HTMLTagScanner tagScanner;
	private HTMLScanner htmlScanner;

	private static HTMLSourceConfiguration instance;
	
	public static HTMLSourceConfiguration getDefault() {
		if (instance == null) {
			instance = new HTMLSourceConfiguration();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes() {
		return CONTENT_TYPES;
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules() {
		return partitioningRules;
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_TAG);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, HTMLSourceConfiguration.HTML_COMMENT);
		reconciler.setRepairer(ndr, HTMLSourceConfiguration.HTML_COMMENT);
	}

	protected ITokenScanner getXMLScanner() {
		if (htmlScanner == null) {
			htmlScanner = new HTMLScanner();
			htmlScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.DEFAULT))));
		}
		return htmlScanner;
	}
	
	protected ITokenScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new HTMLTagScanner();
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.TAG))));
		}
		return tagScanner;
	}

}
