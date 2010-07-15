package com.aptana.editor.css.contentassist.index;

public interface CSSIndexConstants
{
	static final String PREFIX = "css."; //$NON-NLS-1$

	// general constants
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA = PREFIX + "metadata"; //$NON-NLS-1$
	static final String CORE = "CSS Core"; //$NON-NLS-1$

	// index categories
	static final String ELEMENT = PREFIX + "element"; //$NON-NLS-1$
	static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$
	static final String VALUE = PREFIX + "value"; //$NON-NLS-1$

	static final String CLASS = PREFIX + "class"; //$NON-NLS-1$
	static final String IDENTIFIER = PREFIX + "identifier"; //$NON-NLS-1$
	static final String COLOR = PREFIX + "color"; //$NON-NLS-1$
}
