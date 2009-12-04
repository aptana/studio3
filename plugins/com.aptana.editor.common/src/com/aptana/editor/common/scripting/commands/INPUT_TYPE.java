package com.aptana.editor.common.scripting.commands;

import java.util.HashMap;
import java.util.Map;

public enum INPUT_TYPE  {
	UNDEFINED
	,NONE
	,SELECTION
	,SELECTED_LINES
	,LINE
	,WORD
	,LEFT_CHAR
	,RIGHT_CHAR
	,DOCUMENT
	,INPUT_FROM_CONSOLE;

	private static Map<String, INPUT_TYPE> inputTypeStringToInputType = new HashMap<String, INPUT_TYPE>();
	
	public static INPUT_TYPE getINPUT_TYPE(String inputTypeString) {
		if (inputTypeString != null) {
			INPUT_TYPE inputType = INPUT_TYPE.valueOf(inputTypeString);
			if (inputType == null) {
				inputType = inputTypeStringToInputType.get(inputTypeString);
			}
		}
		return UNDEFINED;
	}
}