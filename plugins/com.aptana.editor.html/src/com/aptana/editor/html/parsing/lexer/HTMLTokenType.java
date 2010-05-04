package com.aptana.editor.html.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum HTMLTokenType implements ITypePredicate
{
	UNDEFINED("undefined.html"),
	DOUBLE_QUOTED_STRING("string.quoted.double.html"),
	SINGLE_QUOTED_STRING("string.quoted.single.html"),
	ATTRIBUTE("entity.other.attribute-name.html"),
	ID("entity.other.attribute-name.id.html"),
	CLASS("entity.other.attribute-name.class.html"),
	META("meta.tag.other.html"),
	SCRIPT("entity.name.tag.script.html"),
	STYLE("entity.name.tag.style.html"),
	STRUCTURE_TAG("entity.name.tag.structure.any.html"),
	BLOCK_TAG("entity.name.tag.block.any.html"),
	INLINE_TAG("entity.name.tag.inline.any.html"),
	TAG_END("punctuation.definition.tag.end.html"),
	EQUAL("punctuation.separator.key-value.html"),
	TAG_START("punctuation.definition.tag.begin.html"),
	TEXT("text");

	private static final Map<String, HTMLTokenType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, HTMLTokenType>();

		for (HTMLTokenType type : EnumSet.allOf(HTMLTokenType.class))
		{
			NAME_MAP.put(type.getName(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final HTMLTokenType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param name
	 */
	private HTMLTokenType(String name)
	{
		this._name = name;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.lexer.ITypePredicate#isDefined()
	 */
	@Override
	public boolean isDefined()
	{
		return (this != UNDEFINED);
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getName();
	}
}
