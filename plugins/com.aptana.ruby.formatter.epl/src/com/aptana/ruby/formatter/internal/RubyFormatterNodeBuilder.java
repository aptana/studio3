/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *     Aptana Inc. - Modify it to work with org.jrubyparser.parser AST (Shalom Gibly)
 *******************************************************************************/
package com.aptana.ruby.formatter.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jrubyparser.ISourcePositionHolder;
import org.jrubyparser.SourcePosition;
import org.jrubyparser.ast.ArgumentNode;
import org.jrubyparser.ast.ArrayNode;
import org.jrubyparser.ast.BeginNode;
import org.jrubyparser.ast.CaseNode;
import org.jrubyparser.ast.ClassNode;
import org.jrubyparser.ast.Colon3Node;
import org.jrubyparser.ast.CommentNode;
import org.jrubyparser.ast.DRegexpNode;
import org.jrubyparser.ast.DStrNode;
import org.jrubyparser.ast.DefnNode;
import org.jrubyparser.ast.DefsNode;
import org.jrubyparser.ast.EnsureNode;
import org.jrubyparser.ast.FCallNode;
import org.jrubyparser.ast.ForNode;
import org.jrubyparser.ast.HashNode;
import org.jrubyparser.ast.IfNode;
import org.jrubyparser.ast.IterNode;
import org.jrubyparser.ast.ListNode;
import org.jrubyparser.ast.MethodDefNode;
import org.jrubyparser.ast.ModuleNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.NodeType;
import org.jrubyparser.ast.PostExeNode;
import org.jrubyparser.ast.PreExeNode;
import org.jrubyparser.ast.RegexpNode;
import org.jrubyparser.ast.RescueBodyNode;
import org.jrubyparser.ast.RescueNode;
import org.jrubyparser.ast.SClassNode;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.UntilNode;
import org.jrubyparser.ast.VCallNode;
import org.jrubyparser.ast.WhenNode;
import org.jrubyparser.ast.WhileNode;
import org.jrubyparser.ast.XStrNode;
import org.jrubyparser.parser.ParserResult;

import com.aptana.editor.ruby.parsing.ast.AbstractVisitor;
import com.aptana.formatter.AbstractFormatterNodeBuilder;
import com.aptana.formatter.IFormatterContainerNode;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterTextNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterArrayNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterAtBeginNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterAtEndNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterBeginNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterCaseNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterClassNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterDoNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterElseIfNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterEnsureNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterForNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterHashNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterIfElseNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterIfEndNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterIfNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterMethodNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterModuleNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRDocNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRequireNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRescueElseNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRescueNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRootNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterStringNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterUntilNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterWhenElseNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterWhenNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterWhileNode;

/**
 * Ruby Formatter node builder.
 * 
 * @author Xored, Shalom Gibly [Aptana]
 */
public class RubyFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	public IFormatterContainerNode build(ParserResult result, final IFormatterDocument document)
	{
		final IFormatterContainerNode root = new FormatterRootNode(document);
		start(root);
		result.getAST().accept(new AbstractVisitor()
		{

			protected Object visitNode(Node visited)
			{
				visitChildren(visited);
				return null;
			}

			public Object visitClassNode(ClassNode visited)
			{
				FormatterClassNode classNode = new FormatterClassNode(document);
				SourcePosition position = visited.getPosition();
				classNode.setBegin(createTextNode(document, position.getStartOffset(), visited.getCPath().getPosition()
						.getEndOffset()));
				push(classNode);
				visitChildren(visited);
				// checkedPop(classNode, visited.getEnd().getPosition().getStartOffset());
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset;
				if (NodeType.NILNODE.equals(bodyNode.getNodeType()))
				{
					bodyEndOffset = classNode.getEndOffset();
				}
				else
				{
					bodyEndOffset = bodyNode.getPosition().getEndOffset();
				}
				checkedPop(classNode, bodyEndOffset);
				classNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitSClassNode(SClassNode visited)
			{
				FormatterClassNode classNode = new FormatterClassNode(document);
				SourcePosition position = visited.getPosition();
				classNode.setBegin(createTextNode(document, visited));
				// .getClassKeyword()));
				push(classNode);
				visitChildren(visited);
				// checkedPop(classNode, visited.getEnd().getPosition().getStartOffset());
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(classNode, bodyEndOffset);
				classNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitModuleNode(ModuleNode visited)
			{
				FormatterModuleNode moduleNode = new FormatterModuleNode(document);
				Colon3Node pathNode = visited.getCPath();
				moduleNode.setBegin(createTextNode(document, pathNode));
				push(moduleNode);
				visitChildren(visited);
				// checkedPop(moduleNode, visited.getEnd().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(moduleNode, bodyEndOffset);
				moduleNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitDefnNode(DefnNode visited)
			{
				return visitMethodDefNode(visited);
			}

			public Object visitDefsNode(DefsNode visited)
			{
				return visitMethodDefNode(visited);
			}

			private Object visitMethodDefNode(MethodDefNode visited)
			{
				FormatterMethodNode methodNode = new FormatterMethodNode(document);
				SourcePosition position = visited.getPosition();
				methodNode.setBegin(createTextNode(document, position.getStartOffset(), visited.getNameNode()
						.getPosition().getEndOffset()));
				push(methodNode);
				visitChildren(visited);
				Node bodyNode = visited.getBodyNode();
				// checkedPop(methodNode, visited.getEnd().getPosition().getStartOffset());
				int bodyEndOffset;
				if (bodyNode != null)
				{
					bodyEndOffset = bodyNode.getPosition().getEndOffset();
				}
				else
				{
					bodyEndOffset = position.getEndOffset() - 3;
				}
				checkedPop(methodNode, bodyEndOffset);
				methodNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitWhileNode(WhileNode visited)
			{
				// if (!visited.isBlock())
				// {
				// visitChildren(visited);
				// return null;
				// }
				FormatterWhileNode whileNode = new FormatterWhileNode(document);
				whileNode.setBegin(createTextNode(document, visited));
				push(whileNode);
				visitChildren(visited);
				// checkedPop(whileNode, visited.getEnd().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(whileNode, bodyEndOffset);
				whileNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitIterNode(IterNode visited)
			{
				FormatterDoNode forNode = new FormatterDoNode(document);
				// Note: The iteration may or may not have a 'varNode'. Also, it may or may not have a 'body'.

				Node varNode = visited.getVarNode();
				Node bodyNode = visited.getBodyNode();
				SourcePosition position = visited.getPosition();
				// Here we try to figure out what to place in the begin segment
				int beginEndOffset = position.getEndOffset();
				if (bodyNode != null)
				{
					beginEndOffset = bodyNode.getPosition().getStartOffset();
				}
				else
				{
					// we have an iteration without a body.
					if (varNode != null)
					{
						// we look for the start offset of the 'end'
						// keyword, right after the var-node
						beginEndOffset = varNode.getPosition().getEndOffset();
						// look for the 'end' keyword start
						beginEndOffset = charLookup(document, beginEndOffset, 'e');
					}
					else
					{
						// we have no body-node, nor var-node.
						// in this case we just look for the start of the 'end' keyword.
						beginEndOffset = locateEndOffset(document, position.getEndOffset());
					}
				}
				forNode.setBegin(createTextNode(document, position.getStartOffset(), beginEndOffset));
				push(forNode);
				visitChildren(visited);
				// checkedPop(forNode, visited.getEnd().getPosition().getStartOffset());
				int bodyEndOffset;
				if (bodyNode != null)
				{
					bodyEndOffset = bodyNode.getPosition().getEndOffset();
				}
				else
				{
					bodyEndOffset = forNode.getEndOffset();
				}
				checkedPop(forNode, bodyEndOffset);
				forNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitForNode(ForNode visited)
			{
				FormatterForNode forNode = new FormatterForNode(document);
				Node bodyNode = visited.getBodyNode();
				forNode.setBegin(createTextNode(document, visited.getPosition().getStartOffset(), bodyNode
						.getPosition().getStartOffset() - 1));
				push(forNode);
				visitChildren(visited);
				// checkedPop(forNode, visited.getEnd().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(forNode, bodyEndOffset);
				forNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitUntilNode(UntilNode visited)
			{
				// if (!visited.isBlock())
				// {
				// visitChild(visited.getBodyNode());
				// FormatterModifierNode block = new FormatterModifierNode(document);
				// block.addChild(createTextNode(document, visited.getKeyword()));
				// push(block);
				// visitChild(visited.getConditionNode());
				// checkedPop(block, visited.getConditionNode().getEndOffset());
				// return null;
				// }
				FormatterUntilNode untilNode = new FormatterUntilNode(document);
				untilNode.setBegin(createTextNode(document, visited));
				push(untilNode);
				visitChildren(visited);
				// checkedPop(untilNode, visited.getEnd().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(untilNode, bodyEndOffset);
				untilNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitCaseNode(CaseNode visited)
			{
				FormatterCaseNode caseNode = new FormatterCaseNode(document);
				// final int caseEnd = visited.getCaseNode().getPosition().getEndOffset();
				SourcePosition position = visited.getPosition();
				// caseNode.setBegin(createTextNode(document, position.getStartOffset(), caseEnd));
				caseNode.setBegin(createTextNode(document, position.getStartOffset(), visited.getCaseNode()
						.getPosition().getEndOffset()));
				push(caseNode);
				Node branch = visited.getFirstWhenNode();
				List<Node> children;
				if (branch instanceof ArrayNode)
				{
					children = branch.childNodes();
				}
				else
				{
					children = new ArrayList<Node>();
					children.add(branch);
				}
				int bodyEndOffset = position.getEndOffset();
				for (Node child : children)
				{
					if (child instanceof WhenNode)
					{
						WhenNode whenBranch = (WhenNode) child;
						FormatterWhenNode whenNode = new FormatterWhenNode(document);
						SourcePosition branchPosition = child.getPosition();
						whenNode.setBegin(createTextNode(document, branchPosition.getStartOffset(), whenBranch
								.getExpressionNodes().getPosition().getEndOffset()));
						push(whenNode);
						Node whenBodyNode = whenBranch.getBodyNode();
						visitChild(whenBodyNode);
						checkedPop(whenNode, whenBodyNode.getPosition().getEndOffset());
						bodyEndOffset = whenNode.getEndOffset();
					}
					else
					{
						FormatterWhenElseNode whenElseNode = new FormatterWhenElseNode(document);
						SourcePosition elsePosition = child.getPosition();
						whenElseNode.setBegin(createTextNode(document, child));
						push(whenElseNode);
						visitChildren(child.childNodes());
						checkedPop(whenElseNode, elsePosition.getEndOffset());
						bodyEndOffset = whenElseNode.getEndOffset();
					}
				}
				checkedPop(caseNode, bodyEndOffset);
				caseNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitCommentNode(CommentNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterRDocNode commentNode = new FormatterRDocNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(commentNode);
				return null;
			}

			public Object visitIfNode(IfNode visited)
			{
				// if (visited.isInline())
				// {
				// List children = new ArrayList(3);
				// if (visited.getThenBody() != null)
				// {
				// children.add(visited.getThenBody());
				// }
				// if (visited.getElseBody() != null)
				// {
				// children.add(visited.getElseBody());
				// }
				// if (visited.getCondition() != null)
				// {
				// children.add(visited.getCondition());
				// }
				// if (!children.isEmpty())
				// {
				// Collections.sort(children, POSITION_COMPARATOR);
				// visitChildren(children);
				// }
				// return null;
				// }
				FormatterIfNode ifNode = new FormatterIfNode(document);
				SourcePosition position = visited.getPosition();
				ifNode.setBegin(createTextNode(document, position.getStartOffset(), visited.getCondition()
						.getPosition().getEndOffset()));
				push(ifNode);
				Node thenBody = visited.getThenBody();
				Node elseBody = visited.getElseBody();
				// Flip the 'else' and 'then' in case the 'then' appears 'after the 'else'
				// This is the case with 'unless', so we flip it to make it easier to handle.
				int ifNodeEnd = -1; // the end position for the entire if block.
				if (elseBody != null)
				{
					ifNodeEnd = elseBody.getPosition().getEndOffset();
				}
				else if (thenBody != null)
				{
					ifNodeEnd = thenBody.getPosition().getEndOffset();
				}
				if (elseBody != null && thenBody != null)
				{
					if (thenBody.getPosition().getStartOffset() > elseBody.getPosition().getStartOffset())
					{
						// we also need to update the end position of the entire if block
						ifNodeEnd = thenBody.getPosition().getEndOffset();
						// flip the 'the' and 'else'
						Node temp = thenBody;
						thenBody = elseBody;
						elseBody = temp;
					}
				}
				visitChild(thenBody);
				if (thenBody == null && elseBody != null)
				{
					// We have an 'unless' case, so we just visit the else-boby
					visitChild(elseBody);
				}
				checkedPop(ifNode, ifNode.getEndOffset());

				while (elseBody != null && thenBody != null)
				{
					if (elseBody instanceof IfNode)
					{
						// elsif
						IfNode elseIfBranch = (IfNode) elseBody;
						FormatterElseIfNode elseIfNode = new FormatterElseIfNode(document);
						elseIfNode.setBegin(createTextNode(document, thenBody.getPosition().getEndOffset(),
								elseIfBranch.getCondition().getPosition().getEndOffset()));
						push(elseIfNode);
						thenBody = elseIfBranch.getThenBody();
						visitChild(thenBody);
						elseBody = elseIfBranch.getElseBody();
						// checkedPop(elseIfNode, branch != null ? branch.getStartOffset() : visited.getEndKeyword()
						// .getPosition().getStartOffset());
						checkedPop(elseIfNode, elseIfNode.getEndOffset());
					}
					else
					{
						// else
						FormatterIfElseNode elseNode = new FormatterIfElseNode(document);
						elseNode.setBegin(createTextNode(document, thenBody.getPosition().getEndOffset(), elseBody
								.getPosition().getStartOffset()));
						push(elseNode);
						visitChild(elseBody);
						checkedPop(elseNode, elseNode.getEndOffset());
						elseBody = null;
					}
				}

				if (ifNodeEnd < 0)
				{
					// locate the 'end'
					ifNodeEnd = locateEndOffset(document, position.getEndOffset());
				}
				addChild(new FormatterIfEndNode(document, ifNodeEnd, position.getEndOffset()));
				return null;
			}

			/*
			 * @see org.jruby.ast.visitor.AbstractVisitor#visitBeginNode(org.jruby .ast.BeginNode)
			 */
			public Object visitBeginNode(BeginNode visited)
			{
				FormatterBeginNode beginNode = new FormatterBeginNode(document);
				beginNode.setBegin(createTextNode(document, visited));
				push(beginNode);
				visitChild(visited.getBodyNode());
				// checkedPop(beginNode, visited.getEndKeyword().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(beginNode, bodyEndOffset);
				beginNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitRescueNode(RescueNode visited)
			{
				// if (visited.isInline())
				// {
				// return null;
				// }
				visitChild(visited.getBodyNode());
				RescueBodyNode node = visited.getRescueNode();
				while (node != null)
				{
					FormatterRescueNode rescueNode = new FormatterRescueNode(document);
					rescueNode.setBegin(createTextNode(document, node.getPosition().getStartOffset(), node
							.getExceptionNodes() != null ? node.getExceptionNodes().getPosition().getEndOffset() : node
							.getPosition().getEndOffset()));
					push(rescueNode);
					visitChild(node.getBodyNode());
					node = node.getOptRescueNode();
					final int rescueEnd;
					if (node != null)
					{
						rescueEnd = node.getPosition().getStartOffset();
					}
					else if (visited.getElseNode() != null)
					{
						rescueEnd = visited.getElseNode().getPosition().getStartOffset();
					}
					else
					{
						rescueEnd = -1;
					}
					checkedPop(rescueNode, rescueEnd);
				}
				if (visited.getElseNode() != null)
				{
					final Node elseBranch = visited.getElseNode();
					FormatterRescueElseNode elseNode = new FormatterRescueElseNode(document);
					elseNode.setBegin(createTextNode(document, elseBranch));
					push(elseNode);
					visitChildren(elseBranch.childNodes());
					checkedPop(elseNode, -1);
				}
				return null;
			}

			public Object visitEnsureNode(EnsureNode visited)
			{
				visitChild(visited.getBodyNode());
				FormatterEnsureNode ensureNode = new FormatterEnsureNode(document);
				Node node = visited.getEnsureNode();
				ensureNode.setBegin(createTextNode(document, node));
				push(ensureNode);
				visitChildren(node.childNodes());
				checkedPop(ensureNode, -1);
				return null;
			}

			public Object visitPreExeNode(PreExeNode visited)
			{
				FormatterAtBeginNode endNode = new FormatterAtBeginNode(document);
				// endNode.setBegin(createTextNode(document, visited.getKeyword().getPosition().getStartOffset(),
				// visited
				// .getLeftBrace().getPosition().getEndOffset()));
				endNode.setBegin(createTextNode(document, visited));
				push(endNode);
				visitChildren(visited);
				// checkedPop(endNode, visited.getRightBrace().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(endNode, bodyEndOffset);
				endNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitPostExeNode(PostExeNode visited)
			{
				FormatterAtEndNode endNode = new FormatterAtEndNode(document);
				// endNode.setBegin(createTextNode(document, visited.getEndKeyword().getPosition().getStartOffset(),
				// visited.getLeftBrace().getPosition().getEndOffset()));
				endNode.setBegin(createTextNode(document, visited));
				push(endNode);
				visitChildren(visited);
				// checkedPop(endNode, visited.getRightBrace().getPosition().getStartOffset());
				SourcePosition position = visited.getPosition();
				Node bodyNode = visited.getBodyNode();
				int bodyEndOffset = bodyNode.getPosition().getEndOffset();
				checkedPop(endNode, bodyEndOffset);
				endNode.setEnd(createTextNode(document, bodyEndOffset, position.getEndOffset()));
				return null;
			}

			public Object visitStrNode(StrNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			public Object visitVCallNode(VCallNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			public Object visitDStrNode(DStrNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			public Object visitRegexpNode(RegexpNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			public Object visitDRegxNode(DRegexpNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			public Object visitXStrNode(XStrNode visited)
			{
				SourcePosition position = visited.getPosition();
				FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
						.getEndOffset());
				addChild(strNode);
				return null;
			}

			// public Object visitHeredocNode(HeredocNode visited)
			// {
			// SourcePosition position = visited.getPosition();
			// FormatterHereDocNode heredocNode = new FormatterHereDocNode(document, position.getStartOffset(), position
			// .getEndOffset(), visited.isIndent());
			// addChild(heredocNode);
			// heredocNode.setContentRegion(createRegion(visited.getContent().getPosition()));
			// heredocNode.setEndMarkerRegion(createRegion(visited.getEndMarker().getPosition()));
			// return null;
			// }

			public Object visitFCallNode(FCallNode visited)
			{
				if (isRequireMethod(visited))
				{
					SourcePosition position = visited.getPosition();
					FormatterRequireNode requireNode = new FormatterRequireNode(document, position.getStartOffset(),
							position.getEndOffset());
					addChild(requireNode);
				}
				else
				{
					SourcePosition position = visited.getPosition();
					FormatterStringNode strNode = new FormatterStringNode(document, position.getStartOffset(), position
							.getEndOffset());
					addChild(strNode);
				}
				return null;
			}

			public Object visitArrayNode(ArrayNode visited)
			{
				SourcePosition position = visited.getPosition();
				String right = document.get(position.getStartOffset(), position.getStartOffset() + 1);
				String left = document.get(position.getEndOffset() - 1, position.getEndOffset());
				// if (visited.getLeftBracketPosition() != null && visited.getRightBracketPosition() != null)
				if ("[".equals(right) && "]".equals(left)) { //$NON-NLS-1$ //$NON-NLS-2$
					final FormatterArrayNode arrayNode = new FormatterArrayNode(document);
					arrayNode.setBegin(createTextNode(document, visited));
					push(arrayNode);
					SourcePosition lastNodePosition = visited.getLast().getPosition();
					checkedPop(arrayNode, lastNodePosition.getEndOffset());
					arrayNode.setEnd(createTextNode(document, position.getEndOffset() - 1, position.getEndOffset()));
					return null;
				}
				else
				{
					return super.visitArrayNode(visited);
				}
			}

			public Object visitHashNode(HashNode visited)
			{

				SourcePosition position = visited.getPosition();
				String right = document.get(position.getStartOffset(), position.getStartOffset() + 1);
				String left = document.get(position.getEndOffset() - 1, position.getEndOffset());
				// if (visited.getLeftBrace() != null && visited.getRightBrace() != null)
				if ("{".equals(right) && "}".equals(left)) { //$NON-NLS-1$ //$NON-NLS-2$
					final FormatterHashNode hashNode = new FormatterHashNode(document);
					hashNode.setBegin(createTextNode(document, visited));
					push(hashNode);
					// checkedPop(hashNode, right.getStartOffset());
					SourcePosition listNodePosition = visited.getListNode().getPosition();
					checkedPop(hashNode, listNodePosition.getEndOffset());
					hashNode.setEnd(createTextNode(document, position.getEndOffset() - 1, position.getEndOffset()));
					return null;
				}
				else
				{
					return super.visitHashNode(visited);
				}
			}

			private boolean isRequireMethod(FCallNode call)
			{
				if ("require".equals(call.getName())) //$NON-NLS-1$
				{
					if (call.getArgsNode() instanceof ArrayNode)
					{
						return true;
					}
				}
				return false;
			}

			private void visitChildren(Node visited)
			{
				final List<Node> children = visited.childNodes();
				if (!children.isEmpty())
				{
					visitChildren(children);
				}
			}

			private void visitChildren(List<Node> children)
			{
				for (Node child : children)
				{
					visitChild(child);
				}
			}

			private void visitChild(final Node child)
			{
				if (child != null && isVisitable(child))
				{
					child.accept(this);
				}
			}

			private boolean isVisitable(Node node)
			{
				return !(node instanceof ArgumentNode) && node.getClass() != ListNode.class;
			}
		});
		checkedPop(root, document.getLength());
		return root;
	}

	/**
	 * @param positionHolder
	 * @return
	 */
	private IFormatterTextNode createTextNode(IFormatterDocument document, ISourcePositionHolder positionHolder)
	{
		return createTextNode(document, positionHolder.getPosition());
	}

	/**
	 * Locate the word 'end' by searching for it from right to left in the given document. The assumption here is that
	 * the 'end' word is actually there, and the only thing that separate us from reaching it are white-spaces.
	 * 
	 * @param document
	 * @param rightOffset
	 * @return The start offset of the 'end' word
	 */
	protected int locateEndOffset(IFormatterDocument document, int rightOffset)
	{
		String toLocate = "end"; //$NON-NLS-1$
		int wordLength = toLocate.length();
		do
		{
			int leftOffset = rightOffset - wordLength;
			String endString = document.get(leftOffset, rightOffset);
			if (toLocate.equals(endString))
			{
				// found it!
				return leftOffset;
			}
			rightOffset--;
		}
		while (rightOffset - wordLength >= 0);
		// if we got here, we didn't find the 'end'
		return rightOffset;
	}

	/**
	 * Look for the given char in the document and return it's position.
	 * 
	 * @param document
	 * @param offset
	 * @param c
	 * @return The char offset; -1 if no matching char was found
	 */
	protected int charLookup(final IFormatterDocument document, int offset, char c)
	{
		while (offset + 1 < document.getLength())
		{
			if (document.charAt(offset) == c)
			{
				return offset;
			}
			offset++;
		}
		return -1;
	}

	/**
	 * @param position
	 * @return
	 */
	private IFormatterTextNode createTextNode(IFormatterDocument document, SourcePosition position)
	{
		return createTextNode(document, position.getStartOffset(), position.getEndOffset());
	}

	private static IRegion createRegion(SourcePosition position)
	{
		return new Region(position.getStartOffset(), position.getEndOffset() - position.getStartOffset());
	}

	protected static final Comparator POSITION_COMPARATOR = new Comparator()
	{

		public int compare(Object o1, Object o2)
		{
			final Node node1 = (Node) o1;
			final Node node2 = (Node) o2;
			return node1.getPosition().getStartOffset() - node2.getPosition().getStartOffset();
		}
	};
}
