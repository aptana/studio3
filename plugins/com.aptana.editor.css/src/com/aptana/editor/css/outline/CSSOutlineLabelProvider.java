package com.aptana.editor.css.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.css.Activator;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSSelectorNode;

public class CSSOutlineLabelProvider extends LabelProvider
{

	private static final Image SELECTOR_ICON = Activator.getImage("icons/selector.png"); //$NON-NLS-1$
	private static final Image DECLARATION_ICON = Activator.getImage("icons/declaration.png"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CSSSelectorNode)
		{
			return SELECTOR_ICON;
		}
		if (element instanceof CSSDeclarationNode)
		{
			return DECLARATION_ICON;
		}
		return super.getImage(element);
	}
}
