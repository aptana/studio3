package com.aptana.editor.css.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.css.parsing.ast.CSSCommentNode;
import com.aptana.editor.css.parsing.ast.CSSFontFaceNode;
import com.aptana.editor.css.parsing.ast.CSSMediaNode;
import com.aptana.editor.css.parsing.ast.CSSPageNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CSSFoldingComputer extends AbstractFoldingComputer implements IFoldingComputer
{

	public CSSFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	protected boolean isFoldable(IParseNode child)
	{
		return (child instanceof CSSCommentNode) || (child instanceof CSSRuleNode) || (child instanceof CSSMediaNode)
				|| (child instanceof CSSPageNode) || (child instanceof CSSFontFaceNode);
	}

	protected IParseNode[] getChildren(IParseNode parseNode)
	{
		if (parseNode instanceof CSSMediaNode)
		{
			CSSMediaNode mediaNode = (CSSMediaNode) parseNode;
			return mediaNode.getStatements();
		}
		return super.getChildren(parseNode);
	}

	@Override
	protected boolean traverseInto(IParseNode child)
	{
		return (child instanceof CSSMediaNode)
				|| (((child instanceof ParseRootNode) || (child instanceof CSSPageNode)) && child.hasChildren());
	}
}
