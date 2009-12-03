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
package com.aptana.editor.common.contentassist;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class CompositeContentAssistProcessor implements IContentAssistProcessor {

    private IContentAssistProcessor fProcessor;
    private TemplateCompletionProcessor fTemplateProcessor;

    /**
     * @param processor
     * @param templateProcessor
     */
    public CompositeContentAssistProcessor(IContentAssistProcessor processor,
            TemplateCompletionProcessor templateProcessor) {
        fProcessor = processor;
        fTemplateProcessor = templateProcessor;
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        // returns the combined proposal list of content assist and template
        // completion
        ICompletionProposal[] proposals = fProcessor.computeCompletionProposals(viewer, offset);
        ICompletionProposal[] templates = fTemplateProcessor.computeCompletionProposals(viewer,
                offset);
        if (proposals == null || proposals.length == 0) {
            return templates;
        }
        if (templates == null || templates.length == 0) {
            return proposals;
        }

        ICompletionProposal[] combined = new ICompletionProposal[proposals.length
                + templates.length];
        System.arraycopy(proposals, 0, combined, 0, proposals.length);
        System.arraycopy(templates, 0, combined, proposals.length, templates.length);

        Arrays.sort(combined, new Comparator<ICompletionProposal>() {

            public int compare(ICompletionProposal o1, ICompletionProposal o2) {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.getDisplayString().compareTo(o2.getDisplayString());
            }
        });
        return combined;
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return fProcessor.computeContextInformation(viewer, offset);
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return fProcessor.getCompletionProposalAutoActivationCharacters();
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return fProcessor.getContextInformationAutoActivationCharacters();
    }

    public IContextInformationValidator getContextInformationValidator() {
        return fProcessor.getContextInformationValidator();
    }

    public String getErrorMessage() {
        return fProcessor.getErrorMessage();
    }
}
