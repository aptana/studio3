package com.aptana.editor.css.internal.text.rules;


/**
 * Detects words starting with '@' and containing letters or digits
 * 
 * @author Chris Williams
 */
public class AtWordDetector extends IdentifierWithPrefixDetector
{

	public AtWordDetector()
	{
		super('@');
	}
	
}
