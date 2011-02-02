/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public class SnippetElement extends CommandElement
{
	private String _expansion;
	
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
	 * Always executable.
	 */
	@Override
	public boolean isExecutable()
	{
		return true;
	}

}
