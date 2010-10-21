/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;

public class EmptyCommentRule extends WordRule implements IPredicateRule
{
	private IToken fSuccessToken;

	/**
	 * EmptyCommentRule
	 * 
	 * @param successToken
	 */
	public EmptyCommentRule(IToken successToken)
	{
		super(new EmptyCommentDetector());
		fSuccessToken = successToken;
		addWord("/**/", fSuccessToken); //$NON-NLS-1$
	}

	/**
	 * evaluate
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		return super.evaluate(scanner);
	}

	/**
	 * getSuccessToken
	 */
	public IToken getSuccessToken()
	{
		return fSuccessToken;
	}
}