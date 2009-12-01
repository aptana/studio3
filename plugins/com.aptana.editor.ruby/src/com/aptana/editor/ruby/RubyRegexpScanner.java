package com.aptana.editor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;

import com.aptana.editor.common.RegexpRule;
import com.aptana.editor.common.theme.ThemeUtil;

public class RubyRegexpScanner extends BufferedRuleBasedScanner {

    public RubyRegexpScanner() {
        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new RegexpRule("\\\\.", ThemeUtil.getToken("constant.character.escape.ruby"))); //$NON-NLS-1$ //$NON-NLS-2$
        setRules(rules.toArray(new IRule[rules.size()]));

        setDefaultReturnToken(ThemeUtil.getToken("string.regexp.ruby")); //$NON-NLS-1$
    }
}
