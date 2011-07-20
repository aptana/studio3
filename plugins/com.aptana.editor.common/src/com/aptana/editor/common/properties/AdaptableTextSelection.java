/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Max Stepanov
 */
public class AdaptableTextSelection extends TextSelection implements IAdaptable
{

	private ISourceViewer viewer;
	/**
	 * @param element
	 */
	public AdaptableTextSelection(ISourceViewer viewer, ITextSelection textSelection)
	{
		super(textSelection.getOffset(), textSelection.getLength());
		this.viewer = viewer;
	}

	public ISourceViewer getViewer()
	{
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (IPropertySource.class == adapter)
		{
			return new EditorPropertySource(this);
		}
		return null;
	}

}
