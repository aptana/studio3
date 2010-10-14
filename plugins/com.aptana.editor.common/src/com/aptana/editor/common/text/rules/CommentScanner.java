package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.tasks.TaskTag;

public class CommentScanner extends RuleBasedScanner
{

	private static final String TASK_TAG_SCOPE = "keyword.other.documentation.task"; //$NON-NLS-1$

	public CommentScanner(IToken defaultToken)
	{
		super();
		setDefaultReturnToken(defaultToken);
		WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED, !TaskTag.isCaseSensitive());
		IToken taskToken = new Token(TASK_TAG_SCOPE);
		for (TaskTag tag : TaskTag.getTaskTags())
		{
			wordRule.addWord(tag.getName(), taskToken);
		}
		setRules(new IRule[] { wordRule });
	}

}
