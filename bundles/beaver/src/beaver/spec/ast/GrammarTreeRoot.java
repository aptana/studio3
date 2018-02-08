/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec.ast;

/**
 * AST root node
 */
public class GrammarTreeRoot extends Node
{
	public final Declaration[] declarations;
	public final Rule[] rules;
	
	public GrammarTreeRoot(Declaration[] declarations, Rule[] rules)
	{
		this.declarations = declarations;
		this.rules = rules;
	}
	
	public void accept(TreeWalker walker)
	{
		walker.visit(this);
	}
}
