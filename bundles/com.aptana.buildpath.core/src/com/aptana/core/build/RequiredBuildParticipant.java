/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

/**
 * This class is intended to be extended by build participants that we do not want the user to be able to disable.
 * 
 * @author cwilliams
 */
public abstract class RequiredBuildParticipant extends AbstractBuildParticipant
{

	@Override
	public boolean isRequired()
	{
		return true;
	}

}
