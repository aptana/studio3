/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * @author Max Stepanov
 *
 */
public interface ISourceViewerConfiguration extends ITopContentTypesProvider {

	public String[] getContentTypes();
	
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer);
	
	public IContentAssistProcessor getContentAssistProcessor(AbstractThemeableEditor editor, String contentType);

}
