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
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.radrails.editor.common.CommonEditorPlugin;
import com.aptana.radrails.editor.common.IPartitioningConfiguration;
import com.aptana.radrails.editor.common.ISourceViewerConfiguration;
import com.aptana.radrails.editor.common.ISubPartitionScanner;
import com.aptana.radrails.editor.common.NonRuleBasedDamagerRepairer;
import com.aptana.radrails.editor.common.theme.ThemeUtil;
import com.aptana.radrails.editor.css.CSSSourceConfiguration;
import com.aptana.radrails.editor.js.JSSourceConfiguration;

/**
 * @author Max Stepanov
 *
 */
public class HTMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {
	
	private static class DocTypeRule extends MultiLineRule {
	    private int fEmbeddedStart= 0;

		public DocTypeRule(IToken token) {
	        super("<!DOCTYPE", ">", token); //$NON-NLS-1$ //$NON-NLS-2$
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	     */
	    protected boolean endSequenceDetected(ICharacterScanner scanner) {
	        int c;
	        while ((c = scanner.read()) != ICharacterScanner.EOF) {
	            if (c == fEscapeCharacter) {
	                // Skip the escaped character.
	                scanner.read();
	            } else if (c == '<') {
	            	fEmbeddedStart++;
	            } else if (c == '>') {
	            	if (fEmbeddedStart == 0) {
	            		return true;
	            	}
	            	fEmbeddedStart--;
	            }
	        }
	        
	        scanner.unread();
	        return false;
	    }
	}
	
	private static class TagRule extends MultiLineRule {

		public TagRule(IToken token) {
			this("", token);
		}

		public TagRule(String tag, IToken token) {
	        super("<"+tag, ">", token); //$NON-NLS-1$ //$NON-NLS-2$
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	     */
	    protected boolean endSequenceDetected(ICharacterScanner scanner) {
		    boolean inSingleQuotedString = false;
		    boolean inDoubleQuotedString = false;
		    
		    int c;
	        while ((c = scanner.read()) != ICharacterScanner.EOF) {
	        	if (c == '>') {
	            	if (!(inSingleQuotedString) && !(inDoubleQuotedString)) {
	            		return true;
	            	}
	            } else if (c == '\\') {
	            	if (inSingleQuotedString || inDoubleQuotedString) {
	            		// Skip the escaped character.
	            		scanner.read();
	            	}
	            } else if (c == '"') {
	            	if (inDoubleQuotedString) {
	            		inDoubleQuotedString = false;
	            	} else if (inSingleQuotedString) {
	            		// continue
	            	} else {
	            		inDoubleQuotedString = true;
	            	}
	            } else if (c == '\'') {
	            	if (inDoubleQuotedString) {
	            		// continue
	            	} else if (inSingleQuotedString) {
	            		inSingleQuotedString = false;
	            	} else {
	            		inSingleQuotedString = true;
	            	}
	            }
	        }
	        
	        scanner.unread();
	        return false;
	    }
	}

	public final static String DEFAULT = "__html" + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String HTML_COMMENT = "__html_comment";
	public final static String CDATA = "__xml_cdata";
	public final static String HTML_DOCTYPE = "__html_doctype";
	public final static String HTML_SCRIPT = "__html_script";
	public final static String HTML_STYLE = "__html_style";
	public final static String HTML_TAG = "__html_tag";

	public static final String[] CONTENT_TYPES = new String[] {
		DEFAULT,
		HTML_COMMENT,
		CDATA,
		HTML_DOCTYPE,
		HTML_SCRIPT,
		HTML_STYLE,
		HTML_TAG
	};

	private IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new MultiLineRule("<!DOCTYPE ", ">", new Token(HTML_DOCTYPE)),
			new DocTypeRule(new Token(CDATA)),
			new MultiLineRule("<!--", "-->", new Token(HTML_COMMENT)),
			new TagRule("script", new Token(HTML_SCRIPT)),
			new TagRule("style", new Token(HTML_STYLE)),
			new TagRule(new Token(HTML_TAG)),
	};

	private HTMLScanner htmlScanner;
	private HTMLTagScanner tagScanner;
	private RuleBasedScanner doubleQuotedStringScanner;
	private RuleBasedScanner singleQuotedStringScanner;
	private RuleBasedScanner cdataScanner;

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
	 * @see com.aptana.radrails.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new HTMLSubPartitionScanner();
	}

	/* (non-Javadoc)
	 * @see com.aptana.radrails.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		JSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		CSSSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getHTMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new DefaultDamagerRepairer(getHTMLTagScanner());		
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_SCRIPT);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_SCRIPT);
		
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_STYLE);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_STYLE);
		
		reconciler.setDamager(dr, HTMLSourceConfiguration.HTML_TAG);
		reconciler.setRepairer(dr, HTMLSourceConfiguration.HTML_TAG);
		
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(ThemeUtil.getToken("comment.block.html"));
		reconciler.setDamager(ndr, HTMLSourceConfiguration.HTML_COMMENT);
		reconciler.setRepairer(ndr, HTMLSourceConfiguration.HTML_COMMENT);
		
		dr = new DefaultDamagerRepairer(getCDATAScanner());
		reconciler.setDamager(dr, CDATA);
		reconciler.setRepairer(dr, CDATA);
		
	}

	protected ITokenScanner getHTMLScanner() {
		if (htmlScanner == null) {
			htmlScanner = new HTMLScanner();
			htmlScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.DEFAULT))));
		}
		return htmlScanner;
	}
	
	private ITokenScanner getCDATAScanner()
	{
		if (cdataScanner == null)
		{
			cdataScanner = new RuleBasedScanner();
			cdataScanner.setDefaultReturnToken(ThemeUtil.getToken("string.unquoted.cdata.xml"));
		}
		return cdataScanner;
	}
	
	protected ITokenScanner getHTMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new HTMLTagScanner();
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					CommonEditorPlugin.getDefault().getColorManager().getColor(IHTMLColorConstants.TAG))));
		}
		return tagScanner;
	}

}
