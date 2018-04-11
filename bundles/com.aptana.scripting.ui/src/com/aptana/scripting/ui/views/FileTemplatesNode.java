/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class FileTemplatesNode extends BaseNode<FileTemplatesNode.Property>
{
	enum Property implements IPropertyInformation<FileTemplatesNode>
	{
		COUNT(Messages.FileTemplatesNode_File_Templates_Count)
		{
			public Object getPropertyValue(FileTemplatesNode node)
			{
				return node.fileTemplates.length;
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}

		public Object getPropertyValue(FileTemplatesNode node)
		{
			return null;
		}
	}

	private static final Image FILE_TEMPLATES_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private FileTemplateNode[] fileTemplates;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	FileTemplatesNode(BundleElement bundle)
	{
		this(bundle.getCommands());
	}

	/**
	 * FileTemplatesNode
	 * 
	 * @param elements
	 */
	FileTemplatesNode(List<CommandElement> elements)
	{
		List<FileTemplateNode> items = new ArrayList<FileTemplateNode>();

		if (elements != null)
		{
			Collections.sort(elements);

			for (CommandElement command : elements)
			{
				if (command instanceof TemplateElement)
				{
					items.add(new FileTemplateNode((TemplateElement) command));
				}
			}
		}

		fileTemplates = items.toArray(new FileTemplateNode[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return fileTemplates;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return FILE_TEMPLATES_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return Messages.FileTemplatesNode_FileTemplatesNodeName;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return fileTemplates.length > 0;
	}
}
