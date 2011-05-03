package com.aptana.editor.yaml.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.yaml.parsing.NodeTupleNode;
import com.aptana.parsing.ast.IParseNode;

public class YAMLFoldingComputer extends AbstractFoldingComputer
{

	public YAMLFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	public boolean isFoldable(IParseNode child)
	{
		return child instanceof NodeTupleNode;
	}

}
