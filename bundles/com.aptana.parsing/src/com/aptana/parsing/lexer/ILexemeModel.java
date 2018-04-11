/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.lexer;

public interface ILexemeModel {

    /**
     * Gets the lexeme at the specific offset.
     * 
     * @param offset
     *            the offset to locate the lexeme
     * @return the lexeme at the given offset, or null if no lexeme is found
     */
    public ILexeme getLexemeFromOffset(int offset);
}
