/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineLabelProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLTextNode;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.js.core.IJSConstants;

public class HTMLOutlineLabelProvider extends CompositeOutlineLabelProvider
{

	private static final Image ELEMENT_ICON = HTMLPlugin.getImage("icons/element.png"); //$NON-NLS-1$

	public HTMLOutlineLabelProvider()
	{
		addSubLanguage(ICSSConstants.CONTENT_TYPE_CSS, new CSSOutlineLabelProvider());
		addSubLanguage(IJSConstants.CONTENT_TYPE_JS, new JSOutlineLabelProvider());
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
		if (element instanceof HTMLTextNode)
		{
			String text = ((HTMLTextNode) element).getText().trim();
			// limits to show the first 20 characters
			return StringUtil.truncate(text, 20);
		}
		return super.getDefaultText(element);
	}
}
