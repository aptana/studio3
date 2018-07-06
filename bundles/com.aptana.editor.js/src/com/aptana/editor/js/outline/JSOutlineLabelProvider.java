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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.editor.js.JSPlugin;

public class JSOutlineLabelProvider extends CommonOutlineLabelProvider
{

	private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	// TODO Centralize with JSModelFormatter if we can. This doesn't know about statics/deprecation/etc. I guess that'd
	// mean we'd need to convert the node to a metadata/indexing BaseElement
	private static final Image ARRAY_ICON = JSPlugin.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN_ICON = JSPlugin.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = JSPlugin.getImage("icons/js_function.png"); //$NON-NLS-1$
	private static final Image CLASS_ICON = JSPlugin.getImage("icons/class.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = JSPlugin.getImage("icons/js_property.png"); //$NON-NLS-1$
	private static final Image NULL_ICON = JSPlugin.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER_ICON = JSPlugin.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT_LITERAL_ICON = JSPlugin.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image REGEX_ICON = JSPlugin.getImage("icons/regex.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = JSPlugin.getImage("icons/string.png"); //$NON-NLS-1$
	private static final ImageDescriptor EXPORT_OVERLAY = JSPlugin.getImageDescriptor("icons/overlays/export.png"); //$NON-NLS-1$
	private static final ImageDescriptor STATIC_OVERLAY = JSPlugin.getImageDescriptor("icons/overlays/static.png"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		// TODO Add an overlay on exported items?
		if (element instanceof JSOutlineItem)
		{
			JSOutlineItem outlineItem = ((JSOutlineItem) element);
			if (outlineItem.isExported())
			{
				return resourceManager.createImage(
						new DecorationOverlayIcon(getBaseImage(outlineItem), EXPORT_OVERLAY, IDecoration.BOTTOM_RIGHT));
			}
			return getBaseImage(outlineItem);
		}
		return super.getImage(element);
	}

	public Image getBaseImage(JSOutlineItem element)
	{
		JSOutlineItem outlineItem = ((JSOutlineItem) element);
		switch (outlineItem.getType())
		{
			case PROPERTY:
				if (outlineItem.isStatic())
				{
					return resourceManager.createImage(
							new DecorationOverlayIcon(PROPERTY_ICON, STATIC_OVERLAY, IDecoration.TOP_RIGHT));
				} else {
					return PROPERTY_ICON;
				}
			case ARRAY:
				return ARRAY_ICON;
			case FUNCTION:
				if (outlineItem.isStatic())
				{
					return resourceManager.createImage(
							new DecorationOverlayIcon(FUNCTION_ICON, STATIC_OVERLAY, IDecoration.TOP_RIGHT));
				} else {
					return FUNCTION_ICON;
				}
			case CLASS:
				return CLASS_ICON;
			case BOOLEAN:
				return BOOLEAN_ICON;
			case NULL:
				return NULL_ICON;
			case NUMBER:
				return NUMBER_ICON;
			case OBJECT_LITERAL:
				return OBJECT_LITERAL_ICON;
			case REGEX:
				return REGEX_ICON;
			case STRING:
				return STRING_ICON;

			default:
				return null;
		}
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

	@Override
	public void dispose()
	{
		super.dispose();
		resourceManager.dispose();
	}
}
