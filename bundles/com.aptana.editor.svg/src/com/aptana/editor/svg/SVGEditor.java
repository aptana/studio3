/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.aptana.editor.svg.outline.SVGOutlineContentProvider;
import com.aptana.editor.svg.outline.SVGOutlineLabelProvider;
import com.aptana.editor.xml.XMLEditor;

public class SVGEditor extends XMLEditor
{

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new SVGOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new SVGOutlineLabelProvider();
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return SVGPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(new SVGSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(SVGPlugin.getDefault().getSVGDocumentProvider());
	}

	protected String getFileServiceContentTypeId()
	{
		return ISVGConstants.CONTENT_TYPE_SVG;
	}
}
