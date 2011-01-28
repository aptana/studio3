/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.hover;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.text.source.DefaultAnnotationHover;

@SuppressWarnings("restriction")
public class CommonAnnotationHover extends DefaultAnnotationHover {

    public CommonAnnotationHover(boolean showLineNumber) {
        super(showLineNumber);
    }

    /*
     * Formats a message as HTML text.
     * 
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.DefaultAnnotationHover#formatSingleMessage(java.lang.String)
     */
    protected String formatSingleMessage(String message) {
        StringBuffer buffer = new StringBuffer();

        HTMLPrinter.addPageProlog(buffer);
        HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
        HTMLPrinter.addPageEpilog(buffer);

        return buffer.toString();
    }

    /*
     * Formats several messages as HTML text.
     * 
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.DefaultAnnotationHover#formatMultipleMessages(java.util.List)
     */
	@SuppressWarnings("rawtypes")
	protected String formatMultipleMessages(List messages) {
        StringBuffer buffer = new StringBuffer();

        HTMLPrinter.addPageProlog(buffer);
        HTMLPrinter.addParagraph(buffer, HTMLPrinter
                .convertToHTMLContent(Messages.CommonAnnotationHover_MultipleMarkers));
        HTMLPrinter.startBulletList(buffer);
        Iterator e = messages.iterator();
        while (e.hasNext()) {
            HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
        }
        HTMLPrinter.endBulletList(buffer);
        HTMLPrinter.addPageEpilog(buffer);
        
        return buffer.toString();
    }
}
