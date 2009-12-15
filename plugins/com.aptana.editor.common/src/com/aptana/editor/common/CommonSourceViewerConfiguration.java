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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import com.aptana.editor.common.preferences.IPreferenceConstants;

public abstract class CommonSourceViewerConfiguration extends TextSourceViewerConfiguration implements ITopContentTypesProvider {

    public CommonSourceViewerConfiguration() {
    }

    public CommonSourceViewerConfiguration(IPreferenceStore preferenceStore) {
        super(preferenceStore);
    }

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();

        assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
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

        // registers information provider
        IInformationProvider provider = new CommonInformationProvider();
        String[] contentTypes = getConfiguredContentTypes(sourceViewer);
        for (String type : contentTypes) {
            presenter.setInformationProvider(provider, type);
        }

        presenter.setSizeConstraints(60, 10, true, true);

        return presenter;
    }

    protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType) {
        return null;
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
