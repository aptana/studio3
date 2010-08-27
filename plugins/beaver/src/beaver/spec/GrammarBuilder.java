/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003-2009 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import beaver.Symbol;
import beaver.comp.util.Log;
import beaver.spec.Production.RHS;
import beaver.spec.ast.Declaration;
import beaver.spec.ast.GrammarTreeRoot;
import beaver.spec.ast.Rule;
import beaver.spec.ast.TreeWalker;

/**
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GrammarBuilder extends TreeWalker
{
	/**
	 * Specialization that visits only Rules
	 */
	static class RuleWalker extends TreeWalker
	{
		public void visit(GrammarTreeRoot node)
		{
			for (int i = 0; i < node.rules.length; i++)
			{
				node.rules[i].accept(this);
			}
		}
	}
	
	/**
	 * Specialization that visits only Declarations
	 */
	static class DeclarationWalker extends TreeWalker
	{
		public void visit(GrammarTreeRoot node)
		{
			for (int i = 0; i < node.declarations.length; i++)
			{
				node.declarations[i].accept(this);
			}
		}
	}
	
	/**
	 * Checks whether braces in Java code are balanced, to make sure that a
	 * generated parser will be compilable.
	 * 
	 * @param code Java code where braces are checked 
	 * @return true if braces are balanced
	 */
	static private boolean checkBraces(String code)
	{
		boolean ovr = false;
		int n = 0, len = code.length();
		for (int i = 0; i < len; i++)
		{
			char c = code.charAt(i);
			if (c == '{')
				n++;
			else if (c == '}')
				n--;
			if (n < 0)
			{
				ovr = true;
			}
		}
		return !ovr && n == 0;
	}
	
	static private String trimCode(String code)
	{
		if (code != null)
		{
    	    int i = code.length();
			do { --i; } while (i >= 0 && Character.isWhitespace(code.charAt(i)));
			code = code.substring(0, i + 1);
		}
		return code;
	}

	/** "Destination" to report our progress */
	private Log log;
	
	/** The "thing" we build here. */
	private Grammar grammar;

	/** Maps symbol names to grammar symbols */
	private HashMap symbols;
	
	/** Number of non-terminal symbols defined by the grammar */
	private int n_nonterms;
	
	/** Number of terminals in the grammar */
	private int n_terms;
	
	/** Number of production rules in the grammar */
	private int n_rules;
	
	public GrammarBuilder(Log log)
	{
		this.log = log;
	}
	
	public Grammar getGrammar()
	{
		return grammar;
	}
	
	public void visit(GrammarTreeRoot root)
	{
		grammar = new Grammar();

		symbols = new HashMap(89);
		symbols.put(grammar.error.name, grammar.error);
		
		n_nonterms = 1; // error (above)
		n_terms = 1; // implicit EOF
		n_rules = 0;

		final HashMap tokens = new HashMap(89);
        final ArrayList goals = new ArrayList();
		
		root.accept(new TreeWalker() /* collect terminals, nonterminals and "virtual" symbols */
		{
			public void visit(Declaration.Terminals decl)
			{
				for (int i = 0; i < decl.symbols.length; i++)
				{
					String sym_name = (String) decl.symbols[i].value;
					if (!symbols.containsKey(sym_name))
					{
						symbols.put(sym_name, new Terminal(sym_name));
						n_terms++;
						tokens.put(sym_name, decl.symbols[i]);
					}
				}
			}
			public void visit(Rule rule)
			{
				String lhs_sym_name = rule.getLHSSymbolName();
				if (!symbols.containsKey(lhs_sym_name))
				{
					symbols.put(lhs_sym_name, new NonTerminal(lhs_sym_name));
					n_nonterms++;
				}
				else if (tokens.containsKey(lhs_sym_name))
				{
					log.error(rule.lhs_sym, "nonterminal was declared as a terminal");
					symbols.put(lhs_sym_name, new NonTerminal(lhs_sym_name));
					n_nonterms++;
					tokens.remove(lhs_sym_name);
					n_terms--;
				}
				super.visit(rule);
			}
			public void visit(Rule.Definition rhs)
			{
				String prec_sym_name = rhs.getPrecedenceSymbolName();
				if (prec_sym_name != null && !symbols.containsKey(prec_sym_name))
				{
					GrammarSymbol sym = new Terminal(prec_sym_name);
					sym.id = -1; // "virtual"
					symbols.put(prec_sym_name, sym);
				}
				n_rules++;
			}
		});
		root.accept(new RuleWalker() /* "collect" undefined symbols */
		{
			public void visit(Rule.Definition.Element rhs_item)
			{
				String rhs_sym_name = rhs_item.getName();
				GrammarSymbol rhs_sym = (GrammarSymbol) symbols.get(rhs_sym_name); 
				if (rhs_sym == null)
				{
					log.error(rhs_item.sym_name, "symbol is neither a terminal nor a nonterminal of the grammar");
					symbols.put(rhs_sym_name, rhs_sym = new Terminal(rhs_sym_name));
					rhs_sym.id = -1; // make it a "virtual" terminal
				}
				else if (rhs_sym instanceof Terminal)
				{
					if (rhs_sym.id < 0)
						log.error(rhs_item.sym_name, "symbol is not declared as a grammar terminal");
					else
						tokens.remove(rhs_sym_name);
				}
			}
		});
		for (Iterator i = tokens.values().iterator(); i.hasNext();)
		{
			Symbol token = (Symbol) i.next();
			log.warning(token, "declared terminal is not used by the grammar");
			symbols.remove(token.value);
			n_terms--;
		}
		root.accept(new DeclarationWalker()
		{
			/** Next unused highest precedence */
			private int precedence = Integer.MAX_VALUE;
			private HashSet imports = new HashSet(23);
			private List<String> impls = new LinkedList<String>();
			
			public void visit(GrammarTreeRoot root)
			{
				imports.add(Grammar.EBNF_LIST_TYPE);
				imports.add("beaver.*");
				super.visit(root);
				grammar.imports = (String[]) imports.toArray(new String[imports.size()]);
			}
			public void visit(Declaration.Header decl)
			{
				if (grammar.prolog == null)
				{
					String text = (String) decl.code.value;
					int i = 0;
					char c;
					while (Character.isWhitespace(c = text.charAt(i)) || c == '\n') i++;
					
					grammar.prolog = i > 0 ? text.substring(i) : text;
				}
				else
				{
					grammar.prolog += (String) decl.code.value;
				}
			}
			public void visit(Declaration.PackageName decl)
			{
				if (grammar.package_name != null)
				{
					log.warning(decl.name, "Parser package has been already defined as \"" + grammar.package_name + "\", new name ignored.");
				}
				else
				{
					grammar.package_name = decl.getName();
				}
			}
			public void visit(Declaration.Implements decl)
			{
				for (int i = 0; i < decl.symbols.length; i++)
				{
					impls.add((String) decl.symbols[i].value);
				}
				grammar.impls = impls.toArray(new String[impls.size()]);
			}
			public void visit(Declaration.Imports decl)
			{
				for (int i = 0; i < decl.symbols.length; i++)
				{
					imports.add(decl.symbols[i].value);
				}
			}
			public void visit(Declaration.ClassName decl)
			{
				if (grammar.class_name != null)
				{
					log.warning(decl.name, "Parser class name has been already defined as \"" + grammar.class_name + "\", new name ignored.");
				}
				else
				{
					grammar.class_name = decl.getName();
				}
			}
			public void visit(Declaration.ClassCode decl)
			{
				if (grammar.class_code != null)
				{
					log.warning(decl.code, "Embedded parser class code has been already defined, new code ignored.");
				}
				else
				{
					grammar.class_code = trimCode(getCode(decl));
				}
			}
			public void visit(Declaration.ConstructorCode decl)
			{
				if (grammar.init_code != null)
				{
					log.warning(decl.code, "Parser initialization code has been already defined, new code ignored.");
				}
				else
				{
					grammar.init_code = trimCode(getCode(decl));
				}
			}
			public void visit(Declaration.Goal decl)
			{
				String sym_name = decl.getName();
				GrammarSymbol sym = (GrammarSymbol) symbols.get(sym_name);
				if (sym == null)
				{
					log.error(decl.name, "Symbol is undefined");
				}
				else if (sym instanceof Terminal)
				{
					log.error(decl.name, "Symbol is a terminal");
				}
				else
				{
					goals.add(sym);
				}
			}
			public void visit(Declaration.TypeOf decl)
			{
				String type = decl.getTypeName();
				for (int i = 0; i < decl.symbols.length; i++)
				{
					GrammarSymbol sym = (GrammarSymbol) symbols.get(decl.symbols[i].value);
					if (sym == null)
					{
						log.error(decl.symbols[i], "Symbol is undefined");
					}
					else if (sym.type != null)
					{
						log.error(decl.symbols[i], "Symbol's Java type is already set to \"" + sym.type + "\"");
					}
					else
					{
						sym.type = type;
					}
				}
			}
			public void visit(Declaration.LeftAssoc decl)
			{
				setPrecedence(decl, Terminal.Associativity.LEFT);
			}
			public void visit(Declaration.RightAssoc decl)
			{
				setPrecedence(decl, Terminal.Associativity.RIGHT);
			}
			public void visit(Declaration.NonAssoc decl)
			{
				setPrecedence(decl, Terminal.Associativity.NONE);
			}
			
			private void setPrecedence(Declaration.SymbolsContainer decl, Terminal.Associativity type)
			{
				for (int i = 0; i < decl.symbols.length; i++)
				{
					String sym_name = (String) decl.symbols[i].value;
					GrammarSymbol sym = (GrammarSymbol) symbols.get(sym_name);
					if (sym == null)
					{
						log.warning(decl.symbols[i], "Symbol is not used by the grammar");
					}
					else if (sym instanceof NonTerminal)
					{
						log.error(decl.symbols[i], "Symbol is a non-terminal.");
					}
					else
					{
						((Terminal) sym).setPrecedence(precedence, type);
					}
				}
				precedence--;
			}
			
			private String getCode(Declaration.CodeContainer decl)
			{
				String code = decl.getCode();
				if (!checkBraces(code))
				{
					log.warning(decl, "Java code has unbalanced braces");
				}
				return code;
			}
		});

        final ArrayList rules = new ArrayList(n_rules * 2); // twice the size to provide space for synthetic rules
        
        if (goals.isEmpty())
        {
            log.warning(root.rules[0].lhs_sym, "Grammar has not declared any goals, will use first declared nonterminal");
            grammar.goal_symbol = (NonTerminal) symbols.get(root.rules[0].getLHSSymbolName());
        }
        else if (goals.size() == 1) // conventional path
        {
            grammar.goal_symbol = (NonTerminal) goals.get(0);
        }
        else // parser needs a synthetic goal
        {
            NonTerminal[] alts = (NonTerminal[]) goals.toArray(new NonTerminal[goals.size()]);
            
            grammar.goal_symbol = new NonTerminal("$goal");
            symbols.put(grammar.goal_symbol.name, grammar.goal_symbol);
            n_nonterms++;

            rules.add(new Production(rules.size(), grammar.goal_symbol, new Production.RHS(alts[0])));
            
            for (int i = 1; i < alts.length; i++)
            {
                Terminal term = new Terminal("$" + alts[i].name);
                symbols.put(term.name, term);
                n_terms++;
                rules.add(new Production(rules.size(), grammar.goal_symbol, new Production.RHS(term, alts[i])));
            }
        }

		root.accept(new RuleWalker() /* grammar's goal cannot be used in any RHS */
		{
			boolean found = false;
			
			public void visit(GrammarTreeRoot root)
			{
				super.visit(root);
				if (found)
				{
					NonTerminal new_goal_sym = new NonTerminal("$goal");					
					new_goal_sym.type = grammar.goal_symbol.type;
					symbols.put(new_goal_sym.name, new_goal_sym);
					n_nonterms++;

					rules.add(new Production(rules.size(), new_goal_sym, new Production.RHS(grammar.goal_symbol)));

					grammar.goal_symbol = new_goal_sym; 
				}
			}
			
			public void visit(Rule.Definition.Element rhs_item)
			{
				if (!found)
				{
					found = grammar.goal_symbol.name.equals(rhs_item.getName());
				}
			}
		});
		root.accept(new RuleWalker() /* build production rules */
		{
			private NonTerminal lhs_sym;
			private ArrayList rhs_elements = new ArrayList();
			
			public void visit(Rule rule)
			{
				lhs_sym = (NonTerminal) symbols.get(rule.getLHSSymbolName());
				super.visit(rule);
			}
			
			public void visit(Rule.Definition rhs)
			{
				rhs_elements.clear();
				
				super.visit(rhs);

				Production rule = new Production(rules.size(),
				                                 lhs_sym,
												 new Production.RHS((Production.RHS.Item[]) rhs_elements.toArray(new Production.RHS.Item[rhs_elements.size()])),
												 rhs.getPrecedenceSymbolName() == null ? null : (Terminal) symbols.get(rhs.getPrecedenceSymbolName()));
				String code = rhs.getReduceActionCode();
				if (code != null)
				{
					if (!checkBraces(code))
					{
						log.warning(rhs.code, "Java code has unbalanced braces");
					}
					rule.code = trimCode(code);
				}
				if (rhs.elements.length > 0)
				{
					rule.start_pos = rhs.elements[0].getStart();
					rule.end_pos = rhs.elements[rhs.elements.length - 1].getEnd();
				}
				rules.add(rule);
			}
			
			public void visit(Rule.Definition.Element rhs_item)
			{
				GrammarSymbol rhs_sym = rhs_item.ebnf_sym.value == null ? (GrammarSymbol) symbols.get(rhs_item.getName()) : getExtendedSymbol(rhs_item);
					
				rhs_elements.add(new Production.RHS.Item(rhs_sym, rhs_item.getAlias()));
			}
			
			private GrammarSymbol getExtendedSymbol(Rule.Definition.Element rhs_item)
			{
				switch (rhs_item.getExtUseMark())
				{
					case '?': return getOpt(rhs_item.getName());
					case '+': return getLst(rhs_item.getName());
					case '*': return getOpt(getLst(rhs_item.getName()).name);
				}
				throw new IllegalArgumentException("unrecognized extended symbol notation");
			}
			
			private NonTerminal getOpt(String sym_name)
			{
				String opt_sym_name = "opt$" + sym_name;
				NonTerminal opt_sym = (NonTerminal) symbols.get(opt_sym_name);
				if (opt_sym == null)
				{
					GrammarSymbol item_sym = (GrammarSymbol) symbols.get(sym_name);
					symbols.put(opt_sym_name, opt_sym = new NonTerminal(opt_sym_name, item_sym.type));
					n_nonterms++;
					rules.add(new Production(rules.size(), opt_sym, new Production.RHS()));
					rules.add(new Production(rules.size(), opt_sym, new Production.RHS(item_sym)));
				}
				return opt_sym;
			}
			
			private NonTerminal getLst(String sym_name)
			{
				String lst_sym_name = "lst$" + sym_name;
				NonTerminal lst_sym = (NonTerminal) symbols.get(lst_sym_name);
				if (lst_sym == null)
				{
					GrammarSymbol item_sym = (GrammarSymbol) symbols.get(sym_name); 
					symbols.put(lst_sym_name, lst_sym = new NonTerminal(lst_sym_name, item_sym.type != null ? "+" + item_sym.type : null));
					n_nonterms++;

					rules.add(new Production(rules.size(), lst_sym, new Production.RHS(item_sym)));
					rules.add(new Production(rules.size(), lst_sym, new Production.RHS(lst_sym, item_sym)));
				}
				return lst_sym;
			}
		});
		
		grammar.rules = (Production[]) rules.toArray(new Production[rules.size()]);
		grammar.nonterminals = getNonTerminals();
		grammar.terminals = getTerminals();

        propagateTypes(grammar.nonterminals);
		writeListsCode(grammar.nonterminals);
	}
	
	private Terminal[] getTerminals()
	{
		Production[] rules = new Production[grammar.rules.length];
		System.arraycopy(grammar.rules, 0, rules, 0, rules.length);
		Arrays.sort(rules, Production.NUM_TERM_CMP);
		
		Terminal[] terms = new Terminal[n_terms];
		terms[0] = grammar.eof;
		int n = 1;
		for (int i = 0; i < rules.length; i++)
		{
			RHS rhs = rules[i].rhs;
			if (rhs.n_term > 0)
			{
				for (int j = 0; j < rhs.items.length; j++)
				{
					GrammarSymbol sym = rhs.items[j].symbol;
					if (sym instanceof Terminal && sym.id == 0)
					{
						Terminal term = (Terminal) sym;
						term.id = (short) n;
						terms[n++] = term;
					}
				}
			}
		}
		if (n < n_terms) 
			throw new IllegalStateException("found less terminals than previously counted");
		
		return terms;
	}
	
	private NonTerminal[] getNonTerminals()
	{
		Production[] rules = new Production[grammar.rules.length];
		System.arraycopy(grammar.rules, 0, rules, 0, rules.length);
		Arrays.sort(rules, Production.NUM_NONTERM_CMP);
		
		NonTerminal[] nts = new NonTerminal[n_nonterms];
		int n = 0;
		for (int i = 0; i < rules.length; i++)
		{
			RHS rhs = rules[i].rhs;
			if (rhs.n_nonterm > 0)
			{
				for (int j = 0; j < rhs.items.length; j++)
				{
					GrammarSymbol sym = rhs.items[j].symbol;
					if (sym instanceof NonTerminal && sym.id == 0)
					{
						NonTerminal nt = (NonTerminal) sym;
						nt.id = (short) (n + n_terms);
						nts[n++] = nt;
					}
				}
			}
		}
		grammar.goal_symbol.id = (short) (n + n_terms);
		nts[n++] = grammar.goal_symbol;
		if (grammar.error.id == 0)
		{
			grammar.error.id = (short) (n + n_terms);
			nts[n++] = grammar.error;
		}
		if (n < n_nonterms)
			throw new IllegalStateException("found less nonterminals than previously counted");

		return nts;
	}
    
    private void propagateTypes(NonTerminal[] nts)
    {
        boolean more_found;
        do {
            more_found = false;
            
            for (int i = 0; i < nts.length; i++)
            {
                if (nts[i].type != null)
                    continue;
                
                if (nts[i].definitions.size() != 2)
                {
                    if (nts[i].definitions.size() == 1)
                    {
                        Production rule = nts[i].definitions.start();
                        if (rule.code == null && rule.rhs.size() == 1)
                        {
                            GrammarSymbol item = rule.rhs.start().symbol;
                            if (item.type != null)
                            {
                                nts[i].type = item.type; 
                                more_found = true;
                            }
                        }
                    }
                    continue;
                }
                
                Production elem_rule = nts[i].definitions.start();
                if (elem_rule.rhs.size() != 1)
                {
                    if ((elem_rule = elem_rule.next_definition).rhs.size() != 1)
                        continue;
                }
                if (elem_rule.code != null)
                    continue;

                GrammarSymbol elem = elem_rule.rhs.start().symbol;
                if (elem.type == null)
                    continue;
                
                Production next_rule = elem_rule.next_definition != null ? elem_rule.next_definition : nts[i].definitions.start();
                if (next_rule.code != null)
                    continue;

                if (next_rule.rhs.size() == 0)
                {
                    nts[i].type = elem.type;
                    more_found = true;
                }
                else if (next_rule.rhs.size() >= 2 &&  next_rule.rhs.start().symbol == nts[i] && next_rule.rhs.end().symbol == elem)
                {
                    nts[i].type = "+" + elem.type;
                    more_found = true;
                }
            }            
        }
        while (more_found);
    }
	
	private void writeListsCode(NonTerminal[] nts)
	{
		for (int i = 0; i < nts.length; i++)
		{
			if (nts[i].definitions.size() != 2)
				continue;
			
			Production new_list_rule = nts[i].definitions.start();
			if (new_list_rule.rhs.size() != 1)
			{
				if ((new_list_rule = new_list_rule.next_definition).rhs.size() != 1)
					continue;
			}
			if (new_list_rule.code != null)
				continue;
			GrammarSymbol elem = new_list_rule.rhs.start().symbol;
			
			Production add_elem_rule = nts[i].definitions.start();
			if (add_elem_rule.rhs.size() < 2)
			{
				if ((add_elem_rule = add_elem_rule.next_definition).rhs.size() < 2)
					continue;
			}
			if (add_elem_rule.code != null)
				continue;
			if (add_elem_rule.rhs.start().symbol != nts[i] || add_elem_rule.rhs.end().symbol != elem)
				continue;
			
			if (nts[i].type == null)
			{
				nts[i].type = "+" + (elem.type != null ? elem.type : "beaver.Symbol");
			}
			new_list_rule.code = new StringBuffer(Grammar.EBNF_LIST_TYPE_NAME.length() * 2 + 77)
				.append(Grammar.EBNF_LIST_TYPE_NAME).append(" lst = new ").append(Grammar.EBNF_LIST_TYPE_NAME).append("(); ")
				.append("lst.add(_symbols[offset + 1]").append(elem.type != null ? ".value" : "").append("); ")
				.append("return new Symbol(lst);")
				.toString();
			add_elem_rule.code = new StringBuffer(Grammar.EBNF_LIST_TYPE_NAME.length() + 88)
			.append("((").append(Grammar.EBNF_LIST_TYPE_NAME).append(") _symbols[offset + 1].value).add(_symbols[offset + ").append(add_elem_rule.rhs.size()).append("]").append(elem.type != null ? ".value" : "").append("); ")
			.append("return _symbols[offset + 1];")
			.toString();
		}
	}
}
