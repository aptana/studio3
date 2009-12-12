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
package com.aptana.editor.common;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.contentassist.CommonTemplateCompletionProcessor;
import com.aptana.editor.common.contentassist.CompositeContentAssistProcessor;
import com.aptana.editor.common.hover.CommonAnnotationHover;
import com.aptana.editor.common.hover.CommonTextHover;
import com.aptana.editor.common.preferences.IPreferenceConstants;

public class CommonSourceViewerConfiguration extends TextSourceViewerConfiguration {

    private ITextEditor fTextEditor;

    public CommonSourceViewerConfiguration(IPreferenceStore preferenceStore, ITextEditor editor) {
        super(preferenceStore);
        fTextEditor = editor;
    }

    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { new CommonAutoIndentStrategy(contentType, this,
                sourceViewer) };
    }

    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return ICommonConstants.DEFAULT_PARTITIONING;
    }

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();

        assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        String[] contentTypes = getConfiguredContentTypes(sourceViewer);
        IContentAssistProcessor processor;
        for (String type : contentTypes) {
            processor = getContentAssistProcessor(sourceViewer, type);
            if (processor != null) {
                assistant.setContentAssistProcessor(processor, type);
            }
        }

        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        if (fPreferenceStore != null) {
            assistant.enableAutoActivation(fPreferenceStore
                    .getBoolean(IPreferenceConstants.CONTENT_ASSIST_AUTO_ACTIVATION));
            assistant.setAutoActivationDelay(fPreferenceStore
                    .getInt(IPreferenceConstants.CONTENT_ASSIST_DELAY));
        }
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);

        return assistant;
    }

    @Override
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        MultiPassContentFormatter formatter = new MultiPassContentFormatter(
                getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
        return formatter;
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new CommonAnnotationHover(false) {

            protected boolean isIncluded(Annotation annotation) {
                return isShowInVerticalRuler(annotation);
            }
        };
    }

    @Override
    public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) {
        return new CommonAnnotationHover(true) {

            protected boolean isIncluded(Annotation annotation) {
                return isShowInOverviewRuler(annotation);
            }
        };
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new CommonTextHover();
    }

    @Override
    public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {

            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent, false);
            }
        };
    }

    @Override
    public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
        InformationPresenter presenter = new InformationPresenter(
                getInformationPresenterControlCreator(sourceViewer));

        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setSizeConstraints(60, 10, true, true);

        // registers information provider
        String[] contentTypes = getConfiguredContentTypes(sourceViewer);
        IInformationProvider provider;
        for (String type : contentTypes) {
            provider = getInformationProvider(sourceViewer, type);
            if (provider != null) {
                presenter.setInformationProvider(provider, type);
            }
        }

        return presenter;
    }

    @Override
    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        IReconcilingStrategy strategy = getReconcilingStrategy();
        if (strategy == null) {
            return null;
        }
        MonoReconciler reconciler = new MonoReconciler(strategy, false);
        reconciler.setDelay(1000);

        return reconciler;
    }

    /**
     * @return the default indentation string (either tab or spaces which
     *         represents a tab)
     */
    public String getIndent() {
        boolean useSpaces = fPreferenceStore
                .getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
        if (useSpaces) {
            int tabWidth = fPreferenceStore
                    .getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < tabWidth; ++i) {
                buf.append(" "); //$NON-NLS-1$
            }
            return buf.toString();
        }
        return "\t"; //$NON-NLS-1$
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
        Map targets = super.getHyperlinkDetectorTargets(sourceViewer);
        targets.put("com.aptana.editor.ui.hyperlinkTarget", fTextEditor); //$NON-NLS-1$
        return targets;
    }

    /**
     * Returns the content assist processor that will be used for content assist
     * in the given source viewer and for the given partition type.
     * 
     * @param sourceViewer
     *            the source viewer to be configured by this configuration
     * @param contentType
     *            the partition type for which the content assist processor is
     *            applicable
     * @return IContentAssistProcessor or null if the content type is not
     *         supported
     */
    protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer,
            String contentType) {
        return null;
    }

    /**
     * Returns the information provider that will be used for information
     * presentation in the given source viewer and for the given partition type.
     * 
     * @param sourceViewer
     *            the source viewer to be configured by this configuration
     * @param contentType
     *            the partition type for which the information provider is
     *            applicable
     * @return IInformationProvider or null if the content type is not supported
     */
    protected IInformationProvider getInformationProvider(ISourceViewer sourceViewer,
            String contentType) {
        return new CommonInformationProvider();
    }

    protected IReconcilingStrategy getReconcilingStrategy() {
        return new CommonReconcilingStrategy();
    }

    protected IContentAssistProcessor addTemplateCompleteProcessor(
            IContentAssistProcessor processor, String contentType) {
        if (processor == null) {
            return new CommonTemplateCompletionProcessor(contentType);
        }
        return new CompositeContentAssistProcessor(processor,
                new CommonTemplateCompletionProcessor(contentType));
    }

    private IInformationControlCreator getInformationPresenterControlCreator(
            ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {

            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent, true);
            }
        };
    }
}
