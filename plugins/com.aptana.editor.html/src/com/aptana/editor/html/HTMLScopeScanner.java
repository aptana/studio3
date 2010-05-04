package com.aptana.editor.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;

public class HTMLScopeScanner extends HTMLTagScanner
{
	public HTMLScopeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		// add custom rules with higher precedence here
		
		// Add the rules created by the super class first so they have higher
		// precedence
		if (fRules != null)
		{
			rules.addAll(Arrays.asList(fRules));
		}
		
		// add custom rules with lower precedence here
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}
