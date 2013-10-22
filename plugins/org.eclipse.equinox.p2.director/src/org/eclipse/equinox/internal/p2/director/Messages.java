/*******************************************************************************
 *  Copyright (c) 2007, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.equinox.internal.p2.director.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundles
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}

	public static String Director_Task_installer_plan;
	public static String Director_Task_Installing;
	public static String Director_Task_Updating;
	public static String Director_Task_Resolving_Dependencies;
	public static String Director_Unsatisfied_Dependencies;
	public static String Director_error_applying_configuration;
	public static String Director_For_Target;
	public static String Director_For_Target_Unselect_Required;

	public static String Explanation_alreadyInstalled;
	public static String Explanation_from;
	public static String Explanation_fromPatch;
	public static String Explanation_hardDependency;
	public static String Explanation_patchedHardDependency;
	public static String Explanation_missingRequired;
	public static String Explanation_missingRootRequired;
	public static String Explanation_missingNonGreedyRequired;
	public static String Explanation_missingRequiredFilter;
	public static String Explanation_missingRootFilter;
	public static String Explanation_optionalDependency;
	public static String Explanation_rootMissing;
	public static String Explanation_rootSingleton;
	public static String Explanation_singleton;
	public static String Explanation_to;
	public static String Explanation_toInstall;
	public static String Explanation_unsatisfied;

	public static String Planner_Timeout;
	public static String Planner_Problems_resolving_plan;
	public static String Planner_Unsatisfiable_problem;
	public static String Planner_Unsatisfied_dependency;
	public static String Planner_NoSolution;
	public static String Planner_Unexpected_problem;
	public static String Planner_actions_and_software_incompatible;
	public static String Planner_can_not_install_preq;
	public static String Planner_no_profile_registry;
	public static String Planner_profile_out_of_sync;
	public static String RequestStatus_message;
	public static String Planner_no_installer_agent;

}
