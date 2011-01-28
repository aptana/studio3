/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.idl.parsing.IDLParserConstants;

public class IDLEditor extends AbstractThemeableEditor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createFileService()
	 */
	protected FileService createFileService()
	{
		return new FileService(IDLParserConstants.LANGUAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createOutlinePage()
	 */
	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#initializeEditor()
	 */
	protected void initializeEditor()
	{
		super.initializeEditor();

		this.setSourceViewerConfiguration(new IDLSourceViewerConfiguration(this.getPreferenceStore(), this));
		this.setDocumentProvider(new IDLDocumentProvider());
	}
}
