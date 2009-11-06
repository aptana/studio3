package com.aptana.radrails.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;

import com.aptana.radrails.editor.common.RegexpRule;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class JSDoubleQuotedStringScanner extends BufferedRuleBasedScanner
{

	public JSDoubleQuotedStringScanner()
	{
		super();

		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new RegexpRule("\\\\(x[0-9a-fA-F]{2}|[0-2][0-7]{0,2}|3[0-6][0-7]|37[0-7]?|[4-7][0-7]?|.)", ThemeUtil.getToken("constant.character.escape.js")));
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(ThemeUtil.getToken("string.quoted.double.js"));
	}

}
