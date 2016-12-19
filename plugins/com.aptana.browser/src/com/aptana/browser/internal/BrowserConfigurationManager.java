/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.browser.BrowserPlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;

public class BrowserConfigurationManager
{

	private static final String EXTENSION_POINT_ID = "configuration"; //$NON-NLS-1$
	private static final String ELEMENT_SIZE = "size"; //$NON-NLS-1$
	private static final String ELEMENT_SIZE_CATEGORY = "sizeCategory"; //$NON-NLS-1$
	private static final String ELEMENT_BACKGROUND_IMAGE = "backgroundImage"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_ORDER = "order"; //$NON-NLS-1$
	private static final String ATT_WIDTH = "width"; //$NON-NLS-1$
	private static final String ATT_HEIGHT = "height"; //$NON-NLS-1$
	private static final String ATT_CATEGORY = "category"; //$NON-NLS-1$
	private static final String ATT_IMAGE = "image"; //$NON-NLS-1$
	private static final String ATT_PATH = "path"; //$NON-NLS-1$
	private static final String ATT_HOR_INDENT = "horizontalIndent"; //$NON-NLS-1$
	private static final String ATT_VER_INDENT = "verticalIndent"; //$NON-NLS-1$
	private static final String ATT_BG_COLOR = "bgcolor"; //$NON-NLS-1$

	private Map<String, BrowserSizeCategory> sizeCategories;
	private Map<String, BrowserBackgroundImage> backgroundImages;
	private List<BrowserSize> sizes;

	public BrowserConfigurationManager()
	{
		sizeCategories = new HashMap<String, BrowserSizeCategory>();
		backgroundImages = new HashMap<String, BrowserBackgroundImage>();
		sizes = new ArrayList<BrowserSize>();
		readExtensionRegistry();
	}

	public void clear()
	{
		sizeCategories.clear();
		backgroundImages.clear();
		sizes.clear();
	}

	public BrowserSizeCategory[] getSizeCategories()
	{
		List<BrowserSizeCategory> categories = new ArrayList<BrowserSizeCategory>(sizeCategories.values());
		Collections.sort(categories); // sort by order
		return categories.toArray(new BrowserSizeCategory[categories.size()]);
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(BrowserPlugin.PLUGIN_ID, EXTENSION_POINT_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newInOrderSet(ELEMENT_SIZE_CATEGORY, ELEMENT_BACKGROUND_IMAGE,
								ELEMENT_SIZE);
					}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		String name = element.getName();

		if (ELEMENT_SIZE_CATEGORY.equals(name))
		{
			String categoryId = element.getAttribute(ATT_ID);
			if (StringUtil.isEmpty(categoryId))
			{
				return;
			}
			String categoryName = element.getAttribute(ATT_NAME);
			if (StringUtil.isEmpty(categoryName))
			{
				return;
			}
			int order = Byte.MAX_VALUE;
			String orderStr = element.getAttribute(ATT_ORDER);
			if (!StringUtil.isEmpty(orderStr))
			{
				try
				{
					order = Integer.parseInt(orderStr);
				}
				catch (NumberFormatException e)
				{
					IdeLog.logWarning(BrowserPlugin.getDefault(), e);
				}
			}
			sizeCategories.put(categoryId, new BrowserSizeCategory(categoryId, categoryName, order));
		}
		else if (ELEMENT_BACKGROUND_IMAGE.equals(name))
		{
			String imageId = element.getAttribute(ATT_ID);
			if (StringUtil.isEmpty(imageId))
			{
				return;
			}
			String imagePath = element.getAttribute(ATT_PATH);
			if (StringUtil.isEmpty(imagePath))
			{
				return;
			}
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					element.getNamespaceIdentifier(), imagePath);
			int horizontalIndent = 0;
			try
			{
				horizontalIndent = Integer.parseInt(element.getAttribute(ATT_HOR_INDENT));
				if (horizontalIndent < 0)
				{
					horizontalIndent = 0;
				}
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(BrowserPlugin.getDefault(), e);
			}
			int verticalIndent = 0;
			try
			{
				verticalIndent = Integer.parseInt(element.getAttribute(ATT_VER_INDENT));
				if (verticalIndent < 0)
				{
					verticalIndent = 0;
				}
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(BrowserPlugin.getDefault(), e);
			}
			boolean blackBackground = false;
			String bgcolor = element.getAttribute(ATT_BG_COLOR);
			if (!StringUtil.isEmpty(bgcolor))
			{
				blackBackground = Boolean.parseBoolean(bgcolor);
			}
			backgroundImages.put(imageId, new BrowserBackgroundImage(imageId, imageDescriptor, horizontalIndent,
					verticalIndent, blackBackground));
		}
		else if (ELEMENT_SIZE.equals(element.getName()))
		{
			String sizeName = element.getAttribute(ATT_NAME);
			if (StringUtil.isEmpty(sizeName))
			{
				return;
			}
			String widthStr = element.getAttribute(ATT_WIDTH);
			if (StringUtil.isEmpty(widthStr))
			{
				return;
			}
			int width = 0;
			try
			{
				width = Integer.parseInt(widthStr);
				if (width < 0)
				{
					width = 0;
				}
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(BrowserPlugin.getDefault(), e);
			}
			String heightStr = element.getAttribute(ATT_HEIGHT);
			if (StringUtil.isEmpty(heightStr))
			{
				return;
			}
			int height = 0;
			try
			{
				height = Integer.parseInt(heightStr);
				if (height < 0)
				{
					height = 0;
				}
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(BrowserPlugin.getDefault(), e);
			}
			String categoryId = element.getAttribute(ATT_CATEGORY);
			if (StringUtil.isEmpty(categoryId))
			{
				categoryId = StringUtil.EMPTY;
			}
			String imageId = element.getAttribute(ATT_IMAGE);

			BrowserSizeCategory category = sizeCategories.get(categoryId);
			if (category == null)
			{
				// constructs a default category
				String defaultCategoryId = "default"; //$NON-NLS-1$
				category = sizeCategories.get(defaultCategoryId);
				if (category == null)
				{
					sizeCategories.put(defaultCategoryId, category = new BrowserSizeCategory(defaultCategoryId,
							"Default", Integer.MAX_VALUE)); //$NON-NLS-1$
				}
			}
			BrowserSize size = new BrowserSize(sizeName, width, height, category, backgroundImages.get(imageId));
			sizes.add(size);
			category.addSize(size);
		}
	}
}
