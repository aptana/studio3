/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.lexer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import beaver.Scanner;

import com.aptana.editor.coffee.parsing.Terminals;
import com.aptana.editor.coffee.parsing.ast.CoffeeCommentNode;

@SuppressWarnings("nls")
public class CoffeeScanner extends Scanner
{
	/**
	 * Made up transient token ids used internally,
	 */
	private static final short EMPTY = -1;
	private static final short TOKENS = -2;
	private static final short NEOSTRING = -3;
	private static final short UNKNOWN = -4;

	public static final Set<String> JS_KEYWORDS = new HashSet<String>();
	static
	{
		JS_KEYWORDS.add("true");
		JS_KEYWORDS.add("false");
		JS_KEYWORDS.add("null");
		JS_KEYWORDS.add("this");
		JS_KEYWORDS.add("new");
		JS_KEYWORDS.add("delete");
		JS_KEYWORDS.add("typeof");
		JS_KEYWORDS.add("in");
		JS_KEYWORDS.add("instanceof");
		JS_KEYWORDS.add("return");
		JS_KEYWORDS.add("throw");
		JS_KEYWORDS.add("break");
		JS_KEYWORDS.add("continue");
		JS_KEYWORDS.add("debugger");
		JS_KEYWORDS.add("if");
		JS_KEYWORDS.add("else");
		JS_KEYWORDS.add("switch");
		JS_KEYWORDS.add("for");
		JS_KEYWORDS.add("while");
		JS_KEYWORDS.add("do");
		JS_KEYWORDS.add("try");
		JS_KEYWORDS.add("catch");
		JS_KEYWORDS.add("finally");
		JS_KEYWORDS.add("class");
		JS_KEYWORDS.add("extends");
		JS_KEYWORDS.add("super");
	}
	public static final Set<String> COFFEE_KEYWORDS = new HashSet<String>();
	static
	{
		COFFEE_KEYWORDS.add("undefined");
		COFFEE_KEYWORDS.add("then");
		COFFEE_KEYWORDS.add("unless");
		COFFEE_KEYWORDS.add("until");
		COFFEE_KEYWORDS.add("loop");
		COFFEE_KEYWORDS.add("of");
		COFFEE_KEYWORDS.add("by");
		COFFEE_KEYWORDS.add("when");
	}
	private static final Map<String, String> COFFEE_ALIAS_MAP = new HashMap<String, String>();
	static
	{
		COFFEE_ALIAS_MAP.put("and", "&&");
		COFFEE_ALIAS_MAP.put("or", "||");
		COFFEE_ALIAS_MAP.put("is", "==");
		COFFEE_ALIAS_MAP.put("isnt", "!=");
		COFFEE_ALIAS_MAP.put("not", "!");
		COFFEE_ALIAS_MAP.put("yes", "true");
		COFFEE_ALIAS_MAP.put("no", "false");
		COFFEE_ALIAS_MAP.put("on", "true");
		COFFEE_ALIAS_MAP.put("off", "false");
	}
	private static final Set<String> COFFEE_ALIASES = new HashSet<String>();
	static
	{
		for (String key : COFFEE_ALIAS_MAP.keySet())
		{
			COFFEE_ALIASES.add(key);
		}
		COFFEE_KEYWORDS.addAll(COFFEE_ALIASES);
	}
	private static final Set<String> RESERVED = new HashSet<String>();
	static
	{
		RESERVED.add("case");
		RESERVED.add("default");
		RESERVED.add("function");
		RESERVED.add("var");
		RESERVED.add("void");
		RESERVED.add("with");
		RESERVED.add("const");
		RESERVED.add("let");
		RESERVED.add("enum");
		RESERVED.add("export");
		RESERVED.add("import");
		RESERVED.add("native");
		RESERVED.add("__hasProp");
		RESERVED.add("__extends");
		RESERVED.add("__slice");
		RESERVED.add("__bind");
		RESERVED.add("__indexOf");
	}
	private static final Set<String> JS_FORBIDDEN = new HashSet<String>();
	static
	{
		JS_FORBIDDEN.addAll(JS_KEYWORDS);
		JS_FORBIDDEN.addAll(RESERVED);
		// This is shown as "exports.RESERVED" in JS
		// RESERVED.addAll(JS_KEYWORDS);
		// RESERVED.addAll(COFFEE_KEYWORDS);
	}

	private static final Pattern IDENTIFIER = Pattern
			.compile("^([$A-Za-z_\\x7f-\\uffff][$\\w\\x7f-\\uffff]*)([^\\n\\S]*:(?!:))?");
	private static final Pattern NUMBER = Pattern.compile("^0x[\\da-f]+|^\\d*\\.?\\d+(?:e[+-]?\\d+)?",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern HEREDOC = Pattern.compile("^(\"\"\"|''')([\\s\\S]*?)(?:\\n[^\\n\\S]*)?\\1");
	private static final Pattern OPERATOR = Pattern
			.compile("^(?:[-=]>|[-+*\\/%<>&|^!?=]=|>>>=?|([-+:])\\1|([&|<>])\\2=?|\\?\\.|\\.{2,3})");
	private static final Pattern WHITESPACE = Pattern.compile("^[^\\n\\S]+");
	private static final Pattern COMMENT = Pattern
			.compile("^###([^#][\\s\\S]*?)(?:###[^\\n\\S]*|(?:###)?$)|^(?:\\s*#(?!##[^#]).*)+");
	private static final Pattern MULTI_DENT = Pattern.compile("^(?:\\n[^\\n\\S]*)+");
	private static final Pattern SIMPLESTR = Pattern.compile("^'[^\\\\']*(?:\\\\.[^\\\\']*)*'");
	private static final Pattern JSTOKEN = Pattern.compile("^`[^\\\\`]*(?:\\\\.[^\\\\`]*)*`");

	private static final String REGEX_PATTERN = "^ / (?! [\\s=] )" + // disallow leading whitespace or equals signs
			"[^ \\[ / \\n \\\\ ]*" + // every other thing
			"(?:" + //
			"  (?: \\\\[\\s\\S]" + // anything escaped
			"    | \\[" + // character class
			"          [^ \\] \\n \\\\ ]*" + //
			"          (?: \\\\[\\s\\S] [^ \\] \\n \\\\ ]* )*" + //
			"        ]" + //
			"  ) [^ \\[ / \\n \\\\ ]*" + //
			")*" + //
			"/ [imgy]{0,4} (?!\\w)";
	private static final Pattern REGEX = Pattern.compile(REGEX_PATTERN, Pattern.COMMENTS);

	private static final Pattern HEREGEX = Pattern.compile("^\\/{3}([\\s\\S]+?)\\/{3}([imgy]{0,4})(?!\\w)");
	private static final Pattern HEREGEX_OMIT = Pattern.compile("\\s+(?:#.*)?");
	private static final Pattern MULTILINER = Pattern.compile("\\n");
	private static final Pattern HEREDOC_INDENT = Pattern.compile("\\n+([^\\n\\S]*)");
	private static final Pattern HEREDOC_ILLEGAL = Pattern.compile("\\*\\/");
	private static final Pattern LINE_CONTINUER = Pattern.compile("^\\s*(?:,|\\??\\.(?![.\\d])|::)");
	private static final Pattern TRAILING_SPACES = Pattern.compile("\\s+$");

	private static final Set<String> COMPOUND_ASSIGN = new HashSet<String>();
	static
	{
		COMPOUND_ASSIGN.add("-=");
		COMPOUND_ASSIGN.add("+=");
		COMPOUND_ASSIGN.add("/=");
		COMPOUND_ASSIGN.add("*=");
		COMPOUND_ASSIGN.add("%=");
		COMPOUND_ASSIGN.add("||=");
		COMPOUND_ASSIGN.add("&&=");
		COMPOUND_ASSIGN.add("?=");
		COMPOUND_ASSIGN.add("<<=");
		COMPOUND_ASSIGN.add(">>=");
		COMPOUND_ASSIGN.add(">>>=");
		COMPOUND_ASSIGN.add("&=");
		COMPOUND_ASSIGN.add("^=");
		COMPOUND_ASSIGN.add("|=");
	}
	private static final Set<String> UNARY = new HashSet<String>();
	static
	{
		UNARY.add("!");
		UNARY.add("~");
		UNARY.add("NEW");
		UNARY.add("TYPEOF");
		UNARY.add("DELETE");
		UNARY.add("DO");
	}
	private static final Set<String> LOGIC = new HashSet<String>();
	static
	{
		LOGIC.add("&&");
		LOGIC.add("||");
		LOGIC.add("&");
		LOGIC.add("|");
		LOGIC.add("^");
	}
	private static final Set<String> SHIFT = new HashSet<String>();
	static
	{
		SHIFT.add("<<");
		SHIFT.add(">>");
		SHIFT.add(">>>");
	}
	private static final Set<String> COMPARE = new HashSet<String>();
	static
	{
		COMPARE.add("==");
		COMPARE.add("!=");
		COMPARE.add("<");
		COMPARE.add(">");
		COMPARE.add("<=");
		COMPARE.add(">=");
	}
	private static final Set<String> MATH = new HashSet<String>();
	static
	{
		MATH.add("*");
		MATH.add("/");
		MATH.add("%");
	}
	private static final Set<String> RELATION = new HashSet<String>();
	static
	{
		RELATION.add("IN");
		RELATION.add("OF");
		RELATION.add("INSTANCEOF");
	}
	private static final Set<String> BOOL = new HashSet<String>();
	static
	{
		BOOL.add("TRUE");
		BOOL.add("FALSE");
		BOOL.add("NULL");
		BOOL.add("UNDEFINED");
	}
	private static final Set<Short> NOT_REGEX = new HashSet<Short>();
	static
	{
		NOT_REGEX.add(Terminals.NUMBER);
		NOT_REGEX.add(Terminals.REGEX);
		NOT_REGEX.add(Terminals.BOOL);
		NOT_REGEX.add(Terminals.PLUS_PLUS);
		NOT_REGEX.add(Terminals.MINUS_MINUS);
		NOT_REGEX.add(Terminals.RBRACKET);
	}
	private static final Set<Short> NOT_SPACED_REGEX = new HashSet<Short>();
	static
	{
		NOT_SPACED_REGEX.addAll(NOT_REGEX);
		NOT_SPACED_REGEX.add(Terminals.RPAREN);
		NOT_SPACED_REGEX.add(Terminals.RCURLY);
		NOT_SPACED_REGEX.add(Terminals.THIS);
		NOT_SPACED_REGEX.add(Terminals.IDENTIFIER);
		NOT_SPACED_REGEX.add(Terminals.STRING);
	}
	private static final Set<Short> CALLABLE = new HashSet<Short>();
	static
	{
		CALLABLE.add(Terminals.IDENTIFIER);
		CALLABLE.add(Terminals.STRING);
		CALLABLE.add(Terminals.REGEX);
		CALLABLE.add(Terminals.RPAREN);
		CALLABLE.add(Terminals.RBRACKET);
		CALLABLE.add(Terminals.RCURLY);
		CALLABLE.add(Terminals.QUESTION);
		CALLABLE.add(Terminals.DOUBLE_COLON);
		CALLABLE.add(Terminals.AT_SIGIL);
		CALLABLE.add(Terminals.THIS);
		CALLABLE.add(Terminals.SUPER);
	}
	private static final Set<Short> INDEXABLE = new HashSet<Short>();
	static
	{
		INDEXABLE.addAll(CALLABLE);
		INDEXABLE.add(Terminals.NUMBER);
		INDEXABLE.add(Terminals.BOOL);
	}
	private static final Set<Short> LINE_BREAK = new HashSet<Short>();
	static
	{
		LINE_BREAK.add(Terminals.INDENT);
		LINE_BREAK.add(Terminals.OUTDENT);
		LINE_BREAK.add(Terminals.TERMINATOR);
	}

	private String fCode;
	private int fLine;
	private int fIndent;
	private int fIndebt;
	private int fOutdebt;
	private List<CoffeeSymbol> fTokens;
	private String fChunk;
	private boolean fSeenFor;
	private List<Integer> fIndents;
	private List<CoffeeCommentNode> fComments;
	private int fOffset;

	@Override
	public synchronized CoffeeSymbol nextToken() throws IOException, Exception
	{
		if (this.fTokens == null)
		{
			tokenize(fCode, null);
		}
		if (this.fTokens.isEmpty())
		{
			return new CoffeeSymbol(Terminals.EOF, null);
		}
		return this.fTokens.remove(0);
	}

	private List<CoffeeSymbol> tokenize(String code, Map<String, Object> opts) throws SyntaxError
	{
		if (opts == null)
		{
			opts = new HashMap<String, Object>();
		}
		if (WHITESPACE.matcher(code).find())
		{
			code = "\n" + code;
		}
		code = code.replaceAll("\r", "").replaceFirst(TRAILING_SPACES.pattern(), "");
		this.fCode = code;
		this.fLine = 0;
		if (opts.containsKey("fLine"))
		{
			Object value = opts.get("fLine");
			if (value instanceof Integer)
			{
				this.fLine = (Integer) value;
			}
			else if (value instanceof String)
			{
				try
				{
					this.fLine = Integer.parseInt((String) value);
				}
				catch (NumberFormatException e)
				{
					this.fLine = 0;
				}
			}
		}
		this.fIndent = 0;
		this.fIndebt = 0;
		this.fOutdebt = 0;
		this.fIndents = new ArrayList<Integer>();
		this.fTokens = new ArrayList<CoffeeSymbol>();
		this.fComments = new ArrayList<CoffeeCommentNode>();
		this.fOffset = 0;

		while (fOffset < code.length() && (this.fChunk = code.substring(fOffset)).length() > 0)
		{
			int value = this.identifierToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.commentToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.whitespaceToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.lineToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.heredocToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.stringToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.numberToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.regexToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.jsToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
			value = this.literalToken();
			if (value > 0)
			{
				fOffset += value;
				continue;
			}
		}
		this.closeIndentation();
		this.fTokens = new CoffeeRewriter().rewrite(this.fTokens);

		// Let GC reclaim the memory from the last chunk and the underlying source code.
		this.fChunk = null;
		this.fCode = null;
		this.fIndents = null;

		return this.fTokens;
	}

	private int identifierToken() throws SyntaxError
	{
		// PERF fix, check first char to be sure it's letter, $, _ or unicode points defined in regexp
		char c = this.fChunk.charAt(0);
		if (!Character.isLetter(c) && c != '$' && c != '_' && (((int) c > 65535) || ((int) c < 127)))
		{
			return 0;
		}

		Matcher m = IDENTIFIER.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}

		String id = m.group(1); // token string value

		if ("own".equals(id) && Terminals.FOR == this.tag())
		{
			this.token(Terminals.OWN, id, id.length());
			return id.length();
		}

		String colon = m.group(2); // is there a colon?
		boolean forcedIdentifier = colon != null;
		if (!forcedIdentifier)
		{
			CoffeeSymbol prev = last(this.fTokens);
			if (prev != null)
			{
				short ref2 = prev.getId();
				forcedIdentifier = ref2 == Terminals.DOT || ref2 == Terminals.QUESTION_DOT
						|| ref2 == Terminals.DOUBLE_COLON || (ref2 == Terminals.AT_SIGIL && !prev.spaced);
			}
		}
		short tag = Terminals.IDENTIFIER; // token type, from Terminals

		if (JS_KEYWORDS.contains(id) || !forcedIdentifier && COFFEE_KEYWORDS.contains(id))
		{
			String upper = id.toUpperCase();
			tag = terminal(upper);
			if ("WHEN".equals(upper) && (LINE_BREAK.contains(this.tag())))
			{
				tag = Terminals.LEADING_WHEN;
			}
			else if ("FOR".equals(upper))
			{
				tag = Terminals.FOR;
				this.fSeenFor = true;
			}
			else if ("UNLESS".equals(upper))
			{
				tag = Terminals.IF;
			}
			else if (UNARY.contains(upper))
			{
				tag = Terminals.UNARY;
			}
			else if (RELATION.contains(upper))
			{
				if (!"INSTANCEOF".equals(upper) && this.fSeenFor)
				{
					tag = terminal("FOR" + upper);
					this.fSeenFor = false;
				}
				else
				{
					tag = Terminals.RELATION;
					if ("!".equals(this.value()))
					{
						this.fTokens.remove(this.fTokens.size() - 1);
						id = '!' + id;
					}
				}
			}
		}
		if (JS_FORBIDDEN.contains(id))
		{
			if (forcedIdentifier)
			{
				tag = Terminals.IDENTIFIER;
				id = new String(id);
				// id.reserved = true; // FIXME Set special flags on the generated CoffeeSymbol somehow?
			}
			else if (RESERVED.contains(id))
			{
				this.identifierError(id);
			}
		}
		if (!forcedIdentifier)
		{
			if (COFFEE_ALIASES.contains(id))
			{
				id = COFFEE_ALIAS_MAP.get(id);
			}

			if ("!".equals(id))
			{
				tag = Terminals.UNARY;
			}
			else if ("==".equals(id) || "!=".equals(id))
			{
				tag = Terminals.COMPARE;
			}
			else if ("&&".equals(id) || "||".equals(id))
			{
				tag = Terminals.LOGIC;
			}
			else if ("true".equals(id) || "false".equals(id) || "null".equals(id) || "undefined".equals(id))
			{
				tag = Terminals.BOOL;
			}
			else if ("break".equals(id) || "continue".equals(id) || "debugger".equals(id))
			{
				tag = Terminals.STATEMENT;
			}
		}
		String input = m.group(0); // raw match
		this.token(tag, id, colon == null ? input.length() : input.length() - 1);
		if (colon != null)
		{
			this.token(Terminals.COLON, ":", this.fOffset + input.length() - 1, 1);
		}
		return input.length();
	}

	private short terminal(String terminalName)
	{
		if (")".equals(terminalName))
		{
			return Terminals.RPAREN;
		}
		else if ("(".equals(terminalName))
		{
			return Terminals.LPAREN;
		}
		else if ("++".equals(terminalName))
		{
			return Terminals.PLUS_PLUS;
		}
		else if ("--".equals(terminalName))
		{
			return Terminals.MINUS_MINUS;
		}
		else if ("+".equals(terminalName))
		{
			return Terminals.PLUS;
		}
		else if ("-".equals(terminalName))
		{
			return Terminals.MINUS;
		}
		else if ("{".equals(terminalName))
		{
			return Terminals.LCURLY;
		}
		else if ("}".equals(terminalName))
		{
			return Terminals.RCURLY;
		}
		else if ("[".equals(terminalName))
		{
			return Terminals.LBRACKET;
		}
		else if ("]".equals(terminalName))
		{
			return Terminals.RBRACKET;
		}
		else if ("...".equals(terminalName))
		{
			return Terminals.ELLIPSIS;
		}
		else if ("..".equals(terminalName))
		{
			return Terminals.DOT_DOT;
		}
		else if (".".equals(terminalName))
		{
			return Terminals.DOT;
		}
		else if ("?.".equals(terminalName))
		{
			return Terminals.QUESTION_DOT;
		}
		else if ("?".equals(terminalName))
		{
			return Terminals.QUESTION;
		}
		else if (",".equals(terminalName))
		{
			return Terminals.COMMA;
		}
		else if ("=".equals(terminalName))
		{
			return Terminals.EQUAL;
		}
		else if (":".equals(terminalName))
		{
			return Terminals.COLON;
		}
		else if ("::".equals(terminalName))
		{
			return Terminals.DOUBLE_COLON;
		}
		else if (":\\".equals(terminalName))
		{
			return Terminals.COLON_SLASH;
		}
		else if ("@".equals(terminalName))
		{
			return Terminals.AT_SIGIL;
		}
		else if ("->".equals(terminalName))
		{
			return Terminals.FUNC_ARROW;
		}
		else if ("=>".equals(terminalName))
		{
			return Terminals.BOUND_FUNC_ARROW;
		}
		else if (BOOL.contains(terminalName))
		{
			return Terminals.BOOL;
		}
		else if (MATH.contains(terminalName))
		{
			return Terminals.MATH;
		}
		try
		{
			Field f = Terminals.class.getField(terminalName);
			if (f == null)
			{
				return UNKNOWN;
			}
			return (Short) f.get(null);
		}
		catch (Throwable t)
		{
			// ignore
		}
		return UNKNOWN;
	}

	private int numberToken()
	{
		// PERF fix, check for digit as first char before we try the regexp
		char c = this.fChunk.charAt(0);
		if (!Character.isDigit(c))
		{
			return 0;
		}

		Matcher m = NUMBER.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}
		String number = m.group(0);
		this.token(Terminals.NUMBER, number, number.length());
		return number.length();
	}

	private int stringToken() throws SyntaxError
	{
		String string;
		switch (this.fChunk.charAt(0))
		{
			case '\'':
				Matcher m = SIMPLESTR.matcher(this.fChunk);
				if (!m.find())
				{
					return 0;
				}
				string = m.group(0);
				this.token(Terminals.STRING, string.replaceAll(MULTILINER.pattern(), "\\\n"), string.length());
				break;
			case '"':
				string = this.balancedString(this.fChunk, '"');
				if (string == null)
				{
					return 0;
				}
				if (0 < string.indexOf("#{", 1))
				{
					this.interpolateString(string.substring(1, string.length() - 1), false, false);
				}
				else
				{
					this.token(Terminals.STRING, this.escapeLines(string), string.length());
				}
				break;
			default:
				return 0;
		}
		this.fLine += count(string, "\n");
		return string.length();
	}

	private int heredocToken() throws SyntaxError
	{
		// PERF Fix, check first char before doing expensive regexp
		char c = this.fChunk.charAt(0);
		if (c != '"' && c != '\'')
		{
			return 0;
		}

		Matcher m = HEREDOC.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}
		String heredoc = m.group(0);
		char quote = heredoc.charAt(0);
		// FIXME This used to pass in quote in the JS, but I don't see quote used in sanitizeHeredoc anywhere!
		String doc = this.sanitizeHeredoc(m.group(2), null, false);
		if (quote == '"' && 0 <= doc.indexOf("#{"))
		{
			this.interpolateString(doc, true, false);
		}
		else
		{
			this.token(Terminals.STRING, this.makeString(doc, Character.toString(quote), true), heredoc.length());
		}
		this.fLine += count(heredoc, "\n");
		return heredoc.length();
	}

	private int commentToken()
	{
		// PERF fix, check first char for space or # before trying to match the regexp
		char c = this.fChunk.charAt(0);
		if (!Character.isWhitespace(c) && c != '#')
		{
			return 0;
		}
		// Must be a '#' somewhere or we're definitely not at a comment.
		int index = this.fChunk.indexOf('#');
		if (index == -1)
		{
			return 0;
		}
		// if it's not just whitespace before '#', also not a comment.
		String leading = fChunk.substring(0, index);
		if (leading.trim().length() != 0)
		{
			return 0;
		}
		// END PERF fix.

		Matcher m = COMMENT.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}
		String comment = m.group(0);
		String here = m.group(1);
		if (here != null)
		{
			this.token(Terminals.HERECOMMENT, this.sanitizeHeredoc(here, makeIndent(this.fIndent), false),
					comment.length() - 1);
			this.token(Terminals.TERMINATOR, "\n", 1);
		}
		int addOffset = comment.indexOf('#');
		int startOffset = this.fOffset + addOffset;
		// TODO Does this properly chop down the source chunk so it doesn't point to underlying char array from fCode?
		this.fComments.add(new CoffeeCommentNode(comment.substring(addOffset), startOffset, this.fOffset
				+ comment.length()));
		this.fLine += count(comment, "\n");
		return comment.length();
	}

	private String makeIndent(int length)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			builder.append(' ');
		}
		return builder.toString();
	}

	private int jsToken()
	{
		if (this.fChunk.charAt(0) != '`')
		{
			return 0;
		}

		Matcher match = JSTOKEN.matcher(this.fChunk);
		if (!match.find())
		{
			return 0;
		}
		String script = match.group(0);
		this.token(Terminals.JS, script.substring(1, script.length() - 1), script.length());
		return script.length();
	}

	private int regexToken() throws SyntaxError
	{
		if (this.fChunk.charAt(0) != '/')
		{
			return 0;
		}

		Matcher m = HEREGEX.matcher(this.fChunk);
		if (m.find())
		{
			int length = this.heregexToken(m);
			this.fLine += count(m.group(0), "\n");
			return length;
		}
		CoffeeSymbol prev = last(this.fTokens);
		if (prev != null && ((prev.spaced ? NOT_REGEX : NOT_SPACED_REGEX).contains(prev.getId())))
		{
			return 0;
		}
		m = REGEX.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}
		String regex = m.group(0);
		this.token(Terminals.REGEX, regex.equals("//") ? "/(?:)/" : regex, regex.length());
		return regex.length();
	}

	@SuppressWarnings("unchecked")
	private int heregexToken(Matcher match) throws SyntaxError
	{
		String heregex = match.group(0);
		String body = match.group(1);
		String flags = match.group(2);
		if (0 > body.indexOf("#{"))
		{
			String re = body.replaceAll(HEREGEX_OMIT.pattern(), "").replaceAll("\\/", Matcher.quoteReplacement("\\/"));
			this.token(Terminals.REGEX, "/" + (re.length() == 0 ? "(?:)" : re) + "/" + flags, heregex.length());
			return heregex.length();
		}
		this.token(Terminals.IDENTIFIER, "RegExp");
		this.fTokens.add(new CoffeeSymbol(Terminals.CALL_START, "("));
		Stack<CoffeeSymbol> tmpTokens = new Stack<CoffeeSymbol>();
		Stack<CoffeeSymbol> interpolatedNodes = this.interpolateString(body, false, true);
		int _len = interpolatedNodes.size();
		for (int i = 0; i < _len; i++)
		{
			CoffeeSymbol interpolatedNode = interpolatedNodes.get(i);
			short tag = interpolatedNode.getId();
			Object value = interpolatedNode.getValue();
			if (tag == TOKENS)
			{
				tmpTokens.addAll((Stack<CoffeeSymbol>) value);
			}
			else
			{
				String strValue = (String) value;
				if ((strValue = strValue.replaceAll(HEREGEX_OMIT.pattern(), "")).length() == 0)
				{
					continue;
				}
				strValue = strValue.replaceAll("\\", "\\\\");
				tmpTokens.push(new CoffeeSymbol(Terminals.STRING, this.makeString(strValue, "\"", true)));
			}
			tmpTokens.push(new CoffeeSymbol(Terminals.PLUS, "+"));
		}
		tmpTokens.pop();
		if (!tmpTokens.isEmpty() && tmpTokens.get(0) != null && tmpTokens.get(0).getId() != Terminals.STRING)
		{
			this.fTokens.add(new CoffeeSymbol(Terminals.STRING, "\"\""));
			this.fTokens.add(new CoffeeSymbol(Terminals.PLUS, "+"));
		}
		this.fTokens.addAll(tmpTokens);
		if (flags != null && flags.length() > 0)
		{
			this.fTokens.add(new CoffeeSymbol(Terminals.COMMA, ","));
			this.fTokens.add(new CoffeeSymbol(Terminals.STRING, "\"" + flags + "\""));
		}
		this.token(Terminals.RPAREN, ")");
		return heregex.length();
	}

	private int lineToken()
	{
		// PERF Fix, check char before doing expensive regexp...
		if (this.fChunk.charAt(0) != '\n')
		{
			return 0;
		}

		Matcher m = MULTI_DENT.matcher(this.fChunk);
		if (!m.find())
		{
			return 0;
		}
		String indent = m.group(0);
		this.fLine += count(indent, "\n");
		// CoffeeSymbol prev = last(this.tokens, 1);
		int size = indent.length() - 1 - indent.lastIndexOf("\n");
		boolean noNewlines = this.unfinished();
		if (size - this.fIndebt == this.fIndent)
		{
			if (noNewlines)
			{
				this.suppressNewlines();
			}
			else
			{
				this.newlineToken();
			}
			return indent.length();
		}
		if (size > this.fIndent)
		{
			if (noNewlines)
			{
				this.fIndebt = size - this.fIndent;
				this.suppressNewlines();
				return indent.length();
			}
			int diff = size - this.fIndent + this.fOutdebt;
			this.token(Terminals.INDENT, diff, 0);
			this.fIndents.add(diff);
			this.fOutdebt = this.fIndebt = 0;
		}
		else
		{
			this.fIndebt = 0;
			this.outdentToken(this.fIndent - size, noNewlines);
		}
		this.fIndent = size;
		return indent.length();
	}

	private void outdentToken(int moveOut, boolean noNewlines)
	{
		Integer dent = null;
		while (moveOut > 0)
		{
			int len = this.fIndents.size() - 1;
			if (this.fIndents.get(len) == 0)
			{
				moveOut = 0;
			}
			else if (this.fIndents.get(len) == this.fOutdebt)
			{
				moveOut -= this.fOutdebt;
				this.fOutdebt = 0;
			}
			else if (this.fIndents.get(len) < this.fOutdebt)
			{
				this.fOutdebt -= this.fIndents.get(len);
				moveOut -= this.fIndents.get(len);
			}
			else
			{
				dent = this.fIndents.remove(this.fIndents.size() - 1) - this.fOutdebt;
				moveOut -= dent;
				this.fOutdebt = 0;
				this.token(Terminals.OUTDENT, dent, 0);
			}
		}
		if (dent != null)
		{
			this.fOutdebt -= moveOut;
		}
		if (!(this.tag() == Terminals.TERMINATOR || noNewlines))
		{
			this.token(Terminals.TERMINATOR, "\n", 1);
		}
		// return this;
	}

	private int whitespaceToken()
	{
		// PERF Fix, check first character before doing expensive regexp
		char c = this.fChunk.charAt(0);
		if (!Character.isWhitespace(c) || c == '\n')
		{
			return 0;
		}

		Matcher match = WHITESPACE.matcher(this.fChunk);
		boolean nline = (this.fChunk.length() > 0 && this.fChunk.charAt(0) == '\n');
		boolean matched = match.find();
		if (!(matched || nline))
		{
			return 0;
		}
		CoffeeSymbol prev = last(this.fTokens);
		if (prev != null)
		{
			if (matched)
			{
				prev.spaced = true;
			}
			else
			{
				prev.newLine = true;
			}
		}
		if (matched)
		{
			return match.group(0).length();
		}
		return 0;
	}

	private void newlineToken()
	{
		if (this.tag() != Terminals.TERMINATOR)
		{
			this.token(Terminals.TERMINATOR, "\n", 1);
		}
		// return this;
	}

	private void suppressNewlines()
	{
		if ("\\".equals(this.value()))
		{
			this.fTokens.remove(this.fTokens.size() - 1);
		}
		// return this;
	}

	private int literalToken() throws SyntaxError
	{
		String value;
		Matcher match = OPERATOR.matcher(this.fChunk);
		if (match.find())
		{
			value = match.group(0);
			// checking for "->" or "=>"
			if (value.length() == 2 && (value.charAt(0) == '-' || value.charAt(0) == '=') && value.charAt(1) == '>')
			{
				this.tagParameters();
			}
		}
		else
		{
			value = Character.toString(this.fChunk.charAt(0));
		}
		short tag = terminal(value);
		CoffeeSymbol prev = last(this.fTokens);
		if (value.equals("=") && prev != null)
		{
			if (!prev.reserved && JS_FORBIDDEN.contains(prev.getValue()))
			{
				this.assignmentError();
			}
			if (prev.getValue().equals("||") || prev.getValue().equals("&&"))
			{
				this.fTokens.remove(this.fTokens.size() - 1);
				prev = new CoffeeSymbol(Terminals.COMPOUND_ASSIGN, "" + prev.getValue() + '=');
				this.fTokens.add(prev);
				return value.length();
			}
		}
		if (value.equals(";"))
		{
			tag = Terminals.TERMINATOR;
		}
		else if (MATH.contains(value))
		{
			tag = Terminals.MATH;
		}
		else if (COMPARE.contains(value))
		{
			tag = Terminals.COMPARE;
		}
		else if (COMPOUND_ASSIGN.contains(value))
		{
			tag = Terminals.COMPOUND_ASSIGN;
		}
		else if (UNARY.contains(value))
		{
			tag = Terminals.UNARY;
		}
		else if (SHIFT.contains(value))
		{
			tag = Terminals.SHIFT;
		}
		else if (LOGIC.contains(value) || value.equals("?") && (prev != null ? prev.spaced : false))
		{
			tag = Terminals.LOGIC;
		}
		else if (prev != null && !prev.spaced)
		{
			if (value.equals("(") && CALLABLE.contains(prev.getId()))
			{
				if (prev.getId() == Terminals.QUESTION)
				{
					prev.setId(Terminals.FUNC_EXIST);
				}
				tag = Terminals.CALL_START;
			}
			else if (value.equals("[") && INDEXABLE.contains(prev.getId()))
			{
				tag = Terminals.INDEX_START;
				switch (prev.getId())
				{
					case Terminals.QUESTION:
						prev.setId(Terminals.INDEX_SOAK);
						break;
					case Terminals.DOUBLE_COLON:
						prev.setId(Terminals.INDEX_PROTO);
						break;
				}
			}
		}
		this.token(tag, value, value.length());
		return value.length();
	}

	private String sanitizeHeredoc(String doc, String indent, boolean herecomment)
	{
		if (herecomment)
		{
			Matcher m = HEREDOC_ILLEGAL.matcher(doc);
			if (m.find())
			{
				throw new Error("block comment cannot contain \"*/\", starting on fLine " + (this.fLine + 1));
			}
			if (doc.indexOf('\n') <= 0)
			{
				return doc;
			}
		}
		else
		{
			Matcher match = HEREDOC_INDENT.matcher(doc);
			while (match.find())
			{
				String attempt = match.group(1);
				int len = attempt.length();
				if (indent == null || (0 < len && len < indent.length()))
				{
					indent = attempt;
				}
			}
		}
		if (indent != null)
		{
			doc = doc.replaceAll("\\n" + indent, "\n");
		}
		if (!herecomment)
		{
			doc = doc.replaceFirst("^\n", "");
		}
		return doc;
	}

	private void tagParameters()
	{
		if (this.tag() != Terminals.RPAREN)
		{
			return;
		}
		Stack<CoffeeSymbol> stack = new Stack<CoffeeSymbol>();
		int i = fTokens.size();
		fTokens.get(--i).setId(Terminals.PARAM_END);
		CoffeeSymbol tok;
		while ((tok = fTokens.get(--i)) != null)
		{
			switch (tok.getId())
			{
				case Terminals.RPAREN:
					stack.push(tok);
					break;
				case Terminals.LPAREN:
				case Terminals.CALL_START:
					if (!stack.isEmpty())
					{
						stack.pop();
					}
					else if (tok.getId() == Terminals.LPAREN)
					{
						tok.setId(Terminals.PARAM_START);
						return;
					}
					else
					{
						return;
					}
			}
		}
		return;
	}

	private void closeIndentation()
	{
		this.outdentToken(this.fIndent, false);
	}

	private void identifierError(String word) throws SyntaxError
	{
		throw new SyntaxError("Reserved word \"" + word + "\" on fLine " + (this.fLine + 1));
	}

	private void assignmentError() throws SyntaxError
	{
		throw new SyntaxError("Reserved word \"" + (this.value()) + "\" on fLine " + (this.fLine + 1)
				+ " can't be assigned");
	}

	private String balancedString(String str, char end)
	{
		Stack<Character> stack = new Stack<Character>();
		stack.push(end);

		int len = str.length();
		boolean small = (1 <= len);
		char prev = ' ';
		for (int i = 1; (small ? (i < len) : (i > len)); i += (small ? 1 : -1))
		{
			char letter = str.charAt(i);
			switch (letter)
			{
				case '\\':
					i++;
					continue;
				default:
					if (letter == end)
					{
						stack.pop();
						if (stack.isEmpty())
						{
							return str.substring(0, i + 1);
						}
						end = stack.get(stack.size() - 1);
						continue;
					}
			}
			if (end == '}' && (letter == '"' || letter == '\''))
			{
				stack.push(end = letter);
			}
			else if (end == '}' && letter == '{')
			{
				stack.push(end = '}');
			}
			else if (end == '"' && prev == '#' && letter == '{')
			{
				stack.push(end = '}');
			}
			prev = letter;
		}
		throw new Error("missing " + (stack.pop()) + ", starting on fLine " + (this.fLine + 1));
	}

	@SuppressWarnings("unchecked")
	private Stack<CoffeeSymbol> interpolateString(String str, boolean heredoc, boolean regex) throws SyntaxError
	{
		Stack<CoffeeSymbol> tmpTokens = new Stack<CoffeeSymbol>();
		int pi = 0;
		int i = 0;
		String expr;
		for (; i < str.length(); i++)
		{
			char letter = str.charAt(i);
			if (letter == '\\')
			{
				i += 1;
				continue;
			}
			if (!(letter == '#' && str.charAt(i + 1) == '{' && (expr = this.balancedString(str.substring(i + 1), '}')) != null))
			{
				continue;
			}
			if (pi < i)
			{
				tmpTokens.push(new CoffeeSymbol(NEOSTRING, str.substring(pi, i)));
			}
			String inner = expr.substring(1, expr.length() - 1);
			if (inner.length() > 0)
			{
				Map<String, Object> newOptions = new HashMap<String, Object>();
				newOptions.put("fLine", this.fLine);
				newOptions.put("rewrite", false);
				List<CoffeeSymbol> nested = new CoffeeScanner().tokenize(inner, newOptions);
				nested.remove(nested.size() - 1);
				if (nested.get(0) != null && nested.get(0).getId() == Terminals.TERMINATOR)
				{
					nested.remove(0);
				}
				if (!nested.isEmpty())
				{
					if (nested.size() > 1)
					{
						nested.add(0, new CoffeeSymbol(Terminals.LPAREN, "("));
						nested.add(new CoffeeSymbol(Terminals.RPAREN, ")"));
					}
					tmpTokens.push(new CoffeeSymbol(TOKENS, nested));
				}
			}
			i += expr.length();
			pi = i + 1;
		}
		if ((i > pi && pi < str.length()))
		{
			tmpTokens.push(new CoffeeSymbol(NEOSTRING, str.substring(pi)));
		}
		if (regex)
		{
			return tmpTokens;
		}
		if (tmpTokens.isEmpty())
		{
			tmpTokens.push(this.token(Terminals.STRING, "\"\""));
			return tmpTokens;
		}
		if (tmpTokens.get(0).getId() != NEOSTRING)
		{
			tmpTokens.add(0, new CoffeeSymbol(EMPTY, ""));
		}
		int interpolated = tmpTokens.size();
		if (interpolated > 1)
		{
			this.token(Terminals.LPAREN, "(");
		}
		for (int x = 0; x < interpolated; x++)
		{
			CoffeeSymbol token = tmpTokens.get(x);
			short tag = token.getId();
			Object value = token.getValue();
			if (x != 0)
			{
				this.token(Terminals.PLUS, "+");
			}
			if (tag == TOKENS)
			{
				this.fTokens.addAll((List<CoffeeSymbol>) value);
			}
			else
			{
				this.token(Terminals.STRING, this.makeString((String) value, "\"", heredoc));
			}
		}
		if (interpolated != 0)
		{
			this.token(Terminals.RPAREN, ")");
		}
		return tmpTokens;
	}

	/**
	 * @deprecated. Use {@link #token(short, Object, int)} to record length/offset
	 * @param tokenType
	 * @param value
	 * @return
	 */
	private CoffeeSymbol token(short tokenType, Object value)
	{
		// FIXME Need to record the offsets!
		CoffeeSymbol symbol = new CoffeeSymbol(tokenType, value);
		this.fTokens.add(symbol);
		return symbol;
	}

	private CoffeeSymbol token(short tokenType, Object value, int length)
	{
		return token(tokenType, value, this.fOffset, length);
	}

	private CoffeeSymbol token(short tokenType, Object value, int offset, int length)
	{
		CoffeeSymbol symbol = new CoffeeSymbol(tokenType, offset, offset + length, value);
		this.fTokens.add(symbol);
		return symbol;
	}

	private short tag()
	{
		CoffeeSymbol tok = last(this.fTokens);
		if (tok == null)
		{
			return UNKNOWN;
		}
		return tok.getId();
	}

	private String value()
	{
		CoffeeSymbol tok = last(this.fTokens);
		if (tok == null)
		{
			return null;
		}
		return (String) tok.value;
	}

	private boolean unfinished()
	{
		if (LINE_CONTINUER.matcher(this.fChunk).find())
		{
			return true;
		}

		CoffeeSymbol value = last(this.fTokens);
		if (value == null)
		{
			return false;
		}
		// @tag() in ['\\', '.', '?.', 'UNARY', 'MATH', '+', '-', 'SHIFT', 'RELATION'
		// 'COMPARE', 'LOGIC', 'COMPOUND_ASSIGN', 'THROW', 'EXTENDS']
		switch (value.getId())
		{
			case Terminals.DOT:
			case Terminals.QUESTION_DOT:
			case Terminals.UNARY:
			case Terminals.MATH:
			case Terminals.PLUS:
			case Terminals.MINUS:
			case Terminals.SHIFT:
			case Terminals.RELATION:
			case Terminals.COMPARE:
			case Terminals.LOGIC:
			case Terminals.COMPOUND_ASSIGN:
			case Terminals.THROW:
			case Terminals.EXTENDS:
				return true;
			default:
				return "\\".equals(value.getValue());
		}
		// CoffeeSymbol prev = last(this.fTokens, 1);
		// CoffeeSymbol value = last(this.fTokens);
		// return (prev != null && prev.getId() == Terminals.DOT && value != null && !value.reserved
		// && NO_NEWLINE.matcher((String) value.getValue()).find()
		// && !CODE.matcher((String) value.getValue()).find() && !ASSIGNED.matcher(this.fChunk).find());
	}

	private String escapeLines(String string)
	{
		return escapeLines(string, false);
	}

	private String escapeLines(String str, boolean heredoc)
	{
		return MULTILINER.matcher(str).replaceAll(heredoc ? "\\n" : "");
	}

	private String makeString(String body, String quote, boolean heredoc)
	{
		if (body == null)
		{
			return quote + quote;
		}
		Pattern p = Pattern.compile("\\\\([\\s\\S])");
		Matcher m = p.matcher(body);
		StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			String match = m.group(0);
			String contents = m.group(1);
			String replacement = match;
			if (contents.equals("\n") || contents.equals(quote))
			{
				replacement = contents;
			}
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		body = sb.toString();

		body = body.replaceAll(quote, "\\$&");
		return quote + this.escapeLines(body, heredoc) + quote;
	}

	private CoffeeSymbol last(List<CoffeeSymbol> array)
	{
		return last(array, 0);
	}

	private CoffeeSymbol last(List<CoffeeSymbol> array, int back)
	{
		int index = array.size() - back - 1;
		if (index < 0 || index >= array.size())
		{
			return null;
		}
		return array.get(index);
	}

	private int count(String string, String substr)
	{
		int num = 0;
		int pos = 0;
		if (substr.length() == 0)
		{
			// return 1 / 0;
			return -1; // We return -1 since we can't return 1/0!
		}
		while ((pos = 1 + string.indexOf(substr, pos)) != 0)
		{
			num++;
		}
		return num;
	}

	public void setSource(String source)
	{
		reset();
		this.fCode = source;
	}

	private void reset()
	{
		this.fTokens = null;
		this.fIndents = null;
		this.fCode = null;
	}

	public List<CoffeeCommentNode> getComments()
	{
		return Collections.unmodifiableList(this.fComments);
	}
}
