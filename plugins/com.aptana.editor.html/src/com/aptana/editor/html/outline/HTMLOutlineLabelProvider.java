/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineLabelProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;

public class HTMLOutlineLabelProvider extends CompositeOutlineLabelProvider
{

	private static final Image ELEMENT_ICON = Activator.getImage("icons/element.png"); //$NON-NLS-1$

	public HTMLOutlineLabelProvider()
	{
		addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineLabelProvider());
		addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineLabelProvider());
	}

	@Override
	protected Image getDefaultImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getDefaultImage(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof OutlinePlaceholderItem)
		{
			OutlinePlaceholderItem item = (OutlinePlaceholderItem) element;
			if (item.status() == IStatus.ERROR)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
			if (item.status() == IStatus.INFO)
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		}
		if (element instanceof HTMLNode)
		{
			return ELEMENT_ICON;
		}
		return super.getDefaultImage(element);
	}

	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getDefaultText(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof HTMLElementNode)
		{
			return ((HTMLElementNode) element).getText();
		}
		return super.getDefaultText(element);
	}
}
