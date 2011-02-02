package com.aptana.editor.xml.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.xml.parsing.ast.XMLNode;
import com.aptana.parsing.ast.IParseNode;

public class XMLFoldingComputer extends AbstractFoldingComputer implements IFoldingComputer
{

	public XMLFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	protected boolean isFoldable(IParseNode child)
	{
		return child instanceof XMLNode;
	}
}
