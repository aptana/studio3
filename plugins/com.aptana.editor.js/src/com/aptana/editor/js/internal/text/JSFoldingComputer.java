package com.aptana.editor.js.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSCommentNode;
import com.aptana.editor.js.parsing.ast.JSForNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParametersNode;
import com.aptana.editor.js.parsing.ast.JSStatementsNode;
import com.aptana.editor.js.parsing.ast.JSSwitchNode;
import com.aptana.parsing.ast.IParseNode;

public class JSFoldingComputer extends AbstractFoldingComputer implements IFoldingComputer
{

	public JSFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	protected boolean isFoldable(IParseNode child)
	{
		return (child instanceof JSCommentNode)
				|| (child instanceof JSFunctionNode)
				|| (child instanceof JSObjectNode)
				|| (child instanceof JSSwitchNode)
				|| (child instanceof JSStatementsNode && !(child.getParent() instanceof JSFunctionNode || child
						.getParent() instanceof JSForNode)) || (child instanceof JSArrayNode)
				|| (child instanceof JSGroupNode) || (child instanceof JSArgumentsNode)
				|| (child instanceof JSParametersNode) || (child instanceof JSForNode);
	}
}
