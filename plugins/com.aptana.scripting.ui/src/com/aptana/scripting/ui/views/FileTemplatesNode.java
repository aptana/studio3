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
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class FileTemplatesNode extends BaseNode
{
	private static final Image FILE_TEMPLATES_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private FileTemplateNode[] _fileTemplates;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public FileTemplatesNode(BundleElement bundle)
	{
		this(bundle.getCommands());
	}

	/**
	 * FileTemplatesNode
	 * 
	 * @param elements
	 */
	public FileTemplatesNode(List<CommandElement> elements)
	{
		List<FileTemplateNode> fileTemplates = new ArrayList<FileTemplateNode>();

		if (elements != null)
		{
			Collections.sort(elements);

			for (CommandElement command : elements)
			{
				if (command instanceof TemplateElement)
				{
					fileTemplates.add(new FileTemplateNode((TemplateElement) command));
				}
			}
		}

		this._fileTemplates = fileTemplates.toArray(new FileTemplateNode[fileTemplates.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return this._fileTemplates;
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
		return this._fileTemplates.length > 0;
	}
}
