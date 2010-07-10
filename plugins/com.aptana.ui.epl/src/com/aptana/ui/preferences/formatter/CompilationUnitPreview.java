/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ui.epl.UIEplPlugin;


/**
 * 
 *
 */
public class CompilationUnitPreview extends Preview {

    private String fPreviewText;

    /**
     * @param workingValues
     * @param parent
     * @param language 
     * @param store 
     */
    public CompilationUnitPreview(Map workingValues, Composite parent,String language,IPreferenceStore store) {
        super(workingValues, parent,language,store);
    }

    
    protected void doFormatPreview() {
        if (fPreviewText == null) {
            fPreviewDocument.set(""); //$NON-NLS-1$
            return;
        }
        fPreviewDocument.set(formatter.format('\n' + fPreviewText, false, this.getWorkingValues(),null, null));		
				
		try {
			//TODO ADD FORMATTING
		} catch (Exception e) {
			final IStatus status= new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID, 0, 
				FormatterMessages.JavaPreview_formatter_exception, e); 
			UIEplPlugin.getDefault().getLog().log(status);
		} finally {
		   
		}
    }
    
    /**
     * @param previewText
     */
    public void setPreviewText(String previewText) {
        fPreviewText= previewText;
        update();
    }
}
