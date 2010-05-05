package com.aptana.editor.css.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum CSSTokenType implements ITypePredicate
{
	UNDEFINED("undefined.css"),
	PROPERTY("support.type.property-name.css"),
	VALUE("support.constant.property-value.css"),
	UNIT("keyword.other.unit.css"),
	ELEMENT("entity.name.tag.css"),
	MEDIA("support.constant.media.css"),
	FUNCTION("support.function.misc.css"),
	COLOR("support.constant.color.w3c-standard-color-name.css"),
	DEPRECATED_COLOR("invalid.deprecated.color.w3c-non-standard-color-name.css"),
	FONT("support.constant.font-name.css"),
	CURLY_BRACE("punctuation.section.property-list.css"),
	COLON("punctuation.separator.key-value.css"),
	ARGS("punctuation.section.function.css"),
	SEMICOLON("punctuation.terminator.rule.css"),
	RGB("constant.other.color.rgb-value.css"),
	ID("entity.other.attribute-name.id.css"),
	CLASS("entity.other.attribute-name.class.css"),
	NUMBER("constant.numeric.css"),
	AT_RULE("keyword.control.at-rule.media.css"),
	IDENTIFIER("source.css"),
	DOUBLE_QUOTED_STRING("string.quoted.double.css"),
	SINGLE_QUOTED_STRING("string.quoted.single.css"),
	COMMENT("comment.block.css"),
	COMMA("punctuation.separator.css");

	private static final Map<String, CSSTokenType> NAME_MAP;
	private String _name;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, CSSTokenType>();

		for (CSSTokenType type : EnumSet.allOf(CSSTokenType.class))
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
	public static final CSSTokenType get(String name)
	{
		return (NAME_MAP.containsKey(name)) ? NAME_MAP.get(name) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param name
	 */
	private CSSTokenType(String name)
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
