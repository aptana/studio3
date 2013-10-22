/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.director;

import java.util.*;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.director.*;
import org.eclipse.equinox.internal.p2.director.Explanation.IUInstalled;
import org.eclipse.equinox.internal.p2.director.Explanation.IUToInstall;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.osgi.util.NLS;

public class RequestStatus extends Status {
	public static final byte ADDED = 0;
	public static final byte REMOVED = 1;

	private byte initialRequestType;
	private IInstallableUnit iu;
	private Set<Explanation> explanation;
	private Explanation detailedExplanation;
	private Set<IInstallableUnit> conflictingRootIUs;
	private Set<IInstallableUnit> conflictingInstalledIUs;

	public RequestStatus(IInstallableUnit iu, byte initialRequesType, int severity, Set<Explanation> explanation) {
		super(severity, DirectorActivator.PI_DIRECTOR, NLS.bind(Messages.RequestStatus_message, iu));
		this.iu = iu;
		this.initialRequestType = initialRequesType;
		this.explanation = explanation;
		conflictingRootIUs = new HashSet<IInstallableUnit>();
		conflictingInstalledIUs = new HashSet<IInstallableUnit>();
		if (explanation != null) {
			Iterator<Explanation> iterator = explanation.iterator();
			Explanation o = null;
			while (iterator.hasNext() && ((o = iterator.next()) instanceof Explanation.IUToInstall)) {
				conflictingRootIUs.add(((IUToInstall) o).iu);
			}
			if (o instanceof Explanation.IUInstalled) {
				conflictingInstalledIUs.add(((IUInstalled) o).iu);
				while (iterator.hasNext() && ((o = iterator.next()) instanceof Explanation.IUInstalled)) {
					conflictingInstalledIUs.add(((IUInstalled) o).iu);
				}
			}
			detailedExplanation = o;
		}
	}

	public byte getInitialRequestType() {
		return initialRequestType;
	}

	public IInstallableUnit getIu() {
		return iu;
	}

	//Return the already installed roots with which this IU is in conflict
	//Return an empty set if there is no conflict
	public Set<IInstallableUnit> getConflictsWithInstalledRoots() {
		return conflictingRootIUs;
	}

	//Return the already installed roots with which this IU is in conflict
	//Return an empty set if there is no conflict
	public Set<IInstallableUnit> getConflictsWithAnyRoots() {
		return conflictingInstalledIUs;
	}

	//Return an explanation as to why this IU can not be resolved.
	public Set<Explanation> getExplanations() {
		//To start with, this does not have to return the most specific explanation. If it simply returns an global explanation it is good enough.
		return explanation;
	}

	public int getShortExplanation() {
		return detailedExplanation.shortAnswer();
	}

	public Explanation getExplanationDetails() {
		return detailedExplanation;
	}
}
