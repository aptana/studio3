package com.aptana.editor.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;

import com.aptana.editor.common.RegexpRule;
import com.aptana.editor.common.theme.ThemeUtil;

public class JSSingleQuotedStringScanner extends BufferedRuleBasedScanner {

    public JSSingleQuotedStringScanner() {
        super();

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new RegexpRule(
                "\\\\(x[0-9a-fA-F]{2}|[0-2][0-7]{0,2}|3[0-6][0-7]|37[0-7]?|[4-7][0-7]?|.)", //$NON-NLS-1$
                ThemeUtil.getToken("constant.character.escape.js"))); //$NON-NLS-1$
        setRules(rules.toArray(new IRule[rules.size()]));

        setDefaultReturnToken(ThemeUtil.getToken("string.quoted.single.js")); //$NON-NLS-1$
    }

}
