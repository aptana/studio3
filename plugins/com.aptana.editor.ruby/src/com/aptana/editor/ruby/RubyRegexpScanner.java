package com.aptana.editor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class RubyRegexpScanner extends BufferedRuleBasedScanner
{

	public RubyRegexpScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new RegexpRule("\\\\(x[0-9a-fA-F]{2}|[0-2][0-7]{0,2}|3[0-6][0-7]|37[0-7]?|[4-7][0-7]?|.)", //$NON-NLS-1$
				getToken("constant.character.escape.ruby"))); //$NON-NLS-1$
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(getToken("string.regexp.ruby")); //$NON-NLS-1$
	}

	protected IToken getToken(String tokenName)
	{
		return getThemeManager().getToken(tokenName);
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
}
