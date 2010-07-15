package com.aptana.scripting.model;

public class ContentAssistElement extends CommandElement
{
	
	/**
	 * ContentAssistElement
	 * 
	 * @param name
	 */
	public ContentAssistElement(String path)
	{
		super(path);
		
		this.setInputType(InputType.NONE);
		this.setOutputType(OutputType.DISCARD);
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "content_assist"; //$NON-NLS-1$
	}

}
