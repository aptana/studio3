package com.aptana.editor.json.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.json.parsing.ast.JSONArrayNode;
import com.aptana.editor.json.parsing.ast.JSONObjectNode;
import com.aptana.parsing.ast.IParseNode;

public class JSONFoldingComputer extends AbstractFoldingComputer implements IFoldingComputer
{

	public JSONFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	protected boolean isFoldable(IParseNode child)
	{
		return (child instanceof JSONObjectNode) || (child instanceof JSONArrayNode);
	}
}
