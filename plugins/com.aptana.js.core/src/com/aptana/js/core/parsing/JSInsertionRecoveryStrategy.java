/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import com.aptana.parsing.InsertionRecoveryStrategy;

/**
 * InsertionRecoveryStrategy
 */
class JSInsertionRecoveryStrategy extends InsertionRecoveryStrategy<JSTokenType>
{
	/**
	 * JSInsertionRecoveryStrategy
	 * 
	 * @param type1
	 * @param text1
	 * @param type2
	 * @param text2
	 * @param requiredTypes
	 */
	public JSInsertionRecoveryStrategy(JSTokenType type, String text, JSTokenType... requiredTypes)
	{
		super(type, text, requiredTypes);
	}

	/**
	 * JSInsertionRecoveryStrategy
	 * 
	 * @param type1
	 * @param text1
	 * @param type2
	 * @param text2
	 * @param requiredTypes
	 */
	public JSInsertionRecoveryStrategy(JSTokenType type1, String text1, JSTokenType type2, String text2,
			JSTokenType... requiredTypes)
	{
		super(type1, text1, type2, text2, requiredTypes);
	}
}
