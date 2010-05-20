package com.aptana.editor.html.contentassist.index;

public interface HTMLIndexConstants
{
	static final String PREFIX = "html.";

	// general constants
	static final String DELIMITER = "\0";
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA = PREFIX + "metadata"; //$NON-NLS-1$
	static final String CORE = "HTML Core"; //$NON-NLS-1$

	// index categories
	static final String ELEMENT = PREFIX + "element"; //$NON-NLS-1$
	static final String ATTRIBUTE = PREFIX + "attribute"; //$NON-NLS-1$
	static final String EVENT = PREFIX + "event"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$

	static final String RESOURCE_CSS = PREFIX + "resource.css"; //$NON-NLS-1$
	static final String RESOURCE_JS = PREFIX + "resource.js"; //$NON-NLS-1$
}
