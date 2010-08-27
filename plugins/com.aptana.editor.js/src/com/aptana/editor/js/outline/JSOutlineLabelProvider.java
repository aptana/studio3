package com.aptana.editor.js.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.outline.JSOutlineItem.Type;

public class JSOutlineLabelProvider extends LabelProvider
{
	private static final Image ARRAY_ICON = Activator.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = Activator.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = Activator.getImage("icons/js_function.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = Activator.getImage("icons/js_property.png"); //$NON-NLS-1$
	private static final Image NULL_ICON = Activator.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = Activator.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_LITERAL_ICON = Activator.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image REGEX_ICON = Activator.getImage("icons/regex.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = Activator.getImage("icons/string.png"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof JSOutlineItem)
		{
			Type type = ((JSOutlineItem) element).getType();
			if (type == Type.PROPERTY)
			{
				return PROPERTY_ICON;
			}
			if (type == Type.ARRAY)
			{
				return ARRAY_ICON;
			}
			if (type == Type.BOOLEAN)
			{
				return BOOLEAN_ICON;
			}
			if (type == Type.FUNCTION)
			{
				return FUNCTION_ICON;
			}
			if (type == Type.NULL)
			{
				return NULL_ICON;
			}
			if (type == Type.NUMBER)
			{
				return NUMBER_ICON;
			}
			if (type == Type.OBJECT_LITERAL)
			{
				return OBJECT_LITERAL_ICON;
			}
			if (type == Type.REGEX)
			{
				return REGEX_ICON;
			}
			if (type == Type.STRING)
			{
				return STRING_ICON;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof JSOutlineItem)
		{
			return ((JSOutlineItem) element).getLabel();
		}
		return super.getText(element);
	}
}
