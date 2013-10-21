/*******************************************************************************
 * Copyright (c) 2009, 2013 Daniel Le Berre and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *     Daniel Le Berre - initial API and implementation
 *     Red Hat, Inc. - support for remediation page
 ******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import java.math.BigInteger;
import java.util.*;
import org.eclipse.equinox.internal.p2.director.Projector.AbstractVariable;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.query.*;
import org.sat4j.pb.tools.*;
import org.sat4j.specs.ContradictionException;

public class UserDefinedOptimizationFunction extends OptimizationFunction {
	private Collection<IInstallableUnit> alreadyExistingRoots;
	private SteppedTimeoutLexicoHelper<Object, Explanation> dependencyHelper;
	private IQueryable<IInstallableUnit> picker;

	public UserDefinedOptimizationFunction(IQueryable<IInstallableUnit> lastState, List<AbstractVariable> abstractVariables, List<AbstractVariable> optionalVariables, IQueryable<IInstallableUnit> picker, IInstallableUnit selectionContext, Map<String, Map<Version, IInstallableUnit>> slice, DependencyHelper<Object, Explanation> dependencyHelper, Collection<IInstallableUnit> alreadyInstalledIUs) {
		super(lastState, abstractVariables, optionalVariables, picker, selectionContext, slice);
		this.picker = picker;
		this.slice = slice;
		this.dependencyHelper = (SteppedTimeoutLexicoHelper<Object, Explanation>) dependencyHelper;
		this.alreadyExistingRoots = alreadyInstalledIUs;
	}

	public List<WeightedObject<? extends Object>> createOptimizationFunction(IInstallableUnit metaIu, Collection<IInstallableUnit> newRoots) {
		List<WeightedObject<?>> weightedObjects = new ArrayList<WeightedObject<?>>();
		List<Object> objects = new ArrayList<Object>();
		BigInteger weight = BigInteger.valueOf(slice.size() + 1);
		String[] criteria = new String[] {"+new", "-notuptodate", "-changed", "-removed"}; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
		BigInteger currentWeight = weight.pow(criteria.length - 1);
		boolean maximizes;
		Object thing;
		for (int i = 0; i < criteria.length; i++) {
			if (criteria[i].endsWith("new")) { //$NON-NLS-1$
				weightedObjects.clear();
				newRoots(weightedObjects, criteria[i].startsWith("+") ? currentWeight.negate() : currentWeight, metaIu); //$NON-NLS-1$
				currentWeight = currentWeight.divide(weight);
			} else if (criteria[i].endsWith("removed")) { //$NON-NLS-1$
				weightedObjects.clear();
				removedRoots(weightedObjects, criteria[i].startsWith("+") ? currentWeight.negate() : currentWeight, metaIu); //$NON-NLS-1$
				currentWeight = currentWeight.divide(weight);
			} else if (criteria[i].endsWith("notuptodate")) { //$NON-NLS-1$
				weightedObjects.clear();
				notuptodate(weightedObjects, criteria[i].startsWith("+") ? currentWeight.negate() : currentWeight, metaIu); //$NON-NLS-1$
				currentWeight = currentWeight.divide(weight);
			} else if (criteria[i].endsWith("changed")) { //$NON-NLS-1$
				weightedObjects.clear();
				changedRoots(weightedObjects, criteria[i].startsWith("+") ? currentWeight.negate() : currentWeight, metaIu); //$NON-NLS-1$
				currentWeight = currentWeight.divide(weight);
			}
			objects.clear();
			maximizes = criteria[i].startsWith("+"); //$NON-NLS-1$
			for (Iterator<WeightedObject<?>> it = weightedObjects.iterator(); it.hasNext();) {
				thing = it.next().thing;
				if (maximizes) {
					thing = dependencyHelper.not(thing);
				}
				objects.add(thing);
			}
			dependencyHelper.addCriterion(objects);
		}
		weightedObjects.clear();
		return null;
	}

	protected void changedRoots(List<WeightedObject<?>> weightedObjects, BigInteger weight, IInstallableUnit entryPointIU) {
		Collection<IRequirement> requirements = entryPointIU.getRequirements();
		for (IRequirement req : requirements) {
			IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(req.getMatches());
			IQueryResult<IInstallableUnit> matches = picker.query(query, null);
			Object[] changed = new Object[matches.toUnmodifiableSet().size()];
			int i = 0;
			for (IInstallableUnit match : matches) {
				changed[i++] = isInstalledAsRoot(match) ? dependencyHelper.not(match) : match;
			}
			try {
				Projector.AbstractVariable abs = new Projector.AbstractVariable("CHANGED"); //$NON-NLS-1$
				dependencyHelper.or(FakeExplanation.getInstance(), abs, changed);
				weightedObjects.add(WeightedObject.newWO(abs, weight));
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block TODO
				e.printStackTrace();
			}
		}
	}

	protected void newRoots(List<WeightedObject<?>> weightedObjects, BigInteger weight, IInstallableUnit entryPointIU) {
		Collection<IRequirement> requirements = entryPointIU.getRequirements();
		for (IRequirement req : requirements) {
			IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(req.getMatches());
			IQueryResult<IInstallableUnit> matches = picker.query(query, null);
			boolean oneInstalled = false;
			for (IInstallableUnit match : matches) {
				oneInstalled = oneInstalled || isInstalledAsRoot(match);
			}
			if (!oneInstalled) {
				try {
					Projector.AbstractVariable abs = new Projector.AbstractVariable("NEW"); //$NON-NLS-1$
					dependencyHelper.or(FakeExplanation.getInstance(), abs, matches.toArray(IInstallableUnit.class));
					weightedObjects.add(WeightedObject.newWO(abs, weight));
				} catch (ContradictionException e) {
					// should not happen
					e.printStackTrace();
				}
			}
		}
	}

	protected void removedRoots(List<WeightedObject<?>> weightedObjects, BigInteger weight, IInstallableUnit entryPointIU) {
		Collection<IRequirement> requirements = entryPointIU.getRequirements();
		for (IRequirement req : requirements) {
			IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(req.getMatches());
			IQueryResult<IInstallableUnit> matches = picker.query(query, null);
			boolean installed = false;
			Object[] literals = new Object[matches.toUnmodifiableSet().size()];
			int i = 0;
			for (IInstallableUnit match : matches) {
				installed = installed || isInstalledAsRoot(match);
				literals[i++] = dependencyHelper.not(match);
			}
			if (installed) {
				try {
					Projector.AbstractVariable abs = new Projector.AbstractVariable("REMOVED"); //$NON-NLS-1$
					dependencyHelper.and(FakeExplanation.getInstance(), abs, literals);
					weightedObjects.add(WeightedObject.newWO(abs, weight));
				} catch (ContradictionException e) {
					// should not happen TODO
					e.printStackTrace();
				}
			}
		}
	}

	protected void notuptodate(List<WeightedObject<?>> weightedObjects, BigInteger weight, IInstallableUnit entryPointIU) {
		Collection<IRequirement> requirements = entryPointIU.getRequirements();
		for (IRequirement req : requirements) {
			IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(req.getMatches());
			IQueryResult<IInstallableUnit> matches = picker.query(query, null);
			List<IInstallableUnit> toSort = new ArrayList<IInstallableUnit>(matches.toUnmodifiableSet());
			Collections.sort(toSort, Collections.reverseOrder());
			if (toSort.size() == 0)
				continue;

			Projector.AbstractVariable abs = new Projector.AbstractVariable();
			Object notlatest = dependencyHelper.not(toSort.get(0));
			try {
				// notuptodate <=> not iuvn and (iuv1 or iuv2 or ... iuvn-1) 
				dependencyHelper.implication(new Object[] {abs}).implies(notlatest).named(FakeExplanation.getInstance());
				Object[] clause = new Object[toSort.size()];
				toSort.toArray(clause);
				clause[0] = dependencyHelper.not(abs);
				dependencyHelper.clause(FakeExplanation.getInstance(), clause);
				for (int i = 1; i < toSort.size(); i++) {
					dependencyHelper.implication(new Object[] {notlatest, toSort.get(i)}).implies(abs).named(FakeExplanation.getInstance());
				}
			} catch (ContradictionException e) {
				// should never happen
				e.printStackTrace();
			}

			weightedObjects.add(WeightedObject.newWO(abs, weight));
		}
	}

	private static class FakeExplanation extends Explanation {
		private static Explanation singleton = new FakeExplanation();

		public static Explanation getInstance() {
			return singleton;
		}

		protected int orderValue() {
			return Explanation.OTHER_REASON;
		}

		@Override
		public int shortAnswer() {
			return 0;
		}

	}

	private boolean isInstalledAsRoot(IInstallableUnit isInstalled) {
		for (IInstallableUnit installed : alreadyExistingRoots) {
			if (isInstalled.equals(installed))
				return true;
		}
		return false;
	}

}
