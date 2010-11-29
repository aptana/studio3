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

package com.aptana.editor.haml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.haml.internal.HAMLElementScanner;
import com.aptana.editor.haml.internal.HAMLSubPartitionScanner;
import com.aptana.editor.haml.internal.RubyAttributesSourceConfiguration;
import com.aptana.editor.haml.internal.text.rules.HAMLElementRule;
import com.aptana.editor.haml.internal.text.rules.HAMLEscapeRule;
import com.aptana.editor.haml.internal.text.rules.HAMLSingleLineRule;
import com.aptana.editor.haml.internal.text.rules.RubyEvaluationElementRule;
import com.aptana.editor.ruby.IRubyConstants;
import com.aptana.editor.ruby.RubySourceConfiguration;

/**
 * @author Max Stepanov
 * @author Chris Williams
 */
public class HAMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {

	private final static String PREFIX = "__haml_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String DOCTYPE = PREFIX + "doctype"; //$NON-NLS-1$
	public final static String ELEMENT = PREFIX + "element"; //$NON-NLS-1$
	public final static String RUBY_EVALUATION = PREFIX + "ruby_evaluation"; //$NON-NLS-1$
	public final static String RUBY_ATTRIBUTES = PREFIX + "ruby_attributes"; //$NON-NLS-1$
	public final static String RUBY_ATTRIBUTES_CLOSE = PREFIX + "ruby_attributes_close"; //$NON-NLS-1$
	public final static String HTML_ATTRIBUTES = PREFIX + "html_attributes"; //$NON-NLS-1$
	public final static String OBJECT = PREFIX + "object"; //$NON-NLS-1$
	public final static String INTERPOLATION = PREFIX + "interpolation"; //$NON-NLS-1$
	public final static String HTML_COMMENT = PREFIX + "html_comment"; //$NON-NLS-1$
	public final static String HAML_COMMENT = PREFIX + "haml_comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] {
		DEFAULT,
		HTML_COMMENT,
		HAML_COMMENT,
		DOCTYPE,
		ELEMENT,
		INTERPOLATION,
		RUBY_EVALUATION,
		HTML_ATTRIBUTES,
		RUBY_ATTRIBUTES,
		RUBY_ATTRIBUTES_CLOSE,
		OBJECT
	};

	private static final String[][] TOP_CONTENT_TYPES = new String[][] {
		{ IHAMLConstants.CONTENT_TYPE_HAML },
		{ IHAMLConstants.CONTENT_TYPE_HAML, IRubyConstants.CONTENT_TYPE_RUBY }
	};

	private final IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new HAMLSingleLineRule("/", new Token(HTML_COMMENT)), //$NON-NLS-1$
			new HAMLSingleLineRule("-#", new Token(HAML_COMMENT)), //$NON-NLS-1$
			new HAMLSingleLineRule("!!!", new Token(DOCTYPE)), //$NON-NLS-1$
			new HAMLEscapeRule(new Token("")),
			new SingleLineRule("#{", "}", new Token(INTERPOLATION)), //$NON-NLS-1$ //$NON-NLS-2$
			new HAMLElementRule(new Token(ELEMENT)),
			new RubyEvaluationElementRule(new Token(RUBY_EVALUATION)),
			new SingleCharacterRule('{', new Token(RUBY_ATTRIBUTES)),
			new SingleCharacterRule('}', new Token(RUBY_ATTRIBUTES_CLOSE)),
			new SingleLineRule("[", "]", new Token(OBJECT)), //$NON-NLS-1$ //$NON-NLS-2$
			new MultiLineRule("(", ")", new Token(HTML_ATTRIBUTES)), //$NON-NLS-1$ //$NON-NLS-2$
		};

	private RuleBasedScanner fCommentScanner;
	private RuleBasedScanner fTextScanner;
	private RuleBasedScanner fElementScanner;
	private RuleBasedScanner fInterpolationScanner;
	private RuleBasedScanner fObjectScanner;
	private RuleBasedScanner fHTMLAttributesScanner;
	private RuleBasedScanner fHAMLCommentScanner;
	private RuleBasedScanner fDocTypeScanner;

	private static HAMLSourceConfiguration instance;

	static {
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(IHAMLConstants.CONTENT_TYPE_HAML), new QualifiedContentType(IHAMLConstants.TEXT_SCOPE));
		c.addTranslation(new QualifiedContentType(HAML_COMMENT), new QualifiedContentType(IHAMLConstants.HAML_COMMENT_SCOPE));
		c.addTranslation(new QualifiedContentType(HTML_COMMENT), new QualifiedContentType(IHAMLConstants.HTML_COMMENT_SCOPE));
		c.addTranslation(new QualifiedContentType(DOCTYPE), new QualifiedContentType(IHAMLConstants.DOCTYPE_SCOPE));
		c.addTranslation(new QualifiedContentType(ELEMENT), new QualifiedContentType(IHAMLConstants.TAG_SCOPE));
		c.addTranslation(new QualifiedContentType(HTML_ATTRIBUTES), new QualifiedContentType(IHAMLConstants.RUBY_ATTRIBUTES_SCOPE));
		c.addTranslation(new QualifiedContentType(RUBY_ATTRIBUTES), new QualifiedContentType(IHAMLConstants.RUBY_ATTRIBUTES_SCOPE)); 
		c.addTranslation(new QualifiedContentType(RUBY_EVALUATION), new QualifiedContentType(IHAMLConstants.RUBY_EVAL_SCOPE)); 
		c.addTranslation(new QualifiedContentType(OBJECT), new QualifiedContentType(IHAMLConstants.OBJECT_SCOPE)); 
		c.addTranslation(new QualifiedContentType(INTERPOLATION), new QualifiedContentType(IHAMLConstants.INTERPOLATION_SCOPE)); 
		c.addTranslation(new QualifiedContentType(IHAMLConstants.CONTENT_TYPE_HAML, IRubyConstants.CONTENT_TYPE_RUBY), new QualifiedContentType(IHAMLConstants.TEXT_SCOPE, IHAMLConstants.RUBY_EVAL_SCOPE, IHAMLConstants.EMBEDDED_RUBY_SCOPE)); 
	}

	public static HAMLSourceConfiguration getDefault() {
		if (instance == null) {
			instance = new HAMLSourceConfiguration();
		}
		return instance;
	}

	private HAMLSourceConfiguration() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes() {
		return TextUtils.combine(new String[][] {
				CONTENT_TYPES,
				RubySourceConfiguration.CONTENT_TYPES,
				RubyAttributesSourceConfiguration.CONTENT_TYPES
			});
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes() {
		return TOP_CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules() {
		return partitioningRules;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new HAMLSubPartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType) {
		if (contentType.startsWith(PREFIX)) {
			return IHAMLConstants.CONTENT_TYPE_HAML;
		}
		String result = RubySourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null) {
			return result;
		}
		result = RubyAttributesSourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null) {
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation
	 * .PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		RubySourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		RubyAttributesSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getTextScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getHTMLCommentScanner());
		reconciler.setDamager(dr, HTML_COMMENT);
		reconciler.setRepairer(dr, HTML_COMMENT);

		dr = new ThemeingDamagerRepairer(getHAMLCommentScanner());
		reconciler.setDamager(dr, HAML_COMMENT);
		reconciler.setRepairer(dr, HAML_COMMENT);

		dr = new ThemeingDamagerRepairer(getDocTypeScanner());
		reconciler.setDamager(dr, DOCTYPE);
		reconciler.setRepairer(dr, DOCTYPE);

		dr = new ThemeingDamagerRepairer(getElementScanner());
		reconciler.setDamager(dr, ELEMENT);
		reconciler.setRepairer(dr, ELEMENT);

		dr = new ThemeingDamagerRepairer(getInterpolationScanner());
		reconciler.setDamager(dr, INTERPOLATION);
		reconciler.setRepairer(dr, INTERPOLATION);

		dr = new ThemeingDamagerRepairer(getObjectScanner());
		reconciler.setDamager(dr, OBJECT);
		reconciler.setRepairer(dr, OBJECT);

		dr = new ThemeingDamagerRepairer(getHTMLAttributesScanner());
		reconciler.setDamager(dr, HTML_ATTRIBUTES);
		reconciler.setRepairer(dr, HTML_ATTRIBUTES);

	}

	private ITokenScanner getTextScanner() {
		if (fTextScanner == null) {
			fTextScanner = new RuleBasedScanner();
			fTextScanner.setRules(new IRule[] {
					new CharacterMapRule()
					.add('/', getToken("punctuation.terminator.tag.haml")) //$NON-NLS-1$
					.add('/', getToken("punctuation.terminator.tag.haml")) //$NON-NLS-1$
					.add('>', getToken("punctuation.other.tag.haml")) //$NON-NLS-1$
					.add('<', getToken("punctuation.other.tag.haml")) //$NON-NLS-1$
					.add('&', getToken("punctuation.other.tag.haml")) //$NON-NLS-1$
					.add('!', getToken("punctuation.other.tag.haml")), //$NON-NLS-1$
					new HAMLEscapeRule(getToken("meta.escape.haml")), //$NON-NLS-1$
			});
			fTextScanner.setDefaultReturnToken(getToken(IHAMLConstants.TEXT_SCOPE)); 
		}
		return fTextScanner;
	}

	private ITokenScanner getElementScanner() {
		if (fElementScanner == null) {
			fElementScanner = new HAMLElementScanner();
		}
		return fElementScanner;
	}

	private ITokenScanner getInterpolationScanner() {
		if (fInterpolationScanner == null) {
			fInterpolationScanner = new RuleBasedScanner();
			fInterpolationScanner.setRules(new IRule[] {
					new MultiCharacterRule("#{", getToken("punctuation.section.embedded.ruby")), //$NON-NLS-1$ //$NON-NLS-2$
					new SingleCharacterRule('}', getToken("punctuation.section.embedded.ruby")) //$NON-NLS-1$
			});
			fInterpolationScanner.setDefaultReturnToken(getToken("source.ruby.embedded.source")); //$NON-NLS-1$
		}
		return fInterpolationScanner;
	}

	private ITokenScanner getObjectScanner() {
		if (fObjectScanner == null) {
			fObjectScanner = new RuleBasedScanner();
			fObjectScanner.setRules(new IRule[] {
					new CharacterMapRule()
					.add('[', getToken("punctuation.section.other.haml")) //$NON-NLS-1$
					.add(']', getToken("punctuation.section.other.haml")), //$NON-NLS-1$
					// TODO: add word rules here for:
					// - variable.other.readwrite.instance.ruby
					// - constant.other.symbol.ruby
					// - comma
			});
			fObjectScanner.setDefaultReturnToken(getToken(IHAMLConstants.OBJECT_SCOPE)); 
		}
		return fObjectScanner;
	}

	private ITokenScanner getHTMLAttributesScanner() {
		if (fHTMLAttributesScanner == null) {
			fHTMLAttributesScanner = new RuleBasedScanner();
			fHTMLAttributesScanner.setRules(new IRule[] {
					new CharacterMapRule()
					.add('(', getToken("punctuation.section.other.haml")) //$NON-NLS-1$
					.add(')', getToken("punctuation.section.other.haml")), //$NON-NLS-1$
					// TODO: add word rules here for:
					// - single quoted string
					// - double quoted string
					// - an HTML attribute name
					// - equal sign
			});
			fHTMLAttributesScanner.setDefaultReturnToken(getToken(IHAMLConstants.OBJECT_SCOPE)); 
		}
		return fHTMLAttributesScanner;
	}

	private ITokenScanner getHTMLCommentScanner() {
		if (fCommentScanner == null) {
			fCommentScanner = new RuleBasedScanner();
			fCommentScanner.setRules(new IRule[] {
					// TODO Use CommentScanner so we pick up task tags
					new SingleCharacterRule('/', getToken("punctuation.section.comment.haml")) //$NON-NLS-1$
			});
			fCommentScanner.setDefaultReturnToken(getToken(IHAMLConstants.HTML_COMMENT_SCOPE)); 
		}
		return fCommentScanner;
	}

	private ITokenScanner getHAMLCommentScanner() {
		if (fHAMLCommentScanner == null) {
			fHAMLCommentScanner = new RuleBasedScanner();
			fHAMLCommentScanner.setRules(new IRule[] {
					// TODO Use CommentScanner so we pick up task tags
					new EndOfLineRule("#", getToken(IRubyConstants.LINE_COMMENT_SCOPE)) //$NON-NLS-1$
			});
			fHAMLCommentScanner.setDefaultReturnToken(getToken(""));  //$NON-NLS-1$
		}
		return fHAMLCommentScanner;
	}

	private ITokenScanner getDocTypeScanner() {
		if (fDocTypeScanner == null) {
			fDocTypeScanner = new RuleBasedScanner();
			fDocTypeScanner.setRules(new IRule[] {
					new SingleCharacterRule('!', getToken("punctuation.definition.prolog.haml")) //$NON-NLS-1$
			});
			fDocTypeScanner.setDefaultReturnToken(getToken(IHAMLConstants.DOCTYPE_SCOPE)); 
		}
		return fDocTypeScanner;
	}

	private IToken getToken(String name) {
		return new Token(name);
	}
}
