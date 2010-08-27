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

package com.aptana.editor.erb.html;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CompositeSourceViewerConfiguration;
import com.aptana.editor.common.IPartitionerSwitchStrategy;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.erb.ERBPartitionerSwitchStrategy;
import com.aptana.editor.erb.IERBConstants;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.HTMLSourceViewerConfiguration;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.ruby.IRubyConstants;
import com.aptana.editor.ruby.RubySourceConfiguration;
import com.aptana.editor.ruby.core.RubyDoubleClickStrategy;

/**
 * @author Max Stepanov
 * @author cwilliams
 */
public class RHTMLSourceViewerConfiguration extends CompositeSourceViewerConfiguration implements IERBConstants
{

	static
	{
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB), new QualifiedContentType(
				TOPLEVEL_RHTML_SCOPE));
		c.addTranslation(new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB,
				CompositePartitionScanner.START_SWITCH_TAG), new QualifiedContentType(TOPLEVEL_RHTML_SCOPE,
				EMBEDDED_RUBY_TAG_SCOPE));
		c.addTranslation(new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB,
				CompositePartitionScanner.END_SWITCH_TAG), new QualifiedContentType(TOPLEVEL_RHTML_SCOPE,
				EMBEDDED_RUBY_TAG_SCOPE));

		c.addTranslation(
				new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB, IHTMLConstants.CONTENT_TYPE_HTML),
				new QualifiedContentType(TOPLEVEL_RHTML_SCOPE));
		c.addTranslation(new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB, ICSSConstants.CONTENT_TYPE_CSS),
				new QualifiedContentType(TOPLEVEL_RHTML_SCOPE, EMBEDDED_CSS_SCOPE));
		c.addTranslation(new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB, IJSConstants.CONTENT_TYPE_JS),
				new QualifiedContentType(TOPLEVEL_RHTML_SCOPE, EMBEDDED_JS_SCOPE));
		c.addTranslation(
				new QualifiedContentType(IERBConstants.CONTENT_TYPE_HTML_ERB, IRubyConstants.CONTENT_TYPE_RUBY),
				new QualifiedContentType(TOPLEVEL_RHTML_SCOPE, EMBEDDED_RUBY_SCOPE));
	}

	private RubyDoubleClickStrategy fDoubleClickStrategy;

	protected RHTMLSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor)
	{
		super(HTMLSourceConfiguration.getDefault(), RubySourceConfiguration.getDefault(), preferences, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CompositeSourceViewerConfiguration#getTopContentType()
	 */
	@Override
	protected String getTopContentType()
	{
		return IERBConstants.CONTENT_TYPE_HTML_ERB;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CompositeSourceViewerConfiguration#getLanguageSpecification()
	 */
	@Override
	protected IPartitionerSwitchStrategy getPartitionerSwitchStrategy()
	{
		return ERBPartitionerSwitchStrategy.getDefault();
	}

	protected String getStartEndTokenType()
	{
		return "punctuation.section.embedded.ruby"; //$NON-NLS-1$
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		if (fDoubleClickStrategy == null)
		{
			fDoubleClickStrategy = new RubyDoubleClickStrategy();
		}
		return fDoubleClickStrategy;
	}

	@Override
	protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		// Just uses the HTML content assist processor for now
		// TODO: needs to check for ruby content type when the content assist is available there
		AbstractThemeableEditor editor = getAbstractThemeableEditor();
		return HTMLSourceViewerConfiguration.getContentAssistProcessor(contentType, editor);
	}
}
