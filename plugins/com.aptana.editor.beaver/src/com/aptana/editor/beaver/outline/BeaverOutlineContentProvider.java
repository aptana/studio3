/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import beaver.Symbol;
import beaver.spec.ast.GrammarTreeRoot;

import com.aptana.editor.beaver.parsing.ast.BeaverParseRootNode;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlinePageInput;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.Range;

/**
 * BeaverOutlineContentProvider
 */
public class BeaverOutlineContentProvider implements ITreeContentProvider
{
	class SymbolWrapper extends Range
	{
		private Symbol symbol;

		public SymbolWrapper(Symbol symbol)
		{
			super(symbol.getStart(), symbol.getEnd());

			this.symbol = symbol;
		}

		public Symbol getSymbol()
		{
			return symbol;
		}
	}

	protected static final Object[] EMPTY = new Object[0];

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		return EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] result = EMPTY;

		IParseRootNode root = null;

		if (inputElement instanceof CommonOutlinePageInput)
		{
			root = ((CommonOutlinePageInput) inputElement).ast;
		}
		else if (inputElement instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor editor = (AbstractThemeableEditor) inputElement;
			root = editor.getAST();
		}

		if (root instanceof BeaverParseRootNode)
		{
			GrammarTreeRoot grammarRoot = ((BeaverParseRootNode) root).getRoot();

			// TODO: include declarations?
			result = new Object[grammarRoot.rules.length];

			for (int i = 0; i < result.length; i++)
			{
				result[i] = new SymbolWrapper(grammarRoot.rules[i]);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}
}
