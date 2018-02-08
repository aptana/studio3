/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.dtd.text.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;

import com.aptana.dtd.core.parsing.DTDTokenType;

/**
 * @author Max Stepanov
 *
 */
public class DTDSourceScanner extends DTDTagScanner {

	/**
	 * 
	 */
	public DTDSourceScanner() {
		super();
		List<IRule> rules = new ArrayList<IRule>();
		
		// Already handled by partitioning, but we need this for the parser
		rules.add(new MultiLineRule("<!--", "-->", createToken(DTDTokenType.COMMENT), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$

		// TODO: This should require Name directly after the opening <? and it
		// should reject <?xml
		rules.add(new MultiLineRule("<?", "?>", createToken(DTDTokenType.PI), '\0', true)); //$NON-NLS-1$ //$NON-NLS-2$

		rules.addAll(Arrays.asList(fRules));
		setRules(rules.toArray(new IRule[rules.size()]));
	}

}
