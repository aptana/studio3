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
package com.aptana.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.WhitespaceDetector;
import com.aptana.editor.common.theme.ThemeUtil;

/**
 * A rule based JavaDoc scanner.
 */
public class JSDocScanner extends RuleBasedScanner {
    /**
     * A key word detector.
     */
    static class JSDocWordDetector implements IWordDetector {
        /*
         * (non-Javadoc) Method declared on IWordDetector
         */
        public boolean isWordStart(char c) {
            return (c == '@');
        }

        /*
         * (non-Javadoc) Method declared on IWordDetector
         */
        public boolean isWordPart(char c) {
            return Character.isLetter(c);
        }
    }

    private static String[] KEYWORDS = { "@author", "@deprecated", "@exception", "@param", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "@return", "@see", "@serial", "@serialData", "@serialField", "@since", "@throws", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            "@version" }; //$NON-NLS-1$

    /**
     * Create a new javadoc scanner for the given color provider.
     * 
     */
    public JSDocScanner() {
        super();

        IToken keyword = ThemeUtil.getToken("meta.documentation.tag.js"); //$NON-NLS-1$
        IToken tag = ThemeUtil.getToken("text.html.basic"); //$NON-NLS-1$
        IToken link = ThemeUtil.getToken("markup.underline.link"); //$NON-NLS-1$

        List<IRule> list = new ArrayList<IRule>();

        // Add rule for tags.
        list.add(new SingleLineRule("<", ">", tag)); //$NON-NLS-2$ //$NON-NLS-1$

        // Add rule for links.
        list.add(new SingleLineRule("{", "}", link)); //$NON-NLS-2$ //$NON-NLS-1$

        // Add generic whitespace rule.
        list.add(new WhitespaceRule(new WhitespaceDetector()));

        // Add word rule for keywords.
        WordRule wordRule = new WordRule(new JSDocWordDetector());

        for (String word : KEYWORDS) {
            wordRule.addWord(word, keyword);
        }

        list.add(wordRule);

        setDefaultReturnToken(ThemeUtil.getToken("comment.block.documentation.js")); //$NON-NLS-1$
        setRules(list.toArray(new IRule[list.size()]));
    }
}
