/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.hover;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * Base class for information hovers
 */
public abstract class AbstractCommonTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
/* , IInformationControlExtension5 */
{
	private IEditorPart fEditor;

	/**
	 * Sets an {@link IEditorPart}
	 * 
	 * @param editor
	 *            An {@link IEditorPart}
	 */
	public void setEditor(IEditorPart editor)
	{
		fEditor = editor;
	}

	/**
	 * Returns a registered {@link IEditorPart}; <code>null</code> if non is registered.
	 * 
	 * @return an {@link IEditorPart} (or <code>null</code>)
	 */
	protected IEditorPart getEditor()
	{
		return fEditor;
	}

	/**
	 * Returns the tool tip affordance string.
	 * 
	 * @return the affordance string which is empty if the preference is enabled but the key binding not active or
	 *         <code>null</code> if the preference is disabled or the binding service is unavailable
	 */
	protected String getTooltipAffordanceString()
	{
		return EditorsUI.getTooltipAffordanceString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new DefaultInformationControl(parent, AbstractCommonTextHover.this.getTooltipAffordanceString());
			}
		};
	}
}
