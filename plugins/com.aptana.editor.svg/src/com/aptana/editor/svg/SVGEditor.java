/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.svg.outline.SVGOutlineContentProvider;
import com.aptana.editor.svg.outline.SVGOutlineLabelProvider;
import com.aptana.editor.svg.parsing.SVGParserConstants;
import com.aptana.editor.xml.XMLEditor;

public class SVGEditor extends XMLEditor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.XMLEditor#createFileService()
	 */
	@Override
	protected FileService createFileService()
	{
		return new FileService(SVGParserConstants.LANGUAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.XMLEditor#createOutlinePage()
	 */
	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outline = super.createOutlinePage();

		outline.setContentProvider(new SVGOutlineContentProvider());
		outline.setLabelProvider(new SVGOutlineLabelProvider());

		return outline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.XMLEditor#getOutlinePreferenceStore()
	 */
	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return SVGPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.xml.XMLEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setSourceViewerConfiguration(new SVGSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new SVGDocumentProvider());
	}
}
