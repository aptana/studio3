package com.aptana.radrails.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;

import com.aptana.radrails.editor.common.RegexpRule;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class JSRegexpScanner extends BufferedRuleBasedScanner
{

	public JSRegexpScanner()
	{
		super();

		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new RegexpRule("\\\\.", ThemeUtil.getToken("constant.character.escape.js")));
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(ThemeUtil.getToken("string.regexp.js"));
	}
}
