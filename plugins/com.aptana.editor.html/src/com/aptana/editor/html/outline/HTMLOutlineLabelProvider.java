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

	private static final Image ELEMENT_ICON = Activator.getImage("icons/element.gif"); //$NON-NLS-1$

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
