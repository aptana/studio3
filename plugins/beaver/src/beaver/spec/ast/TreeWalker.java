/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec.ast;

/**
 *
 */
public class TreeWalker
{
	public void visit(GrammarTreeRoot node)
	{
		for (int i = 0; i < node.declarations.length; i++)
		{
			node.declarations[i].accept(this);
		}
		for (int i = 0; i < node.rules.length; i++)
		{
			node.rules[i].accept(this);
		}
	}
	
	public void visit(Declaration.Error node)
	{
		// leaf
	}

	public void visit(Declaration.Header node)
	{
		// leaf
	}
	
	public void visit(Declaration.PackageName node)
	{
		// leaf
	}
	
	public void visit(Declaration.Implements node)
	{
		// leaf
	}

	public void visit(Declaration.Imports node)
	{
		// leaf
	}
	
	public void visit(Declaration.ClassName node)
	{
		// leaf
	}
	
	public void visit(Declaration.ClassCode node)
	{
		// leaf
	}
	
	public void visit(Declaration.ConstructorCode node)
	{
		// leaf
	}
	
	public void visit(Declaration.LeftAssoc node)
	{
		// leaf
	}
	
	public void visit(Declaration.RightAssoc node)
	{
		// leaf
	}
	
	public void visit(Declaration.NonAssoc node)
	{
		// leaf
	}
	
	public void visit(Declaration.Goal node)
	{
		// leaf
	}
	
	public void visit(Declaration.ListType node)
	{
		// leaf
	}
	
	public void visit(Declaration.Terminals node)
	{
		// leaf
	}
	
	public void visit(Declaration.TypeOf node)
	{
		// leaf
	}
	
	public void visit(Rule node)
	{
		for (int i = 0; i < node.defs.length; i++)
		{
			node.defs[i].accept(this);
		}
	}
	
	public void visit(Rule.Definition node)
	{
		for (int i = 0; i < node.elements.length; i++)
		{
			node.elements[i].accept(this);
		}
	}
	
	public void visit(Rule.Definition.Element node)
	{
		// leaf
	}
}
