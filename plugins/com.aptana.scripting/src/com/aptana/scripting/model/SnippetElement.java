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

	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("snippet \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
		printer.printWithIndent("trigger: ").println(this.getTrigger()); //$NON-NLS-1$

		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
