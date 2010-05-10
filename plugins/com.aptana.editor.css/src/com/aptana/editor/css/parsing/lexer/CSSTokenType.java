package com.aptana.editor.css.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum CSSTokenType implements ITypePredicate
{
	UNDEFINED("undefined.css"), //$NON-NLS-1$
	PROPERTY("support.type.property-name.css"), //$NON-NLS-1$
	VALUE("support.constant.property-value.css"), //$NON-NLS-1$
	UNIT("keyword.other.unit.css"), //$NON-NLS-1$
	ELEMENT("entity.name.tag.css"), //$NON-NLS-1$
	MEDIA("support.constant.media.css"), //$NON-NLS-1$
	FUNCTION("support.function.misc.css"), //$NON-NLS-1$
	COLOR("support.constant.color.w3c-standard-color-name.css"), //$NON-NLS-1$
	DEPRECATED_COLOR("invalid.deprecated.color.w3c-non-standard-color-name.css"), //$NON-NLS-1$
	FONT("support.constant.font-name.css"), //$NON-NLS-1$
	CURLY_BRACE("punctuation.section.property-list.css"), //$NON-NLS-1$
	COLON("punctuation.separator.key-value.css"), //$NON-NLS-1$
	ARGS("punctuation.section.function.css"), //$NON-NLS-1$
	SEMICOLON("punctuation.terminator.rule.css"), //$NON-NLS-1$
	RGB("constant.other.color.rgb-value.css"), //$NON-NLS-1$
	ID("entity.other.attribute-name.id.css"), //$NON-NLS-1$
	CLASS("entity.other.attribute-name.class.css"), //$NON-NLS-1$
	NUMBER("constant.numeric.css"), //$NON-NLS-1$
	AT_RULE("keyword.control.at-rule.media.css"), //$NON-NLS-1$
	IDENTIFIER("source.css"), //$NON-NLS-1$
	DOUBLE_QUOTED_STRING("string.quoted.double.css"), //$NON-NLS-1$
	SINGLE_QUOTED_STRING("string.quoted.single.css"), //$NON-NLS-1$
	COMMENT("comment.block.css"), //$NON-NLS-1$
	
	// start custom enums needed for CA
	
	COMMA("punctuation.separator.css"), //$NON-NLS-1$
	SLASH("punctuation.slash.css"), //$NON-NLS-1$
	STAR("punctuation.asterisk.css"); //$NON-NLS-1$

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
