package com.aptana.editor.haml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class HAMLScanner extends BufferedRuleBasedScanner
{

	private static final boolean OPTIMIZE_REGEXP_RULES = true;

	public HAMLScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// FIXME Must be at end of line (only \s*\n can follow)
		rules.add(new SingleCharacterRule('|', createToken("punctuation.separator.continuation.haml"))); //$NON-NLS-1$
		// TODO Optimize by turning these into WordRules!
		// tags
		rules.add(new RegexpRule("%[\\w-]+", createToken("entity.name.tag.haml"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$ //$NON-NLS-2$
		// ids
		rules.add(new RegexpRule("#[\\w-]+", createToken("entity.name.tag.id.haml"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$ //$NON-NLS-2$
		// classes
		rules.add(new RegexpRule("\\.[\\w-]+", createToken("entity.name.tag.class.haml"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$ //$NON-NLS-2$
		// escape character FIXME Must be at beginning of line (can only be preceded by spaces*)
		rules.add(new RegexpRule("\\\\.", createToken("meta.escape.haml"), OPTIMIZE_REGEXP_RULES)); //$NON-NLS-1$ //$NON-NLS-2$
		setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected IToken createToken(String string)
	{
		return getThemeManager().getToken(string);
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
}
