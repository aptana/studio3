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

import java.util.*;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.query.*;

public class OperationGenerator {
	private static final IInstallableUnit NULL_IU = MetadataFactory.createResolvedInstallableUnit(MetadataFactory.createInstallableUnit(new MetadataFactory.InstallableUnitDescription()), new IInstallableUnitFragment[0]);
	private final IProvisioningPlan plan;

	public OperationGenerator(IProvisioningPlan plan) {
		this.plan = plan;
	}

	public void generateOperation(Collection<IInstallableUnit> from_, Collection<IInstallableUnit> to_) {
		Collection<IInstallableUnit> intersection = new HashSet<IInstallableUnit>(from_);
		intersection.retainAll(to_);

		HashSet<IInstallableUnit> tmpFrom = new HashSet<IInstallableUnit>(from_);
		HashSet<IInstallableUnit> tmpTo = new HashSet<IInstallableUnit>(to_);
		tmpFrom.removeAll(intersection);
		tmpTo.removeAll(intersection);

		List<IInstallableUnit> from = new ArrayList<IInstallableUnit>(tmpFrom);
		Collections.sort(from);

		List<IInstallableUnit> to = new ArrayList<IInstallableUnit>(tmpTo);
		Collections.sort(to);

		generateUpdates(from, to);
		generateInstallUninstall(from, to);
		generateConfigurationChanges(to_, intersection);
	}

	//This generates operations that are causing the IUs to be reconfigured.
	private void generateConfigurationChanges(Collection<IInstallableUnit> to_, Collection<IInstallableUnit> intersection) {
		if (intersection.size() == 0)
			return;
		//We retain from each set the things that are the same.
		//Note that despite the fact that they are the same, a different CU can be attached.
		//The objects contained in the intersection are the one that were originally in the from collection.
		TreeSet<IInstallableUnit> to = new TreeSet<IInstallableUnit>(to_);
		for (IInstallableUnit fromIU : intersection) {
			IInstallableUnit toIU = to.tailSet(fromIU).first();
			generateConfigurationOperation(fromIU, toIU);
		}

	}

	private void generateConfigurationOperation(IInstallableUnit fromIU, IInstallableUnit toIU) {
		Collection<IInstallableUnitFragment> fromFragments = fromIU.getFragments();
		Collection<IInstallableUnitFragment> toFragments = toIU.getFragments();
		if (fromFragments == toFragments)
			return;
		//Check to see if the two arrays are equals independently of the order of the fragments
		if (fromFragments.size() == toFragments.size() && fromFragments.containsAll(toFragments))
			return;
		plan.updateInstallableUnit(fromIU, toIU);
	}

	private void generateInstallUninstall(List<IInstallableUnit> from, List<IInstallableUnit> to) {
		int toIdx = 0;
		int fromIdx = 0;
		while (fromIdx != from.size() && toIdx != to.size()) {
			IInstallableUnit fromIU = from.get(fromIdx);
			IInstallableUnit toIU = to.get(toIdx);
			int comparison = toIU.compareTo(fromIU);
			if (comparison < 0) {
				plan.addInstallableUnit(toIU);
				toIdx++;
			} else if (comparison == 0) {
				toIdx++;
				fromIdx++;
				//				System.out.println("same " + fromIU);
			} else {
				plan.removeInstallableUnit(fromIU);
				fromIdx++;
			}
		}
		if (fromIdx != from.size()) {
			for (int i = fromIdx; i < from.size(); i++) {
				plan.removeInstallableUnit(from.get(i));
			}
		}
		if (toIdx != to.size()) {
			for (int i = toIdx; i < to.size(); i++) {
				plan.addInstallableUnit(to.get(i));
			}
		}
	}

	private void generateUpdates(List<IInstallableUnit> from, List<IInstallableUnit> to) {
		if (to.isEmpty() || from.isEmpty())
			return;

		Set<IInstallableUnit> processed = new HashSet<IInstallableUnit>();
		Set<IInstallableUnit> removedFromTo = new HashSet<IInstallableUnit>();

		QueryableArray indexedFromElements = new QueryableArray(from.toArray(new IInstallableUnit[from.size()]));
		for (int toIdx = 0; toIdx < to.size(); toIdx++) {
			IInstallableUnit iuTo = to.get(toIdx);
			if (iuTo.getId().equals(next(to, toIdx).getId())) { //This handle the case where there are multiple versions of the same IU in the target. Eg we are trying to update from A 1.0.0 to A 1.1.1 and A 1.2.2
				toIdx = skip(to, iuTo, toIdx) - 1;
				//System.out.println("Can't update " + iuTo + " because another iu with same id is in the target state");
				continue;
			}
			if (iuTo.getUpdateDescriptor() == null)
				continue;

			//TODO we eventually need to handle the case where an IU is a merge of several others.

			IQuery<IInstallableUnit> updateQuery = QueryUtil.createMatchQuery(iuTo.getUpdateDescriptor().getIUsBeingUpdated().iterator().next(), new Object[0]);
			iuTo.getUpdateDescriptor().getIUsBeingUpdated();
			IQueryResult<IInstallableUnit> updates = indexedFromElements.query(updateQuery, null);

			if (updates.isEmpty()) { //Nothing to update from.
				continue;
			}
			Iterator<IInstallableUnit> updatesIterator = updates.iterator();
			IInstallableUnit iuFrom = updatesIterator.next();
			if (updatesIterator.hasNext()) { //There are multiple IUs to update from
				//System.out.println("Can't update  " + iuTo + " because there are multiple IUs to update from (" + toString(iusFrom) + ')');
				continue;
			}
			if (iuTo.equals(iuFrom)) {
				from.remove(iuFrom);
				//				fromIdIndexList.remove(iuFrom);
				removedFromTo.add(iuTo);
				continue;
			}
			plan.updateInstallableUnit(iuFrom, iuTo);
			from.remove(iuFrom);
			//			fromIdIndexList.remove(iuFrom);
			processed.add(iuTo);
		}
		to.removeAll(processed);
		to.removeAll(removedFromTo);
	}

	private IInstallableUnit next(List<IInstallableUnit> l, int i) {
		i++;
		if (i >= l.size())
			return NULL_IU;
		return l.get(i);
	}

	private int skip(List<IInstallableUnit> c, IInstallableUnit id, int idx) {
		int i = idx;
		for (; i < c.size(); i++) {
			if (!id.getId().equals(c.get(i).getId()))
				return i;
		}
		return i;
	}
}
