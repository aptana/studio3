/*******************************************************************************
 *  Copyright (c) 2008, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *      Sonatype Inc - Refactoring
 *******************************************************************************/
package org.eclipse.equinox.p2.planner;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;

/**
 * Helper method to decide on the way the installable units are being included.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @since 2.0
 */
public class ProfileInclusionRules {
	private ProfileInclusionRules() {
		//Can't instantiate profile inclusion rules
	}

	/**
	 * Returns an inclusion rule to strictly install the given installable unit. Strictly
	 * installed installable units will never be uninstalled in order to satisfy a
	 * later profile change request. That is, when there is a dependency conflict
	 * between a strictly installed unit and a non-strict unit, the strictly installed
	 * installable unit will take precedence.
	 * 
	 * @param iu the installable unit to be installed.
	 * @return an opaque token to be passed to the {@link IProfileChangeRequest#setInstallableUnitInclusionRules(IInstallableUnit, String)}
	 */
	public static String createStrictInclusionRule(IInstallableUnit iu) {
		return "STRICT"; //$NON-NLS-1$
	}

	/**
	 * Returns an inclusion rule to optionally install the given installable unit. An optionally
	 * installed installable unit will automatically be removed from the profile if any of
	 * its dependencies become unsatisfied.
	 * 
	 * @param iu the installable unit to be installed.
	 * @return an opaque token to be passed to the {@link IProfileChangeRequest#setInstallableUnitInclusionRules(IInstallableUnit, String)}
	 */
	public static String createOptionalInclusionRule(IInstallableUnit iu) {
		return "OPTIONAL"; //$NON-NLS-1$
	}
}
