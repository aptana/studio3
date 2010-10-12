package com.aptana.editor.common.text.rules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class CommentScanner extends RuleBasedScanner
{

	private static final String TASK_TAG_SCOPE = "keyword.other.documentation.task"; //$NON-NLS-1$
	// TODO Move this to some other class and let users control these!
	/**
	 * Map from The Task tag string to the integer priority for tasks with that tag.
	 */
	public static final Map<String, Integer> DEFAULT_TAGS = new HashMap<String, Integer>();
	static
	{
		DEFAULT_TAGS.put("TODO", IMarker.PRIORITY_NORMAL); //$NON-NLS-1$
		DEFAULT_TAGS.put("FIXME", IMarker.PRIORITY_HIGH); //$NON-NLS-1$
		DEFAULT_TAGS.put("XXX", IMarker.PRIORITY_NORMAL); //$NON-NLS-1$
	}

	public CommentScanner(IToken defaultToken)
	{
		super();
		setDefaultReturnToken(defaultToken);
		WordRule wordRule = new WordRule(new WordDetector());
		IToken taskToken = new Token(TASK_TAG_SCOPE);
		for (String tag : DEFAULT_TAGS.keySet())
		{
			wordRule.addWord(tag, taskToken);
		}
		setRules(new IRule[] { wordRule });
	}

	public static boolean isCaseSensitive()
	{
		// TODO Move this wherever the tags are going and let users control via prefs?
		return true;
	}

}
