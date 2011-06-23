/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver.outline;

import org.eclipse.jface.viewers.LabelProvider;

import beaver.spec.ast.Rule;

import com.aptana.editor.beaver.outline.BeaverOutlineContentProvider.SymbolWrapper;

/**
 * BeaverOutlineLabelProvider
 */
public class BeaverOutlineLabelProvider extends LabelProvider
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element)
	{
		String result;

		if (element instanceof SymbolWrapper)
		{
			element = ((SymbolWrapper) element).getSymbol();
		}

		if (element instanceof Rule)
		{
			Rule rule = (Rule) element;

			result = rule.getLHSSymbolName();
		}
		else
		{
			result = super.getText(element);
		}

		return result;
	}
}
