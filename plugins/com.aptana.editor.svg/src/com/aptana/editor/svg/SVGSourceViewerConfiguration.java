package com.aptana.editor.svg;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.editor.svg.contentassist.SVGContentAssistProcessor;

public class SVGSourceViewerConfiguration extends CommonSourceViewerConfiguration
{
	/**
	 * SVGSourceViewerConfiguration
	 * 
	 * @param preferences
	 * @param editor
	 */
	public SVGSourceViewerConfiguration(IPreferenceStore preferences, AbstractThemeableEditor editor)
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
		return TextUtils.combine( //
			new String[][] { //
			{ IDocument.DEFAULT_CONTENT_TYPE }, //
				SVGSourceConfiguration.CONTENT_TYPES, //
				JSSourceConfiguration.CONTENT_TYPES, //
				CSSSourceConfiguration.CONTENT_TYPES //
			} //
		);
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

		if (contentType.startsWith(JSSourceConfiguration.PREFIX))
		{
			return new JSContentAssistProcessor(editor);
		}
		else if (contentType.startsWith(CSSSourceConfiguration.PREFIX))
		{
			return new CSSContentAssistProcessor(editor);
		}
		else
		{
			return null;
			// return new SVGContentAssistProcessor(editor);
		}
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

		SVGSourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		return reconciler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes()
	{
		return SVGSourceConfiguration.getDefault().getTopContentTypes();
	}
}
