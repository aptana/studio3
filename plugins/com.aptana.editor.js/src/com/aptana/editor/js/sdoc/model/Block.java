package com.aptana.editor.js.sdoc.model;

import java.util.LinkedList;
import java.util.List;

import beaver.Symbol;

public class Block extends Symbol
{
	private String _text;
	private List<Tag> _tags;

	/**
	 * Block
	 * 
	 * @param tags
	 */
	public Block(List<Tag> tags)
	{
		this("", tags);
	}

	/**
	 * Block
	 * 
	 * @param text
	 */
	public Block(String text)
	{
		this(text, new LinkedList<Tag>());
	}

	/**
	 * Block
	 * 
	 * @param text
	 * @param tags
	 */
	public Block(String text, List<Tag> tags)
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
}
