/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.ruby.parsing.ast;

import org.jrubyparser.ast.*;
import org.jrubyparser.NodeVisitor;

/**
 * @author Chris Williams
 */
public abstract class AbstractVisitor implements NodeVisitor
{

	protected abstract Object visitNode(Node iVisited);

	public Object visitNullNode()
	{
		return visitNode(null);
	}

	public Object acceptNode(Node node)
	{
		if (node == null)
		{
			return visitNullNode();
		}
		return node.accept(this);
	}

	public Object visitAliasNode(AliasNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitAndNode(AndNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitArgsCatNode(ArgsCatNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitArgsNode(ArgsNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitArgsPushNode(ArgsPushNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitArrayNode(ArrayNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitAttrAssignNode(AttrAssignNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBackRefNode(BackRefNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBeginNode(BeginNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBignumNode(BignumNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBlockArgNode(BlockArgNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBlockNode(BlockNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBlockPassNode(BlockPassNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitBreakNode(BreakNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitCallNode(CallNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitCaseNode(CaseNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitClassNode(ClassNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitClassVarDeclNode(ClassVarDeclNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitClassVarNode(ClassVarNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitColon2Node(Colon2Node iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitColon3Node(Colon3Node iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitConstDeclNode(ConstDeclNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitConstNode(ConstNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDAsgnNode(DAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDRegxNode(DRegexpNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDStrNode(DStrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDSymbolNode(DSymbolNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDVarNode(DVarNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDXStrNode(DXStrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDefinedNode(DefinedNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDefnNode(DefnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDefsNode(DefsNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitDotNode(DotNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitEncodingNode(EncodingNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitEnsureNode(EnsureNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitEvStrNode(EvStrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitFCallNode(FCallNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitFalseNode(FalseNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitFixnumNode(FixnumNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitFlipNode(FlipNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitFloatNode(FloatNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitForNode(ForNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitGlobalVarNode(GlobalVarNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitHashNode(HashNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitIfNode(IfNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitInstAsgnNode(InstAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitInstVarNode(InstVarNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitIterNode(IterNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitLocalAsgnNode(LocalAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitLocalVarNode(LocalVarNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitMatch2Node(Match2Node iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitMatch3Node(Match3Node iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitMatchNode(MatchNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitModuleNode(ModuleNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitMultipleAsgnNode(MultipleAsgn19Node iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitNewlineNode(NewlineNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitNextNode(NextNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitNilNode(NilNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitNotNode(NotNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitNthRefNode(NthRefNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitOpAsgnNode(OpAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitOrNode(OrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitPostExeNode(PostExeNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitPreExeNode(PreExeNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRedoNode(RedoNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRegexpNode(RegexpNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRescueBodyNode(RescueBodyNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRescueNode(RescueNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRestArgNode(RestArgNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRetryNode(RetryNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitReturnNode(ReturnNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitRootNode(RootNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSClassNode(SClassNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSValueNode(SValueNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSelfNode(SelfNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSplatNode(SplatNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitStrNode(StrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSuperNode(SuperNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitSymbolNode(SymbolNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitToAryNode(ToAryNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitTrueNode(TrueNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitUndefNode(UndefNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitUntilNode(UntilNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitVAliasNode(VAliasNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitVCallNode(VCallNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitWhenNode(WhenNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitWhileNode(WhileNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitXStrNode(XStrNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitYieldNode(YieldNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitZArrayNode(ZArrayNode iVisited)
	{
		return visitNode(iVisited);
	}

	public Object visitZSuperNode(ZSuperNode iVisited)
	{
		return visitNode(iVisited);
	}
}
