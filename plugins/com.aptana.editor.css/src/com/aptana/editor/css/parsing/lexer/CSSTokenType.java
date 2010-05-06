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
	
	// start custom enums needed for CA
	
	COMMA("punctuation.separator.css"),
	SLASH("punctuation.slash.css"),
	STAR("punctuation.asterisk.css");

	private static final Map<String, CSSTokenType> NAME_MAP;
	private String _scope;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, CSSTokenType>();

		for (CSSTokenType type : EnumSet.allOf(CSSTokenType.class))
		{
			NAME_MAP.put(type.getScope(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param scope
	 * @return
	 */
	public static final CSSTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param scope
	 */
	private CSSTokenType(String scope)
	{
		this._scope = scope;
	}

	/**
	 * getScope
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
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
		return this.getScope();
	}
}
