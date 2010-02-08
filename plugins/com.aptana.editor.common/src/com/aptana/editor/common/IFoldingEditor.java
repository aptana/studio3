package com.aptana.editor.common;

import java.util.List;

import org.eclipse.jface.text.Position;

public interface IFoldingEditor
{

	public abstract void updateFoldingStructure(List<Position> positions);

}