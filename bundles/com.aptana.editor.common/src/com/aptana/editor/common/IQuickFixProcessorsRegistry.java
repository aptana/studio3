/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;

/**
 * Registry that maintains contributions for the quickFix processors for a content type.
 * 
 * @author pinnamuri
 */
public interface IQuickFixProcessorsRegistry
{

	public IQuickAssistProcessor getQuickFixProcessor(String contentType);

}