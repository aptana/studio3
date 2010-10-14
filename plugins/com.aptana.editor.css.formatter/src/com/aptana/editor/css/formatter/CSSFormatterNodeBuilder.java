package com.aptana.editor.css.formatter;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterCommentNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSBlockNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSCommentNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSDeclarationNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSRuleNode;
import com.aptana.editor.css.parsing.ast.*;

/**
 * CSS formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link HTMLFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 */
public class CSSFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	private FormatterDocument document;

	/**
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterBlockNode(document);
		start(rootNode);
		IParseNode[] children = parseResult.getChildren();
		addNodes(children);
		checkedPop(rootNode, document.getLength());
		return rootNode;
	}

	/**
	 * @param children
	 * @param rootNode
	 */
	private void addNodes(IParseNode[] children)
	{
		if (children == null || children.length == 0)
		{
			return;
		}
		for (IParseNode child : children)
		{
			addNode(child);
		}
	}

	/**
	 * @param node
	 * @param rootNode
	 */
	private void addNode(IParseNode node)
	{

		// DEBUG
		// System.out.println(elementNode.getName() + "[" + elementNode.getStartingOffset() + ", "
		// + elementNode.getEndingOffset() + "]");

		CSSNode cssNode = (CSSNode) node;
		if (cssNode.getNodeType() == CSSNodeTypes.COMMENT)
		{
			// We got a CSSCommentNode
			FormatterCommentNode commentNode = new FormatterCSSCommentNode(document, cssNode.getStartingOffset(),
					cssNode.getEndingOffset() + 1);
			// We just need to add a child here. We cannot 'push', since the comment node is not a container node.
			addChild(commentNode);
		}
		else if (cssNode.getNodeType() == CSSNodeTypes.RULE)
		{
			pushFormatterNode(cssNode);
		}

	}

	/**
	 * Determine the type of the node and return a formatter node that should represent it while rewriting the doc.<br>
	 * Ant HTMLElementNode is acceptable here, even the special nodes. These special node just represents the wrapping
	 * nodes around the 'foreign' nodes that exist as their children (nodes produced from the RHTML parser and JS
	 * parser, for example).<br>
	 * This behavior allows the inner child of these HTMLSpecialNodes to be processed in the
	 * {@link #addNode(IParseNode)} method and produce a FormatterSpecialElementNode.<br>
	 * 
	 * @param node
	 * @return FormatterBlockWithBeginEndNode sub-classing instance
	 */
	private void pushFormatterNode(CSSNode node)
	{
		CSSRuleNode ruleNode = (CSSRuleNode) node;
		FormatterBlockWithBeginNode formatterRuleNode = new FormatterCSSRuleNode(document, ruleNode.getNameNode()
				.getName().toLowerCase());

		CSSSelectorNode[] selectors = ruleNode.getSelectors();
//		int selectorsEndOffset = getBlockStartOffset(selectors[selectors.length - 1].getEndingOffset() + 1,
//				document);
		int blockStartOffset = getBlockStartOffset(selectors[selectors.length - 1].getEndingOffset() + 1,
				document);

		CSSDeclarationNode[] declarations = ruleNode.getDeclarations();
		formatterRuleNode.setBegin(createTextNode(document, ruleNode.getStartingOffset(), blockStartOffset));
		push(formatterRuleNode);

		checkedPop(formatterRuleNode, -1);

		// TODO: need to change the type of element
		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document, StringUtil.EMPTY);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));
		formatterBlockNode.setEnd(createTextNode(document, ruleNode.getEndingOffset(), ruleNode.getEndingOffset() + 1));
		push(formatterBlockNode);

		if (declarations != null && declarations.length != 0)
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1,
					declarations[0].getStartingOffset()));
		} else
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1, ruleNode.getEndingOffset()));
		}

		for (int i = 0; i < declarations.length; ++i)
		{

			CSSDeclarationNode declarationNode = declarations[i];
			String type = declarationNode.getNameNode().getName().toLowerCase();
			FormatterBlockWithBeginNode formatterDeclarationNode = new FormatterCSSDeclarationNode(document, type);

			formatterDeclarationNode.setBegin(createTextNode(document, declarationNode.getStartingOffset(),
					declarationNode.getEndingOffset() + 1));
			push(formatterDeclarationNode);

			checkedPop(formatterDeclarationNode, -1);

			if (i + 1 < declarations.length)
			{
				formatterBlockNode.addChild(createTextNode(document, declarations[i].getEndingOffset() + 1,
						declarations[i + 1].getStartingOffset()));
			}
			else
			{
				formatterBlockNode.addChild(createTextNode(document, declarations[i].getEndingOffset() + 1,
						ruleNode.getEndingOffset()));
			}
		}

		checkedPop(formatterBlockNode, -1);

	}

	private int getBlockStartOffset(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (document.charAt(offset) == '{')
			{
				break;
			}
			offset++;
		}
		return offset;
	}

}
