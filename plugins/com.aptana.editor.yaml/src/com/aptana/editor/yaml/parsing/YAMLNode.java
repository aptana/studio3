/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.parsing;

import com.aptana.editor.yaml.IYAMLConstants;
import com.aptana.parsing.ast.ParseNode;

/**
 * @author cwilliams
 */
abstract class YAMLNode extends ParseNode
{

	public String getLanguage()
	{
		return IYAMLConstants.CONTENT_TYPE_YAML;
	}

}
