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

package com.aptana.editor.haml.internal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.text.rules.PartitionerSwitchingIgnoreRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.SubPartitionScanner;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairer;
import com.aptana.editor.haml.IHAMLConstants;
import com.aptana.editor.ruby.IRubyConstants;
import com.aptana.editor.ruby.RubyCodeScanner;

/**
 * @author Max Stepanov
 *
 */
public class RubyAttributesSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration {

	public final static String PREFIX = "__hamlrubyattr_"; //$NON-NLS-1$
	public final static String DEFAULT = "__hamlrubyattr" + IDocument.DEFAULT_CONTENT_TYPE; //$NON-NLS-1$
	public final static String STRING_DOUBLE = PREFIX + "string_double"; //$NON-NLS-1$
	public final static String STRING_SINGLE = PREFIX + "string_single"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, STRING_SINGLE, STRING_DOUBLE };

	private static RubyAttributesSourceConfiguration instance;

	private final IPredicateRule[] partitioningRules = new IPredicateRule[] {
			new PartitionerSwitchingIgnoreRule(new SingleLineRule("\"", "\"", new Token(STRING_DOUBLE), '\\')), //$NON-NLS-1$ //$NON-NLS-2$
			new PartitionerSwitchingIgnoreRule(new SingleLineRule("\'", "\'", new Token(STRING_SINGLE), '\\')), //$NON-NLS-1$ //$NON-NLS-2$
			new SingleCharacterRule('}', new Token(null))
	};
	
	static {
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(DEFAULT), new QualifiedContentType(IHAMLConstants.RUBY_ATTRIBUTES_SCOPE)); //$NON-NLS-1$
		c.addTranslation(new QualifiedContentType(STRING_SINGLE), new QualifiedContentType(IHAMLConstants.RUBY_ATTRIBUTES_SCOPE, IRubyConstants.SINGLE_QUOTED_STRING_SCOPE)); //$NON-NLS-1$ //$NON-NLS-2$
		c.addTranslation(new QualifiedContentType(STRING_DOUBLE), new QualifiedContentType(IHAMLConstants.RUBY_ATTRIBUTES_SCOPE, IRubyConstants.DOUBLE_QUOTED_STRING_SCOPE)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private RubyCodeScanner codeScanner;
	private RuleBasedScanner singleQuotedStringScanner;
	private RuleBasedScanner doubleQuotedStringScanner;

	public static RubyAttributesSourceConfiguration getDefault() {
		if (instance == null) {
			instance = new RubyAttributesSourceConfiguration();
		}
		return instance;
	}
	
	/**
	 * 
	 */
	private RubyAttributesSourceConfiguration() {
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes() {
		return CONTENT_TYPES;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules() {
		return partitioningRules;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner() {
		return new SubPartitionScanner(partitioningRules, CONTENT_TYPES, new Token(DEFAULT));
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentContentType(java.lang.String)
	 */
	public String getDocumentContentType(String contentType) {
		if (contentType.startsWith(PREFIX)) {
			return IHAMLConstants.CONTENT_TYPE_HAML;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes() {
		throw new IllegalStateException("Should never been called"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation.PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer) {
		DefaultDamagerRepairer dr = new ThemeingDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new ThemeingDamagerRepairer(getSingleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_SINGLE);
		reconciler.setRepairer(dr, STRING_SINGLE);

		dr = new ThemeingDamagerRepairer(getDoubleQuotedStringScanner());
		reconciler.setDamager(dr, STRING_DOUBLE);
		reconciler.setRepairer(dr, STRING_DOUBLE);
	}

	private ITokenScanner getCodeScanner() {
		if (codeScanner == null) {
			codeScanner = new RubyCodeScanner();
		}
		return codeScanner;
	}

	private ITokenScanner getSingleQuotedStringScanner() {
		if (singleQuotedStringScanner == null) {
			singleQuotedStringScanner = new RuleBasedScanner();
			singleQuotedStringScanner.setDefaultReturnToken(getToken(IRubyConstants.SINGLE_QUOTED_STRING_SCOPE)); //$NON-NLS-1$
		}
		return singleQuotedStringScanner;
	}

	private ITokenScanner getDoubleQuotedStringScanner() {
		if (doubleQuotedStringScanner == null) {
			doubleQuotedStringScanner = new RuleBasedScanner();
			doubleQuotedStringScanner.setDefaultReturnToken(getToken(IRubyConstants.DOUBLE_QUOTED_STRING_SCOPE)); //$NON-NLS-1$
		}
		return doubleQuotedStringScanner;
	}

	private IToken getToken(String tokenName) {
		return new Token(tokenName);
	}

}
