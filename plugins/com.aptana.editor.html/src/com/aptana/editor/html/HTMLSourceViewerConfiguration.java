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
package com.aptana.editor.html;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.SimpleSourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.editor.css.text.CSSTextHover;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.editor.svg.SVGSourceConfiguration;
import com.aptana.editor.svg.contentassist.SVGContentAssistProcessor;

public class HTMLSourceViewerConfiguration extends SimpleSourceViewerConfiguration
{
	/**
	 * getContentAssistProcessor
	 * 
	 * @param contentType
	 * @param editor
	 * @return
	 */
	public static IContentAssistProcessor getContentAssistProcessor(String contentType, AbstractThemeableEditor editor)
	{
		if (contentType.startsWith(JSSourceConfiguration.PREFIX))
		{
			return new JSContentAssistProcessor(editor);
		}
		if (contentType.startsWith(CSSSourceConfiguration.PREFIX))
		{
			return new CSSContentAssistProcessor(editor);
		}
		if (contentType.startsWith(SVGSourceConfiguration.PREFIX))
		{
			return new SVGContentAssistProcessor(editor);
		}

		return new HTMLContentAssistProcessor(editor);
	}

	/**
	 * HTMLSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public HTMLSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor)
	{
		super(preferences, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
		return TextUtils.combine(new String[][] { { IDocument.DEFAULT_CONTENT_TYPE }, HTMLSourceConfiguration.CONTENT_TYPES,
			JSSourceConfiguration.CONTENT_TYPES, CSSSourceConfiguration.CONTENT_TYPES, SVGSourceConfiguration.CONTENT_TYPES });
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonSourceViewerConfiguration#getContentAssistProcessor(org.eclipse.jface.text.source
	 * .ISourceViewer, java.lang.String)
	 */
	@Override
	protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		AbstractThemeableEditor editor = this.getAbstractThemeableEditor();

		return getContentAssistProcessor(contentType, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonSourceViewerConfiguration#getHyperlinkDetectorTargets(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	@SuppressWarnings( { "unchecked", "rawtypes" })
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer)
	{
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);

		targets.put("com.aptana.editor.html", getEditor()); //$NON-NLS-1$

		return targets;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleSourceViewerConfiguration#getSourceViewerConfiguration()
	 */
	@Override
	public ISourceViewerConfiguration getSourceViewerConfiguration()
	{
		return HTMLSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer
	 * , java.lang.String)
	 */
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		// When in CSS, use CSSTextHover!
		if (contentType.startsWith(CSSSourceConfiguration.PREFIX))
		{
			return new CSSTextHover();
		}

		return super.getTextHover(sourceViewer, contentType);
	}
}
