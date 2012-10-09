// $codepro.audit.disable unnecessaryImport
// NOTE: CodePro is saying "import com.aptana.editor.js.outline.JSOutlineItem.Type;"
// is unnecessary even though it is required. Also, I couldn't disable code pro
// on that import statement and had to place this at the file level
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.outline.JSOutlineItem.Type;

public class JSOutlineLabelProvider extends CommonOutlineLabelProvider
{
	// TODO Centralize with JSModelFormatter if we can. This doesn't know about statics/deprecation/etc. I guess that'd
	// mean we'd need to convert the node to a metadata/indexing BaseElement
	private static final Image ARRAY_ICON = JSPlugin.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = JSPlugin.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = JSPlugin.getImage("icons/js_function.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = JSPlugin.getImage("icons/js_property.png"); //$NON-NLS-1$
	private static final Image NULL_ICON = JSPlugin.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = JSPlugin.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_LITERAL_ICON = JSPlugin.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image REGEX_ICON = JSPlugin.getImage("icons/regex.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = JSPlugin.getImage("icons/string.png"); //$NON-NLS-1$

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
