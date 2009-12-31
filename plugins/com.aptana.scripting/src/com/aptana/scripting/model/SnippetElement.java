package com.aptana.scripting.model;

public class SnippetElement extends CommandElement
{
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
		return new CommandResult(this.getExpansion());
	}

	/**
	 * execute
	 */
	public CommandResult execute(CommandContext context)
	{
		return new CommandResult(this.getExpansion());
	}

	/**
	 * getExpansion
	 * 
	 * @return
	 */
	public String getExpansion()
	{
		return this.getInvoke();
	}

	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	public void setExpansion(String expansion)
	{
		this.setInvoke(expansion);
	}
}
