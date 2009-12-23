package com.aptana.editor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.theme.IThemeManager;

public class RubyRegexpScanner extends BufferedRuleBasedScanner
{

	public RubyRegexpScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new RegexpRule("\\\\.", getToken("constant.character.escape.ruby"))); //$NON-NLS-1$ //$NON-NLS-2$
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(getToken("string.regexp.ruby")); //$NON-NLS-1$
	}

	protected IToken getToken(String tokenName)
	{
		return getThemeManager().getToken(tokenName);
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}
}
