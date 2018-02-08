package com.aptana.editor.common.text.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.core.resources.TaskTag;

public class CommentScanner extends RuleBasedScanner
{

	private static final String TASK_TAG_SCOPE = "keyword.other.documentation.task"; //$NON-NLS-1$

	public CommentScanner(IToken defaultToken)
	{
		super();
		setDefaultReturnToken(defaultToken);
		List<IRule> rules = createRules();
		setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected List<IRule> createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();
		WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED, !TaskTag.isCaseSensitive());
		IToken taskToken = new Token(TASK_TAG_SCOPE);
		for (TaskTag tag : TaskTag.getTaskTags())
		{
			wordRule.addWord(tag.getName(), taskToken);
		}
		rules.add(wordRule);
		return rules;
	}

}
