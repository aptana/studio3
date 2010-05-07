package com.aptana.editor.html.contentassist.index;

import org.eclipse.core.runtime.Platform;

public interface HTMLIndexConstants
{
	// general constants
	static final String DELIMITER = Platform.inDevelopmentMode() ? "~" : "\0"; //$NON-NLS-1$ //$NON-NLS-2$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA = "html.metadata"; //$NON-NLS-1$
	static final String CORE = "HTML Core"; //$NON-NLS-1$
	
	// index categories
	static final String ELEMENT = "html.element"; //$NON-NLS-1$
	static final String ATTRIBUTE = "html.attribute"; //$NON-NLS-1$
	static final String EVENT = "html.event"; //$NON-NLS-1$
	static final String USER_AGENT = "html.user_agent"; //$NON-NLS-1$
	
	static final String RESOURCE_CSS = "html.resource.css"; //$NON-NLS-1$
	static final String RESOURCE_JS = "html.resource.js"; //$NON-NLS-1$
}
