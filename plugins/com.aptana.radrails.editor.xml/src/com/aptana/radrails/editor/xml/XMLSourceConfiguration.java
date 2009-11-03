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

package com.aptana.radrails.editor.xml;

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
public class XMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {

	public final static String XML_COMMENT = "__xml_comment";
	public final static String XML_TAG = "__xml_tag";

	public static final String[] CONTENT_TYPES = new String[] {
		XML_COMMENT,
		XML_TAG
	};

	private IToken xmlCommentToken = new Token(XML_COMMENT);
	private IToken tagToken = new Token(XML_TAG);
	
	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new MultiLineRule("<!--", "-->", xmlCommentToken),
			new TagRule(tagToken)
	};

	private XMLTagScanner tagScanner;
	private XMLScanner xmlScanner;

	private static XMLSourceConfiguration instance;
	
	public static XMLSourceConfiguration getDefault() {
		if (instance == null) {
			instance = new XMLSourceConfiguration();
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
		reconciler.setDamager(dr, XMLSourceConfiguration.XML_TAG);
		reconciler.setRepairer(dr, XMLSourceConfiguration.XML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(CommonEditorPlugin.getDefault().getColorManager().getColor(IXMLColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, XMLSourceConfiguration.XML_COMMENT);
		reconciler.setRepairer(ndr, XMLSourceConfiguration.XML_COMMENT);
	}

	protected ITokenScanner getXMLScanner() {
		if (xmlScanner == null) {
			xmlScanner = new XMLScanner();
			xmlScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IXMLColorConstants.DEFAULT))));
		}
		return xmlScanner;
	}
	
	protected ITokenScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new XMLTagScanner();
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IXMLColorConstants.TAG))));
		}
		return tagScanner;
	}

}
