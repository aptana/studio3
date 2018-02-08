/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class FileTemplateNode extends BaseNode<FileTemplateNode.Property>
{
	enum Property implements IPropertyInformation<FileTemplateNode>
	{
		NAME(Messages.FileTemplateNode_File_Template_Name)
		{
			public Object getPropertyValue(FileTemplateNode node)
			{
				return node.fileTemplate.getDisplayName();
			}
		},
		PATH(Messages.FileTemplateNode_File_Template_Path)
		{
			public Object getPropertyValue(FileTemplateNode node)
			{
				return node.fileTemplate.getPath();
			}
		},
		SCOPE(Messages.FileTemplateNode_File_Template_Scope)
		{
			public Object getPropertyValue(FileTemplateNode node)
			{
				String scope = node.fileTemplate.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.FileTemplateNode_All_Scopes;
			}
		},
		FILE_TYPE(Messages.FileTemplateNode_File_Template_Type)
		{
			public Object getPropertyValue(FileTemplateNode node)
			{
				return node.fileTemplate.getFiletype();
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
	}

	private static final Image FILE_TEMPLATE_ICON = ScriptingUIPlugin.getImage("icons/template.png"); //$NON-NLS-1$
	private TemplateElement fileTemplate;

	/**
	 * SnippetNode
	 * 
	 * @param fileTemplate
	 */
	FileTemplateNode(TemplateElement fileTemplate)
	{
		this.fileTemplate = fileTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return FILE_TEMPLATE_ICON;
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
		return fileTemplate.getDisplayName();
	}
}
