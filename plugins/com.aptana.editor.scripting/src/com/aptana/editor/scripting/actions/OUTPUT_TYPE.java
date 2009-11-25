package com.aptana.editor.scripting.actions;

import java.util.HashMap;
import java.util.Map;

public enum OUTPUT_TYPE {
	UNDEFINED
	,DISCARD
	,REPLACE_SELECTION
	,REPLACE_SELECTED_LINES
	,REPLACE_LINE
	,REPLACE_WORD
	,REPLACE_DOCUMENT
	,INSERT_AS_TEXT
	,INSERT_AS_SNIPPET
	,SHOW_AS_HTML
	,SHOW_AS_TOOLTIP
	,CREATE_NEW_DOCUMENT
	,OUTPUT_TO_CONSOLE;
	
private static Map<String, OUTPUT_TYPE> outputTypeStringToOutputType = new HashMap<String, OUTPUT_TYPE>();
	
	public static OUTPUT_TYPE getOUTPUT_TYPE(String outputTypeString) {
		if (outputTypeString != null) {
			OUTPUT_TYPE outputType = OUTPUT_TYPE.valueOf(outputTypeString);
			if (outputType == null) {
				outputType = outputTypeStringToOutputType.get(outputTypeString);
			}
		}
		return UNDEFINED;
	}
}