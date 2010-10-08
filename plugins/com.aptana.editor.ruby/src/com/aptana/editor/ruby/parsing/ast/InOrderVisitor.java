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

import java.util.Iterator;

import org.jrubyparser.ast.*;

/**
 * @author Chris Williams
 * @author Michael Xia
 */
public class InOrderVisitor extends AbstractVisitor
{

	@Override
	public Object visitAliasNode(AliasNode iVisited)
	{
		handleNode(iVisited);
		return super.visitAliasNode(iVisited);
	}

	@Override
	public Object visitAndNode(AndNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitAndNode(iVisited);
	}

	@Override
	public Object visitArgsCatNode(ArgsCatNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitArgsCatNode(iVisited);
	}

	@Override
	public Object visitArgsNode(ArgsNode iVisited)
	{
		handleNode(iVisited);
//		if (iVisited.getPre() != null)
//		{
//			visitIter(iVisited.getPre().childNodes().iterator());
//		}
		if (iVisited.getOptional() != null)
		{
			visitIter(iVisited.getOptional().childNodes().iterator());
		}
//		acceptNode(iVisited.getRest());
//		if (iVisited.getPost() != null)
//		{
//			visitIter(iVisited.getPost().childNodes().iterator());
//		}
		acceptNode(iVisited.getBlock());
		return super.visitArgsNode(iVisited);
	}
	
	@Override
	public Object visitRestArgNode(RestArgNode iVisited)
	{
		handleNode(iVisited);
		return super.visitRestArgNode(iVisited);
	}

	@Override
	public Object visitArgsPushNode(ArgsPushNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitArgsPushNode(iVisited);
	}

	@Override
	public Object visitArrayNode(ArrayNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitArrayNode(iVisited);
	}

	@Override
	public Object visitAttrAssignNode(AttrAssignNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getArgsNode());
		return super.visitAttrAssignNode(iVisited);
	}

	@Override
	public Object visitBackRefNode(BackRefNode iVisited)
	{
		handleNode(iVisited);
		return super.visitBackRefNode(iVisited);
	}

	@Override
	public Object visitBeginNode(BeginNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBodyNode());
		return super.visitBeginNode(iVisited);
	}

	@Override
	public Object visitBignumNode(BignumNode iVisited)
	{
		handleNode(iVisited);
		return super.visitBignumNode(iVisited);
	}

	@Override
	public Object visitBlockArgNode(BlockArgNode iVisited)
	{
		handleNode(iVisited);
		return super.visitBlockArgNode(iVisited);
	}

	@Override
	public Object visitBlockNode(BlockNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitBlockNode(iVisited);
	}

	@Override
	public Object visitBlockPassNode(BlockPassNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitBlockPassNode(iVisited);
	}

	@Override
	public Object visitBreakNode(BreakNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitBreakNode(iVisited);
	}

	@Override
	public Object visitConstDeclNode(ConstDeclNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitConstDeclNode(iVisited);
	}

	@Override
	public Object visitClassVarAsgnNode(ClassVarAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitClassVarAsgnNode(iVisited);
	}

	@Override
	public Object visitClassVarDeclNode(ClassVarDeclNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitClassVarDeclNode(iVisited);
	}

	@Override
	public Object visitClassVarNode(ClassVarNode iVisited)
	{
		handleNode(iVisited);
		return super.visitClassVarNode(iVisited);
	}

	@Override
	public Object visitCallNode(CallNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		Node args = iVisited.getArgsNode();
		if (args instanceof ListNode && ((ListNode) args).size() > 0)
		{
			acceptNode(args);
		}
		acceptNode(iVisited.getIterNode());
		return super.visitCallNode(iVisited);
	}

	@Override
	public Object visitCaseNode(CaseNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitCaseNode(iVisited);
	}

	@Override
	public Object visitClassNode(ClassNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getCPath());
		acceptNode(iVisited.getSuperNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitClassNode(iVisited);
	}

	@Override
	public Object visitColon2Node(Colon2Node iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getLeftNode());
		return super.visitColon2Node(iVisited);
	}

	@Override
	public Object visitColon3Node(Colon3Node iVisited)
	{
		handleNode(iVisited);
		return super.visitColon3Node(iVisited);
	}

	@Override
	public Object visitConstNode(ConstNode iVisited)
	{
		handleNode(iVisited);
		return super.visitConstNode(iVisited);
	}

	@Override
	public Object visitDAsgnNode(DAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitDAsgnNode(iVisited);
	}

	@Override
	public Object visitDRegxNode(DRegexpNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitDRegxNode(iVisited);
	}

	@Override
	public Object visitDStrNode(DStrNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitDStrNode(iVisited);
	}

	@Override
	public Object visitDSymbolNode(DSymbolNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitDSymbolNode(iVisited);
	}

	@Override
	public Object visitDVarNode(DVarNode iVisited)
	{
		handleNode(iVisited);
		return super.visitDVarNode(iVisited);
	}

	@Override
	public Object visitDXStrNode(DXStrNode iVisited)
	{
		handleNode(iVisited);
		visitIter(iVisited.childNodes().iterator());
		return super.visitDXStrNode(iVisited);
	}

	@Override
	public Object visitDefinedNode(DefinedNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getExpressionNode());
		return super.visitDefinedNode(iVisited);
	}

	@Override
	public Object visitDefnNode(DefnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitDefnNode(iVisited);
	}

	@Override
	public Object visitDefsNode(DefsNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitDefsNode(iVisited);
	}

	@Override
	public Object visitDotNode(DotNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBeginNode());
		acceptNode(iVisited.getEndNode());
		return super.visitDotNode(iVisited);
	}

	@Override
	public Object visitEncodingNode(EncodingNode iVisited)
	{
		handleNode(iVisited);
		return super.visitEncodingNode(iVisited);
	}

	@Override
	public Object visitEnsureNode(EnsureNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getEnsureNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitEnsureNode(iVisited);
	}

	@Override
	public Object visitEvStrNode(EvStrNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBody());
		return super.visitEvStrNode(iVisited);
	}

	@Override
	public Object visitFCallNode(FCallNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getIterNode());
		return super.visitFCallNode(iVisited);
	}

	@Override
	public Object visitFalseNode(FalseNode iVisited)
	{
		handleNode(iVisited);
		return super.visitFalseNode(iVisited);
	}

	@Override
	public Object visitFixnumNode(FixnumNode iVisited)
	{
		handleNode(iVisited);
		return super.visitFixnumNode(iVisited);
	}

	@Override
	public Object visitFlipNode(FlipNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBeginNode());
		acceptNode(iVisited.getEndNode());
		return super.visitFlipNode(iVisited);
	}

	@Override
	public Object visitFloatNode(FloatNode iVisited)
	{
		handleNode(iVisited);
		return super.visitFloatNode(iVisited);
	}

	@Override
	public Object visitForNode(ForNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getVarNode());
		acceptNode(iVisited.getIterNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitForNode(iVisited);
	}

	@Override
	public Object visitGlobalAsgnNode(GlobalAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitGlobalAsgnNode(iVisited);
	}

	@Override
	public Object visitGlobalVarNode(GlobalVarNode iVisited)
	{
		handleNode(iVisited);
		return super.visitGlobalVarNode(iVisited);
	}

	@Override
	public Object visitHashNode(HashNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getListNode());
		return super.visitHashNode(iVisited);
	}

	@Override
	public Object visitInstAsgnNode(InstAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitInstAsgnNode(iVisited);
	}

	@Override
	public Object visitInstVarNode(InstVarNode iVisited)
	{
		handleNode(iVisited);
		return super.visitInstVarNode(iVisited);
	}

	@Override
	public Object visitIfNode(IfNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getCondition());
		acceptNode(iVisited.getThenBody());
		acceptNode(iVisited.getElseBody());
		return super.visitIfNode(iVisited);
	}

	@Override
	public Object visitIterNode(IterNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getVarNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitIterNode(iVisited);
	}

	@Override
	public Object visitLocalAsgnNode(LocalAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitLocalAsgnNode(iVisited);
	}

	@Override
	public Object visitLocalVarNode(LocalVarNode iVisited)
	{
		handleNode(iVisited);
		return super.visitLocalVarNode(iVisited);
	}

	@Override
	public Object visitMultipleAsgnNode(MultipleAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getHeadNode());
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getValueNode());
		return super.visitMultipleAsgnNode(iVisited);
	}

	@Override
	public Object visitMultipleAsgnNode(MultipleAsgn19Node iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getPre());
		acceptNode(iVisited.getRest());
		acceptNode(iVisited.getValueNode());
		return super.visitMultipleAsgnNode(iVisited);
	}

	@Override
	public Object visitMatch2Node(Match2Node iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getValueNode());
		return super.visitMatch2Node(iVisited);
	}

	@Override
	public Object visitMatch3Node(Match3Node iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getValueNode());
		return super.visitMatch3Node(iVisited);
	}

	@Override
	public Object visitMatchNode(MatchNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getRegexpNode());
		return super.visitMatchNode(iVisited);
	}

	@Override
	public Object visitModuleNode(ModuleNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBodyNode());
		return super.visitModuleNode(iVisited);
	}

	@Override
	public Object visitNewlineNode(NewlineNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getNextNode());
		return super.visitNewlineNode(iVisited);
	}

	@Override
	public Object visitNextNode(NextNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitNextNode(iVisited);
	}

	@Override
	public Object visitNilNode(NilNode iVisited)
	{
		if (!(iVisited instanceof NilImplicitNode))
		{
			handleNode(iVisited);
		}
		return super.visitNilNode(iVisited);
	}

	@Override
	public Object visitNotNode(NotNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getConditionNode());
		return super.visitNotNode(iVisited);
	}

	@Override
	public Object visitNthRefNode(NthRefNode iVisited)
	{
		handleNode(iVisited);
		return super.visitNthRefNode(iVisited);
	}

	@Override
	public Object visitOpElementAsgnNode(OpElementAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getArgsNode());
		acceptNode(iVisited.getValueNode());
		return super.visitOpElementAsgnNode(iVisited);
	}

	@Override
	public Object visitOpAsgnNode(OpAsgnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getValueNode());
		return super.visitOpAsgnNode(iVisited);
	}

	@Override
	public Object visitOpAsgnAndNode(OpAsgnAndNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitOpAsgnAndNode(iVisited);
	}

	@Override
	public Object visitOpAsgnOrNode(OpAsgnOrNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitOpAsgnOrNode(iVisited);
	}

	@Override
	public Object visitOrNode(OrNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getFirstNode());
		acceptNode(iVisited.getSecondNode());
		return super.visitOrNode(iVisited);
	}

	@Override
	public Object visitPostExeNode(PostExeNode iVisited)
	{
		handleNode(iVisited);
		return super.visitPostExeNode(iVisited);
	}

	@Override
	public Object visitRedoNode(RedoNode iVisited)
	{
		handleNode(iVisited);
		return super.visitRedoNode(iVisited);
	}

	@Override
	public Object visitRegexpNode(RegexpNode iVisited)
	{
		handleNode(iVisited);
		return super.visitRegexpNode(iVisited);
	}

	@Override
	public Object visitRescueBodyNode(RescueBodyNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getExceptionNodes());
		acceptNode(iVisited.getOptRescueNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitRescueBodyNode(iVisited);
	}

	@Override
	public Object visitRescueNode(RescueNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getRescueNode());
		acceptNode(iVisited.getBodyNode());
		acceptNode(iVisited.getElseNode());
		return super.visitRescueNode(iVisited);
	}

	@Override
	public Object visitRetryNode(RetryNode iVisited)
	{
		handleNode(iVisited);
		return super.visitRetryNode(iVisited);
	}

	@Override
	public Object visitReturnNode(ReturnNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValueNode());
		return super.visitReturnNode(iVisited);
	}

	@Override
	public Object visitRootNode(RootNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getBodyNode());
		return super.visitRootNode(iVisited);
	}

	@Override
	public Object visitSClassNode(SClassNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getReceiverNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitSClassNode(iVisited);
	}

	@Override
	public Object visitSelfNode(SelfNode iVisited)
	{
		handleNode(iVisited);
		return super.visitSelfNode(iVisited);
	}

	@Override
	public Object visitSplatNode(SplatNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValue());
		return super.visitSplatNode(iVisited);
	}

	@Override
	public Object visitStrNode(StrNode iVisited)
	{
		handleNode(iVisited);
		return super.visitStrNode(iVisited);
	}

	@Override
	public Object visitSuperNode(SuperNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getArgsNode());
		return super.visitSuperNode(iVisited);
	}

	@Override
	public Object visitSValueNode(SValueNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValue());
		return super.visitSValueNode(iVisited);
	}

	@Override
	public Object visitSymbolNode(SymbolNode iVisited)
	{
		handleNode(iVisited);
		return super.visitSymbolNode(iVisited);
	}

	@Override
	public Object visitToAryNode(ToAryNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getValue());
		return super.visitToAryNode(iVisited);
	}

	@Override
	public Object visitTrueNode(TrueNode iVisited)
	{
		handleNode(iVisited);
		return super.visitTrueNode(iVisited);
	}

	@Override
	public Object visitUndefNode(UndefNode iVisited)
	{
		handleNode(iVisited);
		return super.visitUndefNode(iVisited);
	}

	@Override
	public Object visitUntilNode(UntilNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getConditionNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitUntilNode(iVisited);
	}

	@Override
	public Object visitVAliasNode(VAliasNode iVisited)
	{
		handleNode(iVisited);
		return super.visitVAliasNode(iVisited);
	}

	@Override
	public Object visitVCallNode(VCallNode iVisited)
	{
		handleNode(iVisited);
		return super.visitVCallNode(iVisited);
	}

	@Override
	public Object visitWhenNode(WhenNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getExpressionNodes());
		acceptNode(iVisited.getBodyNode());
		acceptNode(iVisited.getNextCase());
		return super.visitWhenNode(iVisited);
	}

	@Override
	public Object visitWhileNode(WhileNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getConditionNode());
		acceptNode(iVisited.getBodyNode());
		return super.visitWhileNode(iVisited);
	}

	@Override
	public Object visitXStrNode(XStrNode iVisited)
	{
		handleNode(iVisited);
		return super.visitXStrNode(iVisited);
	}

	@Override
	public Object visitYieldNode(YieldNode iVisited)
	{
		handleNode(iVisited);
		acceptNode(iVisited.getArgsNode());
		return super.visitYieldNode(iVisited);
	}

	@Override
	public Object visitZArrayNode(ZArrayNode iVisited)
	{
		handleNode(iVisited);
		return super.visitZArrayNode(iVisited);
	}

	@Override
	public Object visitZSuperNode(ZSuperNode iVisited)
	{
		handleNode(iVisited);
		return super.visitZSuperNode(iVisited);
	}

	@Override
	protected Object visitNode(Node iVisited)
	{
		return null;
	}

	protected Object handleNode(Node visited)
	{
		return visitNode(visited);
	}

	/**
	 * @param iterator
	 */
	private Object visitIter(Iterator<Node> iterator)
	{
		while (iterator.hasNext())
		{
			acceptNode(iterator.next());
		}
		return null;
	}
}
