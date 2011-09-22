/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.lexer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.aptana.editor.coffee.parsing.Terminals;

@SuppressWarnings("nls")
public class CoffeeRewriter
{

	private static Map<Short, Short> BALANCED_PAIRS = new HashMap<Short, Short>();
	static
	{
		BALANCED_PAIRS.put(Terminals.LPAREN, Terminals.RPAREN);
		BALANCED_PAIRS.put(Terminals.LBRACKET, Terminals.RBRACKET);
		BALANCED_PAIRS.put(Terminals.LCURLY, Terminals.RCURLY);
		BALANCED_PAIRS.put(Terminals.INDENT, Terminals.OUTDENT);
		BALANCED_PAIRS.put(Terminals.CALL_START, Terminals.CALL_END);
		BALANCED_PAIRS.put(Terminals.PARAM_START, Terminals.PARAM_END);
		BALANCED_PAIRS.put(Terminals.INDEX_START, Terminals.INDEX_END);
	}

	private static Map<Short, Short> INVERSES = new HashMap<Short, Short>();
	private static Set<Short> EXPRESSION_START = new HashSet<Short>();
	private static Set<Short> EXPRESSION_END = new HashSet<Short>();
	static
	{
		for (Map.Entry<Short, Short> entry : BALANCED_PAIRS.entrySet())
		{
			Short left = entry.getKey();
			Short right = entry.getValue();
			INVERSES.put(right, left);
			INVERSES.put(left, right);
			EXPRESSION_START.add(left);
			EXPRESSION_END.add(right);
		}
	}

	private static Set<Short> EXPRESSION_CLOSE = new HashSet<Short>();
	static
	{
		EXPRESSION_CLOSE.add(Terminals.CATCH);
		EXPRESSION_CLOSE.add(Terminals.WHEN);
		EXPRESSION_CLOSE.add(Terminals.ELSE);
		EXPRESSION_CLOSE.add(Terminals.FINALLY);
		EXPRESSION_CLOSE.addAll(EXPRESSION_END);
	}

	private static Set<Short> SINGLE_LINERS = new HashSet<Short>();
	static
	{
		SINGLE_LINERS.add(Terminals.ELSE);
		SINGLE_LINERS.add(Terminals.FUNC_ARROW);
		SINGLE_LINERS.add(Terminals.BOUND_FUNC_ARROW);
		SINGLE_LINERS.add(Terminals.TRY);
		SINGLE_LINERS.add(Terminals.FINALLY);
		SINGLE_LINERS.add(Terminals.THEN);
	}

	private static Set<Short> SINGLE_CLOSERS = new HashSet<Short>();
	static
	{
		SINGLE_CLOSERS.add(Terminals.TERMINATOR);
		SINGLE_CLOSERS.add(Terminals.CATCH);
		SINGLE_CLOSERS.add(Terminals.FINALLY);
		SINGLE_CLOSERS.add(Terminals.ELSE);
		SINGLE_CLOSERS.add(Terminals.OUTDENT);
		SINGLE_CLOSERS.add(Terminals.LEADING_WHEN);
	}

	private static Set<Short> IMPLICIT_FUNC = new HashSet<Short>();
	static
	{
		IMPLICIT_FUNC.add(Terminals.IDENTIFIER);
		IMPLICIT_FUNC.add(Terminals.SUPER);
		IMPLICIT_FUNC.add(Terminals.RPAREN);
		IMPLICIT_FUNC.add(Terminals.CALL_END);
		IMPLICIT_FUNC.add(Terminals.RBRACKET);
		IMPLICIT_FUNC.add(Terminals.INDEX_END);
		IMPLICIT_FUNC.add(Terminals.AT_SIGIL);
		IMPLICIT_FUNC.add(Terminals.THIS);
	}

	private static Set<Short> IMPLICIT_CALL = new HashSet<Short>();
	static
	{
		IMPLICIT_CALL.add(Terminals.IDENTIFIER);
		IMPLICIT_CALL.add(Terminals.NUMBER);
		IMPLICIT_CALL.add(Terminals.STRING);
		IMPLICIT_CALL.add(Terminals.JS);
		IMPLICIT_CALL.add(Terminals.REGEX);
		IMPLICIT_CALL.add(Terminals.NEW);
		IMPLICIT_CALL.add(Terminals.PARAM_START);
		IMPLICIT_CALL.add(Terminals.CLASS);
		IMPLICIT_CALL.add(Terminals.IF);
		IMPLICIT_CALL.add(Terminals.TRY);
		IMPLICIT_CALL.add(Terminals.SWITCH);
		IMPLICIT_CALL.add(Terminals.THIS);
		IMPLICIT_CALL.add(Terminals.BOOL);
		IMPLICIT_CALL.add(Terminals.UNARY);
		IMPLICIT_CALL.add(Terminals.SUPER);
		IMPLICIT_CALL.add(Terminals.AT_SIGIL);
		IMPLICIT_CALL.add(Terminals.FUNC_ARROW);
		IMPLICIT_CALL.add(Terminals.BOUND_FUNC_ARROW);
		IMPLICIT_CALL.add(Terminals.LBRACKET);
		IMPLICIT_CALL.add(Terminals.LPAREN);
		IMPLICIT_CALL.add(Terminals.LCURLY);
		IMPLICIT_CALL.add(Terminals.MINUS_MINUS);
		IMPLICIT_CALL.add(Terminals.PLUS_PLUS);
	}

	private static Set<Short> IMPLICIT_UNSPACED_CALL = new HashSet<Short>();
	static
	{
		IMPLICIT_UNSPACED_CALL.add(Terminals.PLUS);
		IMPLICIT_UNSPACED_CALL.add(Terminals.MINUS);
	}

	private static Set<Short> IMPLICIT_BLOCK = new HashSet<Short>();
	static
	{
		IMPLICIT_BLOCK.add(Terminals.FUNC_ARROW);
		IMPLICIT_BLOCK.add(Terminals.BOUND_FUNC_ARROW);
		IMPLICIT_BLOCK.add(Terminals.LCURLY);
		IMPLICIT_BLOCK.add(Terminals.LBRACKET);
		IMPLICIT_BLOCK.add(Terminals.COMMA);
	}

	private static Set<Short> IMPLICIT_END = new HashSet<Short>();
	static
	{
		IMPLICIT_END.add(Terminals.POST_IF);
		IMPLICIT_END.add(Terminals.FOR);
		IMPLICIT_END.add(Terminals.WHILE);
		IMPLICIT_END.add(Terminals.UNTIL);
		IMPLICIT_END.add(Terminals.WHEN);
		IMPLICIT_END.add(Terminals.BY);
		IMPLICIT_END.add(Terminals.LOOP);
		IMPLICIT_END.add(Terminals.TERMINATOR);
		IMPLICIT_END.add(Terminals.INDENT);
	}

	private static Set<Short> LINEBREAKS = new HashSet<Short>();
	static
	{
		LINEBREAKS.add(Terminals.TERMINATOR);
		LINEBREAKS.add(Terminals.INDENT);
		LINEBREAKS.add(Terminals.OUTDENT);
	}

	private static Set<Short> CHECK_FOR_IMPLICIT_INDENTATION = new HashSet<Short>();
	static
	{
		CHECK_FOR_IMPLICIT_INDENTATION.add(Terminals.OUTDENT);
		CHECK_FOR_IMPLICIT_INDENTATION.add(Terminals.TERMINATOR);
		CHECK_FOR_IMPLICIT_INDENTATION.add(Terminals.FINALLY);
	}

	private static Set<Short> IMPLICIT_PARENS_CHECK_1 = new HashSet<Short>();
	static
	{
		IMPLICIT_PARENS_CHECK_1.add(Terminals.IF);
		IMPLICIT_PARENS_CHECK_1.add(Terminals.ELSE);
		IMPLICIT_PARENS_CHECK_1.add(Terminals.FUNC_ARROW);
		IMPLICIT_PARENS_CHECK_1.add(Terminals.BOUND_FUNC_ARROW);
	}

	private static Set<Short> IMPLICIT_PARENS_CHECK_2 = new HashSet<Short>();
	static
	{
		IMPLICIT_PARENS_CHECK_2.add(Terminals.DOT);
		IMPLICIT_PARENS_CHECK_2.add(Terminals.QUESTION_DOT);
		IMPLICIT_PARENS_CHECK_2.add(Terminals.DOUBLE_COLON);
	}

	private static Set<Short> IMPLICIT_BRACES = new HashSet<Short>();
	static
	{
		IMPLICIT_BRACES.add(Terminals.IDENTIFIER);
		IMPLICIT_BRACES.add(Terminals.NUMBER);
		IMPLICIT_BRACES.add(Terminals.STRING);
		IMPLICIT_BRACES.add(Terminals.AT_SIGIL);
		IMPLICIT_BRACES.add(Terminals.TERMINATOR);
		IMPLICIT_BRACES.add(Terminals.OUTDENT);
	}

	private List<CoffeeSymbol> fTokens;
	private boolean seenSingle;

	List<CoffeeSymbol> rewrite(List<CoffeeSymbol> tokens)
	{
		this.fTokens = tokens;
		removeLeadingNewlines();
		removeMidExpressionNewlines();
		closeOpenCalls();
		closeOpenIndexes();
		addImplicitIndentation();
		tagPostfixConditionals();
		addImplicitBraces();
		addImplicitParentheses();
		ensureBalance(BALANCED_PAIRS);
		rewriteClosingParens();

		return this.fTokens;
	}

	private void removeLeadingNewlines()
	{
		// Remove leading TERMINATORs
		while (!fTokens.isEmpty())
		{
			CoffeeSymbol sym = fTokens.get(0);
			if (Terminals.TERMINATOR != sym.getId())
			{
				break;
			}
			fTokens.remove(0);
		}
	}

	private void removeMidExpressionNewlines()
	{
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol sym = this.fTokens.get(i);
			CoffeeSymbol next = null;
			if (i + 1 < this.fTokens.size())
			{
				next = this.fTokens.get(i + 1);
			}
			if (sym.getId() == Terminals.TERMINATOR && next != null && EXPRESSION_CLOSE.contains(next.getId()))
			{
				this.fTokens.remove(i);
				continue;
			}
			i++;
		}
	}

	private void closeOpenCalls()
	{
		// scanTokens
		for (int i = 0; i < this.fTokens.size(); i++)
		{
			CoffeeSymbol sym = this.fTokens.get(i);
			if (Terminals.CALL_START == sym.getId())
			{
				// detectEnd
				int levels = 0;
				for (int j = i + 1; j < this.fTokens.size(); j++)
				{
					CoffeeSymbol token = this.fTokens.get(j);
					if (levels == 0
							&& ((token.getId() == Terminals.RPAREN || token.getId() == Terminals.CALL_END) || (token
									.getId() == Terminals.OUTDENT && this.fTokens.get(j - 1).getId() == Terminals.RPAREN)))
					{
						int index = token.getId() == Terminals.OUTDENT ? j - 1 : j;
						this.fTokens.get(index).setId(Terminals.CALL_END);
						break;
					}
					if (token == null || levels < 0)
					{
						int index = token.getId() == Terminals.OUTDENT ? j - 2 : j - 1;
						this.fTokens.get(index).setId(Terminals.CALL_END);
						break;
					}
					if (EXPRESSION_START.contains(token.getId()))
					{
						levels++;
					}
					else if (EXPRESSION_END.contains(token.getId()))
					{
						levels--;
					}
				}
			}
		}
	}

	private void closeOpenIndexes()
	{
		// scanTokens
		for (int i = 0; i < this.fTokens.size(); i++)
		{
			CoffeeSymbol sym = this.fTokens.get(i);
			if (Terminals.INDEX_START == sym.getId())
			{
				// detectEnd
				int levels = 0;
				for (int j = i + 1; j < this.fTokens.size(); j++)
				{
					CoffeeSymbol token = this.fTokens.get(j);
					if (levels == 0 && (token.getId() == Terminals.RBRACKET || token.getId() == Terminals.INDEX_END))
					{
						token.setId(Terminals.INDEX_END);
						break;
					}
					if (token == null || levels < 0)
					{
						token.setId(Terminals.INDEX_END);
						break;
					}
					if (EXPRESSION_START.contains(token.getId()))
					{
						levels++;
					}
					else if (EXPRESSION_END.contains(token.getId()))
					{
						levels--;
					}
				}
			}
		}
	}

	private void addImplicitBraces()
	{
		Stack<CoffeeSymbol> stack = new Stack<CoffeeSymbol>();
		// scanTokens
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol token = this.fTokens.get(i);
			if (EXPRESSION_START.contains(token.getId()))
			{
				short id;
				if (Terminals.INDENT == token.getId() && Terminals.LCURLY == this.fTokens.get(i - 1).getId())
				{
					id = Terminals.LCURLY;
				}
				else
				{
					id = token.getId();
				}

				stack.add(new CoffeeSymbol(id, i));
				i++;
				continue;
			}
			if (EXPRESSION_END.contains(token.getId()))
			{
				stack.pop();
				i++;
				continue;
			}

			short endOfStack = -1;
			if (!stack.isEmpty())
			{
				endOfStack = stack.get(stack.size() - 1).getId();
			}
			if (!(Terminals.COLON == token.getId() && ((i >= 2 && Terminals.COLON == this.fTokens.get(i - 2).getId()) || (Terminals.LCURLY != endOfStack))))
			{
				i++;
				continue;
			}
			stack.push(new CoffeeSymbol(Terminals.LCURLY, "{"));

			int idx = ((i >= 2 && Terminals.AT_SIGIL == this.fTokens.get(i - 2).getId()) ? i - 2 : i - 1);
			while (idx >= 2 && this.fTokens.get(idx - 2).getId() == Terminals.HERECOMMENT)
			{
				idx -= 2;
			}
			// Grab the end of the token before this implicit curly, and use that as our offset
			int offsetToUse = 0;
			if (idx >= 1)
			{
				CoffeeSymbol replacing = this.fTokens.get(idx - 1);
				offsetToUse = replacing.getEnd();
			}
			CoffeeSymbol tok = new CoffeeSymbol(Terminals.LCURLY, offsetToUse, offsetToUse, "{");
			tok.generated = true;
			this.fTokens.add(idx, tok);

			// detectEnd
			int levels = 0;
			for (int j = i + 2; j < this.fTokens.size(); j++)
			{
				CoffeeSymbol innerToken = this.fTokens.get(j);
				if (levels == 0 && addImplicitBrace(innerToken, j))
				{
					CoffeeSymbol toAdd = new CoffeeSymbol(Terminals.RCURLY, innerToken.getStart(),
							innerToken.getStart(), "}");
					toAdd.generated = true;
					this.fTokens.add(j, toAdd);
					break;
				}
				if (innerToken == null || levels < 0)
				{
					CoffeeSymbol toAdd = new CoffeeSymbol(Terminals.RCURLY, innerToken.getStart(),
							innerToken.getStart(), "}");
					toAdd.generated = true;
					this.fTokens.add(j, toAdd);
					break;
				}
				if (EXPRESSION_START.contains(innerToken.getId()))
				{
					levels++;
				}
				else if (EXPRESSION_END.contains(innerToken.getId()))
				{
					levels--;
				}
			}
			i += 2;
		}
	}

	private boolean addImplicitBrace(CoffeeSymbol token, int i)
	{
		Short oneId = -1;
		if (i + 1 < this.fTokens.size())
		{
			oneId = this.fTokens.get(i + 1).getId();
		}
		if (Terminals.HERECOMMENT == oneId)
		{
			return false;
		}
		short tag = token.getId();
		Short twoId = -2;
		if (i + 2 < this.fTokens.size())
		{
			twoId = this.fTokens.get(i + 2).getId();
		}
		Short threeId = -3;
		if (i + 3 < this.fTokens.size())
		{
			threeId = this.fTokens.get(i + 3).getId();
		}
		// @formatter:off
		return (
				(Terminals.TERMINATOR == tag || Terminals.OUTDENT == tag) &&
				!(Terminals.COLON == twoId || Terminals.AT_SIGIL == oneId && Terminals.COLON == threeId)
				)
				||
				(Terminals.COMMA == tag && !IMPLICIT_BRACES.contains(oneId));
		// @formatter:on
	}

	private List<CoffeeSymbol> indentation(CoffeeSymbol token)
	{
		List<CoffeeSymbol> symbols = new ArrayList<CoffeeSymbol>();
		symbols.add(new CoffeeSymbol(Terminals.INDENT, token.getEnd(), token.getEnd(), 2));
		symbols.add(new CoffeeSymbol(Terminals.OUTDENT, token.getEnd(), token.getEnd(), 2));
		return symbols;
	}

	private void addImplicitIndentation()
	{
		// scanTokens
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol token = this.fTokens.get(i);
			short tag = token.getId();
			if (Terminals.TERMINATOR == tag && i + 1 < this.fTokens.size()
					&& Terminals.THEN == this.fTokens.get(i + 1).getId())
			{
				this.fTokens.remove(i);
				continue;
			}
			if (Terminals.ELSE == tag && Terminals.OUTDENT != this.fTokens.get(i - 1).getId())
			{
				this.fTokens.addAll(i, indentation(token));
				i += 2;
				continue;
			}

			if (Terminals.CATCH == tag && CHECK_FOR_IMPLICIT_INDENTATION.contains(this.fTokens.get(i + 2).getId()))
			{
				this.fTokens.addAll(i + 2, indentation(token));
				i += 4;
				continue;
			}

			if (SINGLE_LINERS.contains(tag) && Terminals.INDENT != this.fTokens.get(i + 1).getId()
					&& (!(Terminals.ELSE == tag && Terminals.IF == this.fTokens.get(i + 1).getId())))
			{
				short starter = tag;
				List<CoffeeSymbol> indents = indentation(token);
				CoffeeSymbol indent = indents.get(0);
				CoffeeSymbol outdent = indents.get(1);
				indent.fromThen = (Terminals.THEN == starter);
				indent.generated = true;
				outdent.generated = true;
				this.fTokens.add(i + 1, indent);
				// detectEnd
				int levels = 0;
				for (int j = i + 2; j < this.fTokens.size(); j++)
				{
					CoffeeSymbol innerToken = this.fTokens.get(j);
					if (levels == 0 && addImplicitIndent(innerToken, starter, j))
					{
						int index = j;
						if (Terminals.COMMA == this.fTokens.get(j - 1).getId())
						{
							index = j - 1;
						}
						// Fix the outdent offsets!
						outdent.setLocation(innerToken.getEnd(), innerToken.getEnd());
						this.fTokens.add(index, outdent);
						break;
					}
					if (innerToken == null || levels < 0)
					{
						int index = j;
						if (Terminals.COMMA == this.fTokens.get(j - 1).getId())
						{
							index = j - 1;
						}
						// Fix the outdent offsets!
						outdent.setLocation(innerToken.getEnd(), innerToken.getEnd());
						this.fTokens.add(index, outdent);
						break;
					}
					if (EXPRESSION_START.contains(innerToken.getId()))
					{
						levels++;
					}
					else if (EXPRESSION_END.contains(innerToken.getId()))
					{
						levels--;
					}
				}

				if (Terminals.THEN == tag)
				{
					this.fTokens.remove(i);
				}
				i++;
				continue;
			}
			i++;
		}
	}

	private boolean addImplicitIndent(CoffeeSymbol token, short starter, int i)
	{
		Set<Short> toCheck = new HashSet<Short>();
		toCheck.add(Terminals.IF);
		toCheck.add(Terminals.THEN);
		return (!";".equals(token.getValue()) && SINGLE_CLOSERS.contains(token.getId()) && !(Terminals.ELSE == token
				.getId() && !toCheck.contains(starter)));
	}

	private void addImplicitParentheses()
	{
		boolean noCall = false;
		// scanTokens
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol token = this.fTokens.get(i);
			short tag = token.getId();
			if (Terminals.CLASS == tag || Terminals.IF == tag)
			{
				noCall = true;
			}
			CoffeeSymbol prev = null;
			if (i - 1 >= 0)
			{
				prev = this.fTokens.get(i - 1);
			}
			CoffeeSymbol next = null;
			if (i + 1 < this.fTokens.size())
			{
				next = this.fTokens.get(i + 1);
			}
			boolean callObject = (!noCall && Terminals.INDENT == tag && next != null && next.generated
					&& Terminals.LCURLY == next.getId() && prev != null && IMPLICIT_FUNC.contains(prev.getId()));
			this.seenSingle = false;
			if (LINEBREAKS.contains(tag))
			{
				noCall = false;
			}
			if (prev != null && !prev.spaced && Terminals.QUESTION == tag)
			{
				token.call = true;
			}
			if (token.fromThen)
			{
				i++;
				continue;
			}

			if (!(callObject || (prev != null ? prev.spaced : false)
					&& (prev.call || IMPLICIT_FUNC.contains(prev.getId()))
					&& (IMPLICIT_CALL.contains(tag) || !(token.spaced || token.newLine)
							&& IMPLICIT_UNSPACED_CALL.contains(tag))))
			{

				i++;
				continue;
			}

			this.fTokens.add(i, new CoffeeSymbol(Terminals.CALL_START, token.getStart(), token.getStart(), "("));

			// detectEnd
			int levels = 0;
			for (int j = i + 1; j < this.fTokens.size(); j++)
			{
				CoffeeSymbol innerToken = this.fTokens.get(j);

				if (levels == 0 && addImplicitParens(innerToken, j))
				{
					int idx = (Terminals.OUTDENT == innerToken.getId() ? j + 1 : j);
					this.fTokens.add(idx, new CoffeeSymbol(Terminals.CALL_END, innerToken.getEnd(),
							innerToken.getEnd(), ")"));
					break;
				}
				if (innerToken == null || levels < 0)
				{
					int idx = (Terminals.OUTDENT == innerToken.getId() ? j + 1 : j);
					this.fTokens.add(idx, new CoffeeSymbol(Terminals.CALL_END, innerToken.getEnd(),
							innerToken.getEnd(), ")"));
					break;
				}
				if (EXPRESSION_START.contains(innerToken.getId()))
				{
					levels++;
				}
				else if (EXPRESSION_END.contains(innerToken.getId()))
				{
					levels--;
				}
			}

			if (Terminals.QUESTION == prev.getId())
			{
				prev.setId(Terminals.FUNC_EXIST);
			}
			i += 2;
		}
	}

	private boolean addImplicitParens(CoffeeSymbol token, int i)
	{
		if (!seenSingle && token.fromThen)
		{
			return true;
		}
		short tag = token.getId();
		if (IMPLICIT_PARENS_CHECK_1.contains(tag))
		{
			seenSingle = true;
		}

		CoffeeSymbol prev = null;
		if (i - 1 >= 0)
		{
			prev = this.fTokens.get(i - 1);
		}

		if (IMPLICIT_PARENS_CHECK_2.contains(tag) && prev != null && Terminals.OUTDENT == prev.getId())
		{
			return true;
		}
		CoffeeSymbol post = null;
		if (i + 1 < this.fTokens.size())
		{
			post = this.fTokens.get(i + 1);
		}

		return (!token.generated && Terminals.COMMA != prev.getId() && IMPLICIT_END.contains(tag) && (Terminals.INDENT != tag || (Terminals.CLASS != this.fTokens
				.get(i - 2).getId() && !IMPLICIT_BLOCK.contains(prev.getId()) && !(post != null && post.generated && Terminals.LCURLY == post
				.getId()))));
	}

	private void tagPostfixConditionals()
	{
		// scanTokens
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol token = this.fTokens.get(i);
			if (Terminals.IF != token.getId())
			{
				i++;
				continue;
			}

			// detectEnd
			int levels = 0;
			for (int j = i + 1; j < this.fTokens.size(); j++)
			{
				CoffeeSymbol innerToken = this.fTokens.get(j);

				if (levels == 0
						&& (Terminals.TERMINATOR == innerToken.getId() || Terminals.INDENT == innerToken.getId()))
				{
					if (Terminals.INDENT != innerToken.getId())
					{
						token.setId(Terminals.POST_IF);
					}
					break;
				}
				if (innerToken == null || levels < 0)
				{
					if (Terminals.INDENT != innerToken.getId())
					{
						token.setId(Terminals.POST_IF);
					}
					break;
				}
				if (EXPRESSION_START.contains(innerToken.getId()))
				{
					levels++;
				}
				else if (EXPRESSION_END.contains(innerToken.getId()))
				{
					levels--;
				}
			}
			i++;
		}
	}

	private void ensureBalance(Map<Short, Short> pairs)
	{
		Map<Short, Integer> levels = new HashMap<Short, Integer>();
		Map<Short, Integer> openLine = new HashMap<Short, Integer>();
		for (CoffeeSymbol token : fTokens)
		{
			for (Map.Entry<Short, Short> pair : pairs.entrySet())
			{
				short open = pair.getKey();
				short close = pair.getValue();
				if (!levels.containsKey(open))
				{
					levels.put(open, 0);
				}
				if (open == token.getId())
				{
					int level = levels.get(open);
					if (level == 0)
					{
						// FIXME Put line as value, not offset!
						openLine.put(open, token.getStart());
					}
					level++;
					levels.put(open, level);
				}
				else if (close == token.getId())
				{
					int level = levels.get(open);
					level--;
					levels.put(open, level);
					if (level < 0)
					{
						// FIXME When we store lines, spit out line number here, not offset
						throw new IllegalStateException(MessageFormat.format("too many {0} at offset {1}",
								getTerminalNameForShort(close), token.getStart()));
					}
				}
			}
		}

		for (Map.Entry<Short, Integer> entry : levels.entrySet())
		{
			Integer level = entry.getValue();
			if (level > 0)
			{
				Short open = entry.getKey();
				throw new IllegalStateException(MessageFormat.format("unclosed {0} at offset {1}",
						getTerminalNameForShort(open), openLine.get(open)));
			}
		}
	}

	private String getTerminalNameForShort(Short id)
	{
		return Terminals.getNameForValue(id);
	}

	private void rewriteClosingParens()
	{
		Stack<CoffeeSymbol> stack = new Stack<CoffeeSymbol>();
		Map<Short, Integer> debt = new HashMap<Short, Integer>();
		for (Short key : INVERSES.keySet())
		{
			debt.put(key, 0);
		}
		// scanTokens
		for (int i = 0; i < this.fTokens.size();)
		{
			CoffeeSymbol token = this.fTokens.get(i);
			short tag = token.getId();
			if (EXPRESSION_START.contains(tag))
			{
				stack.push(token);
				i++;
				continue;
			}
			if (!EXPRESSION_END.contains(tag))
			{
				i++;
				continue;
			}
			Short inv = INVERSES.get(tag);
			int invValue = debt.get(inv);
			if (invValue > 0)
			{
				invValue--;
				debt.put(inv, invValue);
				this.fTokens.remove(i);
				continue;
			}
			CoffeeSymbol match = stack.pop();
			short mtag = match.getId();
			short oppos = INVERSES.get(mtag);
			if (tag == oppos)
			{
				i++;
				continue;
			}

			int mtagValue = debt.get(mtag);
			mtagValue++;
			debt.put(mtag, mtagValue);

			// Use start offset of next token as our start and end offset
			CoffeeSymbol val = new CoffeeSymbol(oppos, token.getStart(), token.getStart(),
					Terminals.INDENT == mtag ? match.getValue() : Terminals.getValue(oppos));

			if (mtag == this.fTokens.get(i + 2).getId())
			{
				this.fTokens.add(i + 3, val);
				stack.push(match);
			}
			else
			{
				this.fTokens.add(i, val);
			}
			i++;
		}
	}
}
