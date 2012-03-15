/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Java Utilities.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class JavaUtil
{
	/**
	 * Java Keyword enum.
	 */
	@SuppressWarnings("nls")
	public static enum Keyword
	{

		ABSTRACT("abstract"),
		CONTINUE("continue"),
		FOR("for"),
		NEW("new"),
		SWITCH("switch"),
		ASSERT("assert"),
		DEFAULT("default"),
		GOTO("goto"),
		PACKAGE("package"),
		SYNCHRONIZED("synchronized"),
		BOOLEAN("boolean"),
		DO("do"),
		IF("if"),
		PRIVATE("private"),
		THIS("this"),
		BREAK("break"),
		DOUBLE("double"),
		IMPLEMENTS("implements"),
		PROTECTED("protected"),
		THROW("throw"),
		THROWS("throws"),
		BYTE("byte"),
		ELSE("else"),
		IMPORT("import"),
		PUBLIC("public"),
		CASE("case"),
		ENUM("enum"),
		INSTANCEOF("instanceof"),
		RETURN("return"),
		TRANSIENT("transient"),
		CATCH("catch"),
		EXTENDS("extends"),
		INT("int"),
		SHORT("short"),
		TRY("try"),
		CHAR("char"),
		FINAL("final"),
		INTERFACE("interface"),
		STATIC("static"),
		VOID("void"),
		CLASS("class"),
		FINALLY("finally"),
		LONG("long"),
		STRICTFP("strictfp"),
		VOLATILE("volatile"),
		CONST("const"),
		FLOAT("float"),
		NATIVE("native"),
		SUPER("super"),
		TRUE("true"),
		FALSE("false"),
		NULL("null"),
		WHILE("while");

		private String keyword;
		private static Set<String> allKeywords;
		static
		{
			allKeywords = new HashSet<String>();
			for (Keyword key : EnumSet.allOf(Keyword.class))
			{
				allKeywords.add(key.toString());
			}
		}

		private Keyword(String keyword)
		{
			this.keyword = keyword;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return keyword;
		}

		/**
		 * Returns true if the given word is a Java keyword.
		 * 
		 * @param word
		 * @return <code>true</code> if and only if the given word is a Java keyword.
		 */
		public static boolean isKeyword(String word)
		{
			return allKeywords.contains(word);
		}
	}

}
