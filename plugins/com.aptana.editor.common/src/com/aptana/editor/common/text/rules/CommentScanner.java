package com.aptana.editor.common.text.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.tasks.TaskTag;

public class CommentScanner extends RuleBasedScanner
{

	private static final String TASK_TAG_SCOPE = "keyword.other.documentation.task"; //$NON-NLS-1$

	public CommentScanner(IToken defaultToken)
	{
		super();
		setDefaultReturnToken(defaultToken);
		WordRule wordRule = new WordRule(new WordDetector(), Token.UNDEFINED, !isCaseSensitive());
		IToken taskToken = new Token(TASK_TAG_SCOPE);
		for (TaskTag tag : getTaskTags())
		{
			wordRule.addWord(tag.getName(), taskToken);
		}
		setRules(new IRule[] { wordRule });
	}

	public static boolean isCaseSensitive()
	{
		IScopeContext[] contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true, contexts);
	}

	public static Collection<TaskTag> getTaskTags()
	{
		IScopeContext[] contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		String rawTagNames = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAG_NAMES, null, contexts);
		String rawTagPriorities = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAG_PRIORITIES, null, contexts);
		List<TaskTag> tags = createTaskTags(rawTagNames, rawTagPriorities);
		return tags;
	}

	private static List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
	{
		List<TaskTag> tags = new ArrayList<TaskTag>();
		String[] tagNames = rawTagNames.split(",");
		String[] tagPriorities = rawTagPriorities.split(",");
		for (int i = 0; i < tagNames.length; i++)
		{
			tags.add(new TaskTag(tagNames[i], tagPriorities[i]));
		}
		return tags;
	}

}
