/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.aptana.core.util.CollectionsUtil;

public class SnippetElement extends CommandElement
{
	private String _expansion;
	private String _category;
	private List<String> _tags;
	private String _iconPath;
	private URL _iconURL;
	private String _description;

	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public SnippetElement(String path)
	{
		super(path);

		this.setInputType(InputType.NONE);
		this.setOutputType(OutputType.INSERT_AS_SNIPPET);
	}

	/**
	 * execute
	 */
	public CommandResult execute()
	{
		return execute(null);
	}

	/**
	 * execute
	 */
	public CommandResult execute(CommandContext context)
	{
		// set output type
		context.setOutputType(getOutputType());

		CommandResult result = new CommandResult(this, context);

		// set result
		result.setOutputString(this.getExpansion());

		// indicate successful execution so that command result processing will work
		result.setExecutedSuccessfully(true);

		// grab input type so we can report back which input was used
		String inputTypeString = (String) context.get(CommandContext.INPUT_TYPE);
		InputType inputType = InputType.get(inputTypeString);

		result.setInputType(inputType);

		return result;
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "snippet"; //$NON-NLS-1$
	}

	/**
	 * getExpansion
	 * 
	 * @return
	 */
	public String getExpansion()
	{
		return this._expansion;
	}

	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	public void setExpansion(String expansion)
	{
		this._expansion = expansion;
	}

	/**
	 * @return the category
	 */
	public String getCategory()
	{
		return _category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category)
	{
		this._category = category;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags()
	{
		return CollectionsUtil.getListValue(_tags);
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags)
	{
		if (tags != null)
		{
			this._tags = new ArrayList<String>(tags);
		}
		else
		{
			this._tags = null;
		}
	}

	/**
	 * setIconPath
	 * 
	 * @param iconPath
	 */
	public void setIconPath(String iconPath)
	{
		_iconPath = iconPath;
	}

	/**
	 * @return icon path
	 */
	public String getIconPath()
	{
		return _iconPath;
	}

	/**
	 * @return the icon
	 */
	public URL getIconURL()
	{
		if (_iconURL != null)
		{
			return _iconURL;
		}

		_iconURL = getURLFromPath(_iconPath);

		return _iconURL;
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return _description;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		_description = description;
	}

	/**
	 * Always executable.
	 */
	@Override
	public boolean isExecutable()
	{
		return true;
	}

}
