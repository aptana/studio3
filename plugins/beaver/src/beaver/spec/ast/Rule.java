/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec.ast;

import beaver.Symbol;

public class Rule extends Node
{
	static public class Definition extends Node
	{
		static public class Element extends Node
		{
			public final Symbol sym_name;
			public final Symbol alias;
			public final Symbol ebnf_sym;
			
			public Element(Symbol sym_name, Symbol alias, Symbol ebnf_sym)
			{
				this.sym_name = sym_name;
				this.alias = alias;
				this.ebnf_sym = ebnf_sym;
			}
			
			public void accept(TreeWalker walker)
			{
				walker.visit(this);
			}
			
			public String getName()
			{
				return (String) sym_name.value;
			}
			
			public String getAlias()
			{
				return (String) alias.value;
			}
			
			public char getExtUseMark()
			{
				return ebnf_sym.value == null ? ' ' : ((String) ebnf_sym.value).charAt(0);
			}
		}
		
		public final Element[] elements;
		public final Symbol prec_sym_name;
		public final Symbol code;
		
		public Definition(Element[] elts, Symbol prec_sym_name, Symbol code)
		{
			this.elements = elts;
			this.prec_sym_name = prec_sym_name;
			this.code = code;
		}
		
		public Definition(Element[] elts)
		{
			this.elements = elts;
			this.prec_sym_name = null;
			this.code = null;
		}
		
		public void accept(TreeWalker walker)
		{
			walker.visit(this);
		}
		
		public String getPrecedenceSymbolName()
		{
			return (String) prec_sym_name.value;
		}
		
		public String getReduceActionCode()
		{
			return (String) code.value;
		}
	}
	
	public final Symbol lhs_sym;
	public final Definition[] defs;
	
	public Rule(Symbol lhs_sym, Rule.Definition[] defs)
	{
		this.lhs_sym = lhs_sym;
		this.defs = defs;
	}
	
	public String getLHSSymbolName()
	{
		return (String) lhs_sym.value;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
