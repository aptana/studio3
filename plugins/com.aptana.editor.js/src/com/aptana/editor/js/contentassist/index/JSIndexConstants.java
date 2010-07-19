package com.aptana.editor.js.contentassist.index;

public interface JSIndexConstants
{
	// the content format version of the JS index files
	public static final double INDEX_VERSION = 0.11;
	
	// general constants
	static final String PREFIX = "js."; //$NON-NLS-1$
	static final String METADATA = PREFIX + "metadata"; //$NON-NLS-1$
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
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
	
	static final String[] ALL_CATEGORIES = new String[] {
		TYPE,
		FUNCTION,
		PROPERTY,
		DESCRIPTION,
		PARAMETERS,
		RETURN_TYPES,
		USER_AGENT,
		SINCE_LIST,
		EXAMPLES
	};

	// special values
	static final String NO_ENTRY = "-1"; //$NON-NLS-1$
}
