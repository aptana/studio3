package com.aptana.editor.js.sdoc.model;

import java.util.LinkedList;
import java.util.List;

import com.aptana.parsing.io.SourceWriter;

import beaver.Symbol;

public class DocumentationBlock extends Symbol
{
	private String _text;
	private List<Tag> _tags;

	/**
	 * Block
	 * 
	 * @param tags
	 */
	public DocumentationBlock(List<Tag> tags)
	{
		this("", tags);
	}

	/**
	 * Block
	 * 
	 * @param text
	 */
	public DocumentationBlock(String text)
	{
		this(text, new LinkedList<Tag>());
	}

	/**
	 * Block
	 * 
	 * @param text
	 * @param tags
	 */
	public DocumentationBlock(String text, List<Tag> tags)
	{
		this._text = text;
		this._tags = tags;
	}

	/**
	 * getTags
	 * 
	 * @return
	 */
	public List<Tag> getTags()
	{
		return this._tags;
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		return this._text;
	}
	
	/**
	 * hasTags
	 * 
	 * @return
	 */
	public boolean hasTags()
	{
		return (this._tags != null && this._tags.isEmpty() == false);
	}
	
	/**
	 * setRange
	 * 
	 * @param start
	 * @param end
	 */
	public void setRange(int start, int end)
	{
		this.start = start;
		this.end = end;
	}
	
	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourceWriter writer = new SourceWriter(" * ");
		
		this.toSource(writer);
		
		return writer.toString();
	}
	
	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourceWriter writer)
	{
		writer.println("/**").increaseIndent();
		
		if (this._text != null && this._text.isEmpty() == false)
		{
			writer.printlnWithIndent(this._text);
			
			if (this.hasTags())
			{
				writer.printIndent().println();
			}
		}
		
		if (this.hasTags())
		{
			for (Tag tag : this._tags)
			{
				writer.printIndent();
				tag.toSource(writer);
				writer.println();
			}
		}
		
		writer.decreaseIndent().println(" */");
	}
}
