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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.internal.CSSAutoIndentStrategy;
import com.aptana.editor.css.internal.CSSCommentIndentStrategy;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.internal.JSAutoIndentStrategy;
import com.aptana.editor.js.internal.JSCommentIndentStrategy;
import com.aptana.editor.js.internal.JSDocIndentStrategy;

public class HTMLSourceViewerConfiguration extends CommonSourceViewerConfiguration
{

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
		return TextUtils.combine(new String[][] { { IDocument.DEFAULT_CONTENT_TYPE },
				HTMLSourceConfiguration.CONTENT_TYPES, JSSourceConfiguration.CONTENT_TYPES,
				CSSSourceConfiguration.CONTENT_TYPES });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes()
	{
		return HTMLSourceConfiguration.getDefault().getTopContentTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = (PresentationReconciler) super.getPresentationReconciler(sourceViewer);
		HTMLSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);
		return reconciler;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.startsWith(JSSourceConfiguration.PREFIX))
		{

			if (contentType.equals(JSSourceConfiguration.JS_SINGLELINE_COMMENT)
					|| contentType.equals(JSSourceConfiguration.JS_MULTILINE_COMMENT))
			{
				String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
				return new IAutoEditStrategy[] { new JSCommentIndentStrategy(partitioning, contentType, this,
						sourceViewer) };
			}
			if (contentType.equals(JSSourceConfiguration.JS_DOC))
			{
				String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
				return new IAutoEditStrategy[] { new JSDocIndentStrategy(partitioning, contentType, this, sourceViewer) };
			}
			return new IAutoEditStrategy[] { new JSAutoIndentStrategy(contentType, this, sourceViewer) };
		}
		if (contentType.startsWith(CSSSourceConfiguration.PREFIX))
		{
			if (contentType.equals(CSSSourceConfiguration.MULTILINE_COMMENT))
			{
				String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
				return new IAutoEditStrategy[] { new CSSCommentIndentStrategy(partitioning, contentType, this,
						sourceViewer) };
			}
			return new IAutoEditStrategy[] { new CSSAutoIndentStrategy(contentType, this, sourceViewer) };
		}
		return new IAutoEditStrategy[] { new HTMLAutoIndentStrategy(contentType, this, sourceViewer) };
	}
}
