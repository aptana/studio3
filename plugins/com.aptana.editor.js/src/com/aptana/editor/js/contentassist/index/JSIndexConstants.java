package com.aptana.editor.js.contentassist.index;

public interface JSIndexConstants
{
	static final String PREFIX = "js."; //$NON-NLS-1$

	// general constants
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA = PREFIX + "metadata"; //$NON-NLS-1$
	static final String CORE = "JS Core"; //$NON-NLS-1$

	// index categories
	static final String TYPE = PREFIX + "type"; //$NON-NLS-1$
	static final String FUNCTION = PREFIX + "function"; //$NON-NLS-1$
	static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$
	static final String DESCRIPTION = PREFIX + "description"; //$NON-NLS-1$
	static final String PARAMETERS = PREFIX + "parameters"; //$NON-NLS-1$
	static final String RETURN_TYPES = PREFIX + "return_types"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$
	static final String SINCE_LIST = PREFIX + "since_list"; //$NON-NLS-1$
	static final String EXAMPLES = PREFIX + "examples"; //$NON-NLS-1$

	// special values
	static final String NO_ENTRY = "-1"; //$NON-NLS-1$
}
