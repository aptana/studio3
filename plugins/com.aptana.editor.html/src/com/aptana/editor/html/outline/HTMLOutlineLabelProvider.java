package com.aptana.editor.html.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CompositeOutlineLabelProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.editor.css.parsing.ast.CSSNode;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.js.parsing.ast.JSNode;

public class HTMLOutlineLabelProvider extends CompositeOutlineLabelProvider
{

	private static final Image ELEMENT_ICON = Activator.getImage("icons/element.gif"); //$NON-NLS-1$

	public HTMLOutlineLabelProvider()
	{
		addSubLanguage(CSSNode.LANGUAGE, new CSSOutlineLabelProvider());
		addSubLanguage(JSNode.LANGUAGE, new JSOutlineLabelProvider());
	}

	@Override
	protected Image getDefaultImage(Object element)
	{
		if (element instanceof HTMLNode)
		{
			return ELEMENT_ICON;
		}
		return super.getDefaultImage(element);
	}

	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof HTMLElementNode)
		{
			return ((HTMLElementNode) element).getName();
		}
		else if (element instanceof HTMLSpecialNode)
		{
			return ((HTMLSpecialNode) element).getLanguageTag();
		}
		return super.getDefaultText(element);
	}
}
