package com.aptana.editor.json.internal.text;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.parsing.ast.JSONArrayNode;
import com.aptana.editor.json.parsing.ast.JSONObjectNode;
import com.aptana.editor.json.preferences.IPreferenceConstants;
import com.aptana.parsing.ast.IParseNode;

public class JSONFoldingComputer extends AbstractFoldingComputer
{

	public JSONFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	public boolean isFoldable(IParseNode child)
	{
		return (child instanceof JSONObjectNode) || (child instanceof JSONArrayNode);
	}

	@Override
	public boolean isCollapsed(IParseNode child)
	{
		if (child instanceof JSONObjectNode)
		{
			return Platform.getPreferencesService().getBoolean(JSONPlugin.PLUGIN_ID,
					IPreferenceConstants.INITIALLY_FOLD_OBJECTS, false, null);
		}
		if (child instanceof JSONArrayNode)
		{
			return Platform.getPreferencesService().getBoolean(JSONPlugin.PLUGIN_ID,
					IPreferenceConstants.INITIALLY_FOLD_ARRAYS, false, null);
		}
		return false;
	}
}
