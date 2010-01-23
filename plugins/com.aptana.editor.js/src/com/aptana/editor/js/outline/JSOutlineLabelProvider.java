package com.aptana.editor.js.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;

public class JSOutlineLabelProvider extends LabelProvider
{
	private static final Image ARRAY_ICON = Activator.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = Activator.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = Activator.getImage("icons/js_function.gif"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = Activator.getImage("icons/js_property.gif"); //$NON-NLS-1$
	private static final Image NULL_ICON = Activator.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = Activator.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_LITERAL_ICON = Activator.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image REGEX_ICON = Activator.getImage("icons/regex.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = Activator.getImage("icons/string.png"); //$NON-NLS-1$

	public Image getImage(Object element)
	{
		Image result;

		if (element instanceof JSNode)
		{
			JSNode item = (JSNode) element;

			switch (item.getType())
			{
				case JSNodeTypes.FUNCTION:
					return FUNCTION_ICON;
				case JSNodeTypes.NULL:
					return NULL_ICON;
				case JSNodeTypes.NUMBER:
					return NUMBER_ICON;
				case JSNodeTypes.REGEX:
					return REGEX_ICON;
				case JSNodeTypes.STRING:
					return STRING_ICON;
			}
		}
		return super.getImage(element);
	}
}
