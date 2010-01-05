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
		return execute(null);
	}

	/**
	 * execute
	 */
	public CommandResult execute(CommandContext context)
	{
		return new CommandResult(this.getExpansion(), InputType.get((String) context.get(CommandContext.INPUT_TYPE)));
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
