package com.aptana.radrails.editor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;

import com.aptana.radrails.editor.common.RegexpRule;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

public class RubyRegexpScanner extends BufferedRuleBasedScanner {

    public RubyRegexpScanner() {
        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new RegexpRule("\\\\.", ThemeUtil.getToken("constant.character.escape.rb"))); //$NON-NLS-1$ //$NON-NLS-2$
        setRules(rules.toArray(new IRule[rules.size()]));

        setDefaultReturnToken(ThemeUtil.getToken("string.regexp.rb")); //$NON-NLS-1$
    }
}
