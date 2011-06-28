package com.aptana.editor.dtd.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A key word detector.
 */
/* package */ class DTDWordDetector implements IWordDetector {
	
	/*
	 * (non-Javadoc) Method declared on IWordDetector
	 */
	public boolean isWordStart(char c) {
		return Character.isLetter(c) || c == '<' || c == '#';
	}

	/*
	 * (non-Javadoc) Method declared on IWordDetector
	 */
	public boolean isWordPart(char c) {
		return Character.isLetter(c) || c == '!';
	}
}