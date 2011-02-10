/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class CSSContextInformationValidator implements IContextInformationValidator,
        IContextInformationPresenter {

    public void install(IContextInformation info, ITextViewer viewer, int offset) {
    }

    public boolean isContextInformationValid(int offset) {
        return false;
    }

    public boolean updatePresentation(int offset, TextPresentation presentation) {
        return false;
    }
}
