/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *   IBM Corporation - initial API and implementation
 *   Daniel Le Berre - Fix in the encoding and the optimization function
 *   Alban Browaeys - Optimized string concatenation in bug 251357
 *   Jed Anderson - switch from opb files to API calls to DependencyHelper in bug 200380
 *   Sonatype, Inc. - ongoing development
 *   Rapicorp, Inc. - split the optimization function
 *   Red Hat, Inc. - support for remediation page
 ******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import java.util.*;
import java.util.Map.Entry;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.core.helpers.CollectionUtils;
import org.eclipse.equinox.internal.p2.core.helpers.Tracing;
import org.eclipse.equinox.internal.p2.director.Explanation.NotInstallableRoot;
import org.eclipse.equinox.internal.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.metadata.expression.IMatchExpression;
import org.eclipse.equinox.p2.query.*;
import org.eclipse.osgi.util.NLS;
import org.sat4j.minisat.restarts.LubyRestarts;
import org.sat4j.pb.*;
import org.sat4j.pb.core.PBSolverResolution;
import org.sat4j.pb.tools.*;
import org.sat4j.specs.*;

/**
 * This class is the interface between SAT4J and the planner. It produces a
 * boolean satisfiability problem, invokes the solver, and converts the solver result
 * back into information understandable by the planner.
 */
public class Projector {
	/**
	 * The name of a Java system property specifying the timeout to set in the SAT solver.
	 * Note this value is not a time, but rather a conflict count. Essentially the solver
	 * will give up on a particular solution path when this number of conflicts is reached.
	 */
	private static final String PROP_PROJECTOR_TIMEOUT = "eclipse.p2.projector.timeout"; //$NON-NLS-1$
	/**
	 * The default SAT solver timeout (in number of conflicts). See bug 372529 for discussion.
	 */
	private static final int DEFAULT_SOLVER_TIMEOUT = 10000;
	static boolean DEBUG = Tracing.DEBUG_PLANNER_PROJECTOR;
	private static boolean DEBUG_ENCODING = Tracing.DEBUG_PLANNER_PROJECTOR_ENCODING;
	private IQueryable<IInstallableUnit> picker;
	private QueryableArray patches;

	private List<AbstractVariable> allOptionalAbstractRequirements;
	private List<AbstractVariable> abstractVariables;

	private Map<String, Map<Version, IInstallableUnit>> slice; //The IUs that have been considered to be part of the problem

	private IInstallableUnit selectionContext;

	DependencyHelper<Object, Explanation> dependencyHelper;
	private Collection<IInstallableUnit> solution;
	private Collection<Object> assumptions;

	private MultiStatus result;

	private Collection<IInstallableUnit> alreadyInstalledIUs;
	private IQueryable<IInstallableUnit> lastState;

	private boolean considerMetaRequirements;
	private IInstallableUnit entryPoint;
	private Map<IInstallableUnitFragment, Set<IInstallableUnit>> fragments = new HashMap<IInstallableUnitFragment, Set<IInstallableUnit>>();

	//Non greedy things
	private Set<IInstallableUnit> nonGreedyIUs; //All the IUs that would satisfy non greedy dependencies
	private Map<IInstallableUnit, AbstractVariable> nonGreedyVariables = new HashMap<IInstallableUnit, AbstractVariable>();
	private Map<AbstractVariable, List<Object>> nonGreedyProvider = new HashMap<AbstractVariable, List<Object>>(); //Keeps track of all the "object" that provide an IU that is non greedly requested  

	private boolean emptyBecauseFiltered;
	private boolean userDefinedFunction;

	static class AbstractVariable {
		//		private String name;

		public AbstractVariable(String name) {
			//						this.name = name;
		}

		public AbstractVariable() {
			// TODO Auto-generated constructor stub
		}

		public String toString() {
			return "AbstractVariable: " + hashCode(); //$NON-NLS-1$
			//			return name == null ? "AbstractVariable: " + hashCode() : name; //$NON-NLS-1$
		}
	}

	/**
	 * Job for computing SAT failure explanation in the background.
	 */
	class ExplanationJob extends Job {
		private Set<Explanation> explanation;

		public ExplanationJob() {
			super(Messages.Planner_NoSolution);
			//explanations cannot be canceled directly, so don't show it to the user
			setSystem(true);
		}

		public boolean belongsTo(Object family) {
			return family == ExplanationJob.this;
		}

		protected void canceling() {
			super.canceling();
			dependencyHelper.stopExplanation();
		}

		public Set<Explanation> getExplanationResult() {
			return explanation;
		}

		protected IStatus run(IProgressMonitor monitor) {
			long start = 0;
			if (DEBUG) {
				start = System.currentTimeMillis();
				Tracing.debug("Determining cause of failure: " + start); //$NON-NLS-1$
			}
			try {
				explanation = dependencyHelper.why();
				if (DEBUG) {
					long stop = System.currentTimeMillis();
					Tracing.debug("Explanation found: " + (stop - start)); //$NON-NLS-1$
					Tracing.debug("Explanation:"); //$NON-NLS-1$
					for (Explanation ex : explanation) {
						Tracing.debug(ex.toString());
					}
				}
			} catch (TimeoutException e) {
				if (DEBUG)
					Tracing.debug("Timeout while computing explanations"); //$NON-NLS-1$
			} finally {
				//must never have a null result, because caller is waiting on result to be non-null
				if (explanation == null)
					explanation = CollectionUtils.emptySet();
			}
			synchronized (this) {
				ExplanationJob.this.notify();
			}
			return Status.OK_STATUS;
		}

	}

	public Projector(IQueryable<IInstallableUnit> q, Map<String, String> context, Set<IInstallableUnit> nonGreedyIUs, boolean considerMetaRequirements) {
		picker = q;
		slice = new HashMap<String, Map<Version, IInstallableUnit>>();
		selectionContext = InstallableUnit.contextIU(context);
		abstractVariables = new ArrayList<AbstractVariable>();
		allOptionalAbstractRequirements = new ArrayList<AbstractVariable>();
		result = new MultiStatus(DirectorActivator.PI_DIRECTOR, IStatus.OK, Messages.Planner_Problems_resolving_plan, null);
		assumptions = new ArrayList<Object>();
		this.nonGreedyIUs = nonGreedyIUs;
		this.considerMetaRequirements = considerMetaRequirements;
	}

	@SuppressWarnings("unchecked")
	public void encode(IInstallableUnit entryPointIU, IInstallableUnit[] alreadyExistingRoots, IQueryable<IInstallableUnit> installedIUs, Collection<IInstallableUnit> newRoots, IProgressMonitor monitor) {
		alreadyInstalledIUs = Arrays.asList(alreadyExistingRoots);
		lastState = installedIUs;
		this.entryPoint = entryPointIU;
		try {
			long start = 0;
			if (DEBUG) {
				start = System.currentTimeMillis();
				Tracing.debug("Start projection: " + start); //$NON-NLS-1$
			}
			IPBSolver solver;
			if (DEBUG_ENCODING) {
				solver = new UserFriendlyPBStringSolver<Object>();
			} else {
				if (userDefinedFunction) {
					PBSolverResolution mysolver = SolverFactory.newCompetPBResLongWLMixedConstraintsObjectiveExpSimp();
					mysolver.setSimplifier(mysolver.SIMPLE_SIMPLIFICATION);
					mysolver.setRestartStrategy(new LubyRestarts(512));
					solver = mysolver;
				} else {
					solver = SolverFactory.newEclipseP2();
				}
			}
			int timeout = DEFAULT_SOLVER_TIMEOUT;
			String timeoutString = null;
			try {
				// allow the user to specify a longer timeout. 
				// only set the value if it is a positive integer larger than the default.
				// see https://bugs.eclipse.org/336967
				timeoutString = DirectorActivator.context.getProperty(PROP_PROJECTOR_TIMEOUT);
				if (timeoutString != null)
					timeout = Math.max(timeout, Integer.parseInt(timeoutString));
			} catch (Exception e) {
				// intentionally catch all errors (npe, number format, etc)
				// print out to syserr and fall through
				System.err.println("Ignoring user-specified 'eclipse.p2.projector.timeout' value of: " + timeoutString); //$NON-NLS-1$
				e.printStackTrace();
			}
			if (userDefinedFunction)
				solver.setTimeoutOnConflicts(timeout / 4);
			else
				solver.setTimeoutOnConflicts(timeout);

			IQueryResult<IInstallableUnit> queryResult = picker.query(QueryUtil.createIUAnyQuery(), null);
			if (DEBUG_ENCODING) {
				dependencyHelper = new LexicoHelper<Object, Explanation>(solver, false);
				((UserFriendlyPBStringSolver<Object>) solver).setMapping(dependencyHelper.getMappingToDomain());
			} else {
				if (userDefinedFunction)
					dependencyHelper = new SteppedTimeoutLexicoHelper<Object, Explanation>(solver);
				else
					dependencyHelper = new DependencyHelper<Object, Explanation>(solver);
			}
			List<IInstallableUnit> iusToOrder = new ArrayList<IInstallableUnit>(queryResult.toSet());
			Collections.sort(iusToOrder);
			for (Iterator<IInstallableUnit> iusToEncode = iusToOrder.iterator(); iusToEncode.hasNext();) {
				if (monitor.isCanceled()) {
					result.merge(Status.CANCEL_STATUS);
					throw new OperationCanceledException();
				}
				IInstallableUnit iuToEncode = iusToEncode.next();
				if (iuToEncode != entryPointIU) {
					processIU(iuToEncode, false);
				}
			}
			createMustHave(entryPointIU, alreadyExistingRoots);

			createConstraintsForSingleton();

			createConstraintsForNonGreedy();

			createOptimizationFunction(entryPointIU, newRoots);
			if (DEBUG) {
				long stop = System.currentTimeMillis();
				Tracing.debug("Projection complete: " + (stop - start)); //$NON-NLS-1$
			}
			if (DEBUG_ENCODING) {
				System.out.println(solver.toString());
			}
		} catch (IllegalStateException e) {
			result.add(new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, e.getMessage(), e));
		} catch (ContradictionException e) {
			result.add(new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, Messages.Planner_Unsatisfiable_problem));
		}
	}

	private void createConstraintsForNonGreedy() throws ContradictionException {
		for (IInstallableUnit iu : nonGreedyIUs) {
			AbstractVariable var = getNonGreedyVariable(iu);
			List<Object> providers = nonGreedyProvider.get(var);
			if (providers == null || providers.size() == 0) {
				dependencyHelper.setFalse(var, new Explanation.MissingGreedyIU(iu));
			} else {
				createImplication(var, providers, Explanation.OPTIONAL_REQUIREMENT);//FIXME
			}
		}

	}

	private void createOptimizationFunction(IInstallableUnit entryPointIU, Collection<IInstallableUnit> newRoots) {
		if (!userDefinedFunction) {
			createStandardOptimizationFunction(entryPointIU, newRoots);
		} else {
			createUserDefinedOptimizationFunction(entryPointIU, newRoots);
		}
	}

	//Create an optimization function favoring the highest version of each IU
	private void createStandardOptimizationFunction(IInstallableUnit entryPointIU, Collection<IInstallableUnit> newRoots) {
		List<WeightedObject<? extends Object>> weights = new OptimizationFunction(lastState, abstractVariables, allOptionalAbstractRequirements, picker, selectionContext, slice).createOptimizationFunction(entryPointIU, newRoots);
		createObjectiveFunction(weights);
	}

	private void createUserDefinedOptimizationFunction(IInstallableUnit entryPointIU, Collection<IInstallableUnit> newRoots) {
		List<WeightedObject<? extends Object>> weights = new UserDefinedOptimizationFunction(lastState, abstractVariables, allOptionalAbstractRequirements, picker, selectionContext, slice, dependencyHelper, alreadyInstalledIUs).createOptimizationFunction(entryPointIU, newRoots);
		createObjectiveFunction(weights);
	}

	private void createObjectiveFunction(List<WeightedObject<? extends Object>> weightedObjects) {
		if (weightedObjects == null)
			return;
		if (DEBUG) {
			StringBuffer b = new StringBuffer();
			for (WeightedObject<? extends Object> object : weightedObjects) {
				if (b.length() > 0)
					b.append(", "); //$NON-NLS-1$
				b.append(object.getWeight());
				b.append(' ');
				b.append(object.thing);
			}
			Tracing.debug("objective function: " + b); //$NON-NLS-1$
		}
		@SuppressWarnings("unchecked")
		WeightedObject<Object>[] array = (WeightedObject<Object>[]) weightedObjects.toArray(new WeightedObject<?>[weightedObjects.size()]);
		dependencyHelper.setObjectiveFunction(array);
	}

	private void createMustHave(IInstallableUnit iu, IInstallableUnit[] alreadyExistingRoots) throws ContradictionException {
		processIU(iu, true);
		if (DEBUG) {
			Tracing.debug(iu + "=1"); //$NON-NLS-1$
		}
		// dependencyHelper.setTrue(variable, new Explanation.IUToInstall(iu));
		assumptions.add(iu);
	}

	private void createNegation(IInstallableUnit iu, IRequirement req) throws ContradictionException {
		if (DEBUG) {
			Tracing.debug(iu + "=0"); //$NON-NLS-1$
		}
		dependencyHelper.setFalse(iu, new Explanation.MissingIU(iu, req, iu == this.entryPoint));
	}

	// Check whether the requirement is applicable
	private boolean isApplicable(IRequirement req) {
		IMatchExpression<IInstallableUnit> filter = req.getFilter();
		return filter == null || filter.isMatch(selectionContext);
	}

	private boolean isApplicable(IInstallableUnit iu) {
		IMatchExpression<IInstallableUnit> filter = iu.getFilter();
		return filter == null || filter.isMatch(selectionContext);
	}

	private void expandNegatedRequirement(IRequirement req, IInstallableUnit iu, List<AbstractVariable> optionalAbstractRequirements, boolean isRootIu) throws ContradictionException {
		if (!isApplicable(req))
			return;
		List<IInstallableUnit> matches = getApplicableMatches(req);
		if (matches.isEmpty()) {
			return;
		}
		Explanation explanation;
		if (isRootIu) {
			IInstallableUnit reqIu = matches.get(0);
			if (alreadyInstalledIUs.contains(reqIu)) {
				explanation = new Explanation.IUInstalled(reqIu);
			} else {
				explanation = new Explanation.IUToInstall(reqIu);
			}
		} else {
			explanation = new Explanation.HardRequirement(iu, req);
		}
		createNegationImplication(iu, matches, explanation);
	}

	private void determinePotentialHostsForFragment(IInstallableUnit iu) {
		// determine matching hosts only for fragments
		if (!(iu instanceof IInstallableUnitFragment))
			return;

		IInstallableUnitFragment fragment = (IInstallableUnitFragment) iu;
		// for each host requirement, find matches and remember them 
		for (IRequirement req : fragment.getHost()) {
			List<IInstallableUnit> matches = getApplicableMatches(req);
			rememberHostMatches((IInstallableUnitFragment) iu, matches);
		}
	}

	private void expandRequirement(IRequirement req, IInstallableUnit iu, List<AbstractVariable> optionalAbstractRequirements, boolean isRootIu) throws ContradictionException {
		if (req.getMax() == 0) {
			expandNegatedRequirement(req, iu, optionalAbstractRequirements, isRootIu);
			return;
		}
		if (!isApplicable(req))
			return;
		List<IInstallableUnit> matches = getApplicableMatches(req);
		determinePotentialHostsForFragment(iu);
		if (req.getMin() > 0) {
			if (matches.isEmpty()) {
				if (iu == entryPoint && emptyBecauseFiltered) {
					dependencyHelper.setFalse(iu, new NotInstallableRoot(req));
				} else {
					missingRequirement(iu, req);
				}
			} else {
				if (req.isGreedy()) {
					IInstallableUnit reqIu = matches.get(0);
					Explanation explanation;
					if (isRootIu) {
						if (alreadyInstalledIUs.contains(reqIu)) {
							explanation = new Explanation.IUInstalled(reqIu);
						} else {
							explanation = new Explanation.IUToInstall(reqIu);
						}
					} else {
						explanation = new Explanation.HardRequirement(iu, req);
					}
					createImplication(iu, matches, explanation);
					IInstallableUnit current;
					for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
						current = it.next();
						if (nonGreedyIUs.contains(current)) {
							addNonGreedyProvider(getNonGreedyVariable(current), iu);
						}
					}
				} else {
					List<Object> newConstraint = new ArrayList<Object>(matches.size());
					IInstallableUnit current;
					for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
						current = it.next();
						newConstraint.add(getNonGreedyVariable(current));
					}
					createImplication(new Object[] {iu}, newConstraint, new Explanation.HardRequirement(iu, req)); // FIXME
				}
			}
		} else {
			if (!matches.isEmpty()) {
				IInstallableUnit current;
				AbstractVariable abs;
				if (req.isGreedy()) {
					abs = getAbstractVariable(req);
					createImplication(new Object[] {abs, iu}, matches, Explanation.OPTIONAL_REQUIREMENT);
					for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
						current = it.next();
						if (nonGreedyIUs.contains(current)) {
							addNonGreedyProvider(getNonGreedyVariable(current), abs);
						}
					}
					optionalAbstractRequirements.add(abs);
				} else {
					abs = getAbstractVariable(req, false);
					List<Object> newConstraint = new ArrayList<Object>();
					for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
						current = it.next();
						newConstraint.add(getNonGreedyVariable(current));
					}
					createImplication(new Object[] {abs, iu}, newConstraint, Explanation.OPTIONAL_REQUIREMENT);
				}
			}
		}
	}

	private void addNonGreedyProvider(AbstractVariable nonGreedyVariable, Object o) {
		List<Object> providers = nonGreedyProvider.get(nonGreedyVariable);
		if (providers == null) {
			providers = new ArrayList<Object>();
			nonGreedyProvider.put(nonGreedyVariable, providers);
		}
		providers.add(o);
	}

	private void expandRequirements(Collection<IRequirement> reqs, IInstallableUnit iu, boolean isRootIu) throws ContradictionException {
		if (reqs.isEmpty())
			return;
		for (IRequirement req : reqs) {
			expandRequirement(req, iu, allOptionalAbstractRequirements, isRootIu);
		}
	}

	public void processIU(IInstallableUnit iu, boolean isRootIU) throws ContradictionException {
		iu = iu.unresolved();
		Map<Version, IInstallableUnit> iuSlice = slice.get(iu.getId());
		if (iuSlice == null) {
			iuSlice = new HashMap<Version, IInstallableUnit>();
			slice.put(iu.getId(), iuSlice);
		}
		iuSlice.put(iu.getVersion(), iu);
		if (!isApplicable(iu)) {
			createNegation(iu, null);
			return;
		}

		IQueryResult<IInstallableUnit> applicablePatches = getApplicablePatches(iu);
		expandLifeCycle(iu, isRootIU);
		//No patches apply, normal code path
		if (applicablePatches.isEmpty()) {
			expandRequirements(getRequiredCapabilities(iu), iu, isRootIU);
		} else {
			//Patches are applicable to the IU
			expandRequirementsWithPatches(iu, applicablePatches, allOptionalAbstractRequirements, isRootIU);
		}
	}

	private Collection<IRequirement> getRequiredCapabilities(IInstallableUnit iu) {
		boolean isFragment = iu instanceof IInstallableUnitFragment;
		//Short-circuit for the case of an IInstallableUnit 
		if ((!isFragment) && iu.getMetaRequirements().size() == 0)
			return iu.getRequirements();

		ArrayList<IRequirement> aggregatedRequirements = new ArrayList<IRequirement>(iu.getRequirements().size() + iu.getMetaRequirements().size() + (isFragment ? ((IInstallableUnitFragment) iu).getHost().size() : 0));
		aggregatedRequirements.addAll(iu.getRequirements());

		if (iu instanceof IInstallableUnitFragment) {
			aggregatedRequirements.addAll(((IInstallableUnitFragment) iu).getHost());
		}

		if (considerMetaRequirements)
			aggregatedRequirements.addAll(iu.getMetaRequirements());
		return aggregatedRequirements;
	}

	static final class Pending {
		List<? super IInstallableUnitPatch> matches;
		Explanation explanation;
		Object left;
	}

	private void expandRequirementsWithPatches(IInstallableUnit iu, IQueryResult<IInstallableUnit> applicablePatches, List<AbstractVariable> optionalAbstractRequirements, boolean isRootIu) throws ContradictionException {
		//Unmodified dependencies
		Collection<IRequirement> iuRequirements = getRequiredCapabilities(iu);
		Map<IRequirement, List<IInstallableUnitPatch>> unchangedRequirements = new HashMap<IRequirement, List<IInstallableUnitPatch>>(iuRequirements.size());
		Map<IRequirement, Pending> nonPatchedRequirements = new HashMap<IRequirement, Pending>(iuRequirements.size());
		for (Iterator<IInstallableUnit> iterator = applicablePatches.iterator(); iterator.hasNext();) {
			IInstallableUnitPatch patch = (IInstallableUnitPatch) iterator.next();
			IRequirement[][] reqs = mergeRequirements(iu, patch);
			if (reqs.length == 0)
				return;

			// Optional requirements are encoded via:
			// ABS -> (match1(req) or match2(req) or ... or matchN(req))
			// noop(IU)-> ~ABS
			// IU -> (noop(IU) or ABS)
			// Therefore we only need one optional requirement statement per IU
			for (int i = 0; i < reqs.length; i++) {
				//The requirement is unchanged
				if (reqs[i][0] == reqs[i][1]) {
					if (reqs[i][0].getMax() == 0) {
						expandNegatedRequirement(reqs[i][0], iu, optionalAbstractRequirements, isRootIu);
						return;
					}
					if (!isApplicable(reqs[i][0]))
						continue;

					List<IInstallableUnitPatch> patchesAppliedElseWhere = unchangedRequirements.get(reqs[i][0]);
					if (patchesAppliedElseWhere == null) {
						patchesAppliedElseWhere = new ArrayList<IInstallableUnitPatch>();
						unchangedRequirements.put(reqs[i][0], patchesAppliedElseWhere);
					}
					patchesAppliedElseWhere.add(patch);
					continue;
				}

				//Generate dependency when the patch is applied
				//P1 -> (A -> D) equiv. (P1 & A) -> D
				if (isApplicable(reqs[i][1])) {
					IRequirement req = reqs[i][1];
					List<IInstallableUnit> matches = getApplicableMatches(req);
					determinePotentialHostsForFragment(iu);
					if (req.getMin() > 0) {
						if (matches.isEmpty()) {
							missingRequirement(patch, req);
						} else {
							IInstallableUnit current;
							if (req.isGreedy()) {
								IInstallableUnit reqIu = matches.get(0);
								Explanation explanation;
								if (isRootIu) {
									if (alreadyInstalledIUs.contains(reqIu)) {
										explanation = new Explanation.IUInstalled(reqIu);
									} else {
										explanation = new Explanation.IUToInstall(reqIu);
									}
								} else {
									explanation = new Explanation.PatchedHardRequirement(iu, req, patch);
								}
								createImplication(new Object[] {patch, iu}, matches, explanation);
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									if (nonGreedyIUs.contains(current)) {
										addNonGreedyProvider(getNonGreedyVariable(current), iu);
									}
								}
							} else {
								List<Object> newConstraint = new ArrayList<Object>();
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									newConstraint.add(getNonGreedyVariable(current));
								}
								createImplication(new Object[] {iu}, newConstraint, new Explanation.HardRequirement(iu, req)); // FIXME
							}
						}
					} else {
						if (!matches.isEmpty()) {
							IInstallableUnit current;
							AbstractVariable abs;
							if (req.isGreedy()) {
								abs = getAbstractVariable(req);
								createImplication(new Object[] {patch, abs, iu}, matches, Explanation.OPTIONAL_REQUIREMENT);
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									if (nonGreedyIUs.contains(current)) {
										addNonGreedyProvider(getNonGreedyVariable(current), abs);
									}
								}
								optionalAbstractRequirements.add(abs);
							} else {
								abs = getAbstractVariable(req, false);
								List<Object> newConstraint = new ArrayList<Object>(matches.size());
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									newConstraint.add(getNonGreedyVariable(current));
								}
								createImplication(new Object[] {patch, abs, iu}, newConstraint, Explanation.OPTIONAL_REQUIREMENT);
							}
						}
					}
				}
				//Generate dependency when the patch is not applied
				//-P1 -> (A -> B) ( equiv. A -> (P1 or B) )
				if (isApplicable(reqs[i][0])) {
					IRequirement req = reqs[i][0];

					// Fix: if multiple patches apply to the same IU-req, we need to make sure we list each
					// patch as an optional match
					Pending pending = nonPatchedRequirements.get(req);
					if (pending != null) {
						pending.matches.add(patch);
						continue;
					}
					pending = new Pending();
					pending.left = iu;
					List<IInstallableUnit> matches = getApplicableMatches(req);
					determinePotentialHostsForFragment(iu);
					if (req.getMin() > 0) {
						if (matches.isEmpty()) {
							matches.add(patch);
							pending.explanation = new Explanation.HardRequirement(iu, req);
							pending.matches = matches;
						} else {
							// manage non greedy IUs
							IInstallableUnit current;
							List<Object> nonGreedys = new ArrayList<Object>();
							for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
								current = it.next();
								if (nonGreedyIUs.contains(current)) {
									nonGreedys.add(getNonGreedyVariable(current));
								}
							}
							matches.add(patch);
							if (req.isGreedy()) {
								IInstallableUnit reqIu = matches.get(0);///(IInstallableUnit) picker.query(new CapabilityQuery(req), new Collector(), null).iterator().next();
								Explanation explanation;
								if (isRootIu) {
									if (alreadyInstalledIUs.contains(reqIu)) {
										explanation = new Explanation.IUInstalled(reqIu);
									} else {
										explanation = new Explanation.IUToInstall(reqIu);
									}
								} else {
									explanation = new Explanation.HardRequirement(iu, req);
								}

								// Fix: make sure we collect all patches that will impact this IU-req, not just one
								pending.explanation = explanation;
								pending.matches = matches;
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									if (nonGreedyIUs.contains(current)) {
										addNonGreedyProvider(getNonGreedyVariable(current), iu);
									}
								}
							} else {
								List<Object> newConstraint = new ArrayList<Object>(matches.size());
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									newConstraint.add(getNonGreedyVariable(current));
								}
								pending.explanation = new Explanation.HardRequirement(iu, req);
								pending.matches = newConstraint;
							}
							nonPatchedRequirements.put(req, pending);

						}
					} else {
						if (!matches.isEmpty()) {
							IInstallableUnit current;
							AbstractVariable abs;
							matches.add(patch);
							pending = new Pending();
							pending.explanation = Explanation.OPTIONAL_REQUIREMENT;

							if (req.isGreedy()) {
								abs = getAbstractVariable(req);
								// Fix: make sure we collect all patches that will impact this IU-req, not just one
								pending.left = new Object[] {abs, iu};
								pending.matches = matches;
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									if (nonGreedyIUs.contains(current)) {
										addNonGreedyProvider(getNonGreedyVariable(current), abs);
									}
								}
							} else {
								abs = getAbstractVariable(req, false);
								List<Object> newConstraint = new ArrayList<Object>(matches.size());
								for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
									current = it.next();
									newConstraint.add(getNonGreedyVariable(current));
								}
								newConstraint.add(patch);
								pending.left = new Object[] {abs, iu};
								pending.matches = newConstraint;
							}
							nonPatchedRequirements.put(req, pending);
							optionalAbstractRequirements.add(abs);
						}
					}
				}
			}
		}

		// Fix: now create the pending non-patch requirements based on the full set of patches
		for (Pending pending : nonPatchedRequirements.values()) {
			createImplication(pending.left, pending.matches, pending.explanation);
		}

		for (Entry<IRequirement, List<IInstallableUnitPatch>> entry : unchangedRequirements.entrySet()) {
			List<IInstallableUnitPatch> patchesApplied = entry.getValue();
			Iterator<IInstallableUnit> allPatches = applicablePatches.iterator();
			List<IInstallableUnitPatch> requiredPatches = new ArrayList<IInstallableUnitPatch>();
			while (allPatches.hasNext()) {
				IInstallableUnitPatch patch = (IInstallableUnitPatch) allPatches.next();
				if (!patchesApplied.contains(patch))
					requiredPatches.add(patch);
			}
			IRequirement req = entry.getKey();
			List<IInstallableUnit> matches = getApplicableMatches(req);
			determinePotentialHostsForFragment(iu);
			if (req.getMin() > 0) {
				if (matches.isEmpty()) {
					if (requiredPatches.isEmpty()) {
						missingRequirement(iu, req);
					} else {
						createImplication(iu, requiredPatches, new Explanation.HardRequirement(iu, req));
					}
				} else {
					// manage non greedy IUs
					IInstallableUnit current;
					List<Object> nonGreedys = new ArrayList<Object>(matches.size());
					for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
						current = it.next();
						if (nonGreedyIUs.contains(current)) {
							nonGreedys.add(getNonGreedyVariable(current));
						}
					}
					if (!requiredPatches.isEmpty())
						matches.addAll(requiredPatches);
					if (req.isGreedy()) {
						IInstallableUnit reqIu = matches.get(0);
						Explanation explanation;
						if (isRootIu) {
							if (alreadyInstalledIUs.contains(reqIu)) {
								explanation = new Explanation.IUInstalled(reqIu);
							} else {
								explanation = new Explanation.IUToInstall(reqIu);
							}
						} else {
							explanation = new Explanation.HardRequirement(iu, req);
						}
						createImplication(iu, matches, explanation);
						for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
							current = it.next();
							if (nonGreedyIUs.contains(current)) {
								addNonGreedyProvider(getNonGreedyVariable(current), iu);
							}
						}
					} else {
						List<Object> newConstraint = new ArrayList<Object>(matches.size());
						for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
							current = it.next();
							newConstraint.add(getNonGreedyVariable(current));
						}
						createImplication(new Object[] {iu}, newConstraint, new Explanation.HardRequirement(iu, req)); // FIXME
					}
				}
			} else {
				if (!matches.isEmpty()) {
					IInstallableUnit current;
					if (!requiredPatches.isEmpty())
						matches.addAll(requiredPatches);
					AbstractVariable abs;
					if (req.isGreedy()) {
						abs = getAbstractVariable(req);
						createImplication(new Object[] {abs, iu}, matches, Explanation.OPTIONAL_REQUIREMENT);
						for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
							current = it.next();
							if (nonGreedyIUs.contains(current)) {
								addNonGreedyProvider(getNonGreedyVariable(current), iu);
							}
						}
					} else {
						abs = getAbstractVariable(req, false);
						List<Object> newConstraint = new ArrayList<Object>(matches.size());
						for (Iterator<IInstallableUnit> it = matches.iterator(); it.hasNext();) {
							current = it.next();
							newConstraint.add(getNonGreedyVariable(current));
						}
						createImplication(new Object[] {abs, iu}, newConstraint, new Explanation.HardRequirement(iu, req)); // FIXME
					}
					optionalAbstractRequirements.add(abs);
				}
			}
		}
	}

	private void expandLifeCycle(IInstallableUnit iu, boolean isRootIu) throws ContradictionException {
		if (!(iu instanceof IInstallableUnitPatch))
			return;
		IInstallableUnitPatch patch = (IInstallableUnitPatch) iu;
		IRequirement req = patch.getLifeCycle();
		if (req == null)
			return;
		expandRequirement(req, iu, CollectionUtils.<AbstractVariable> emptyList(), isRootIu);
	}

	private void missingRequirement(IInstallableUnit iu, IRequirement req) throws ContradictionException {
		result.add(new Status(IStatus.WARNING, DirectorActivator.PI_DIRECTOR, NLS.bind(Messages.Planner_Unsatisfied_dependency, iu, req)));
		createNegation(iu, req);
	}

	/**
	 * @param req
	 * @return a list of mandatory requirements if any, an empty list if req.isOptional().
	 */
	private List<IInstallableUnit> getApplicableMatches(IRequirement req) {
		List<IInstallableUnit> target = new ArrayList<IInstallableUnit>();
		IQueryResult<IInstallableUnit> matches = picker.query(QueryUtil.createMatchQuery(req.getMatches()), null);
		for (Iterator<IInstallableUnit> iterator = matches.iterator(); iterator.hasNext();) {
			IInstallableUnit match = iterator.next();
			if (isApplicable(match)) {
				target.add(match);
			}
		}
		emptyBecauseFiltered = !matches.isEmpty() && target.isEmpty();
		return target;
	}

	//Return a new array of requirements representing the application of the patch
	private IRequirement[][] mergeRequirements(IInstallableUnit iu, IInstallableUnitPatch patch) {
		if (patch == null)
			return null;
		List<IRequirementChange> changes = patch.getRequirementsChange();
		Collection<IRequirement> iuRequirements = iu.getRequirements();
		IRequirement[] originalRequirements = iuRequirements.toArray(new IRequirement[iuRequirements.size()]);
		List<IRequirement[]> rrr = new ArrayList<IRequirement[]>();
		boolean found = false;
		for (int i = 0; i < changes.size(); i++) {
			IRequirementChange change = changes.get(i);
			for (int j = 0; j < originalRequirements.length; j++) {
				if (originalRequirements[j] != null && safeMatch(originalRequirements, change, j)) {
					found = true;
					if (change.newValue() != null)
						rrr.add(new IRequirement[] {originalRequirements[j], change.newValue()});
					else
						// case where a requirement is removed
						rrr.add(new IRequirement[] {originalRequirements[j], null});
					originalRequirements[j] = null;
				}
				//				break;
			}
			if (!found && change.applyOn() == null && change.newValue() != null) //Case where a new requirement is added
				rrr.add(new IRequirement[] {null, change.newValue()});
		}
		//Add all the unmodified requirements to the result
		for (int i = 0; i < originalRequirements.length; i++) {
			if (originalRequirements[i] != null)
				rrr.add(new IRequirement[] {originalRequirements[i], originalRequirements[i]});
		}
		return rrr.toArray(new IRequirement[rrr.size()][]);
	}

	private boolean safeMatch(IRequirement[] originalRequirements, IRequirementChange change, int j) {
		try {
			return change.matches((IRequiredCapability) originalRequirements[j]);
		} catch (ClassCastException e) {
			return false;
		}
	}

	//This will create as many implication as there is element in the right argument
	private void createNegationImplication(Object left, List<?> right, Explanation name) throws ContradictionException {
		if (DEBUG) {
			Tracing.debug(name + ": " + left + "->" + right); //$NON-NLS-1$ //$NON-NLS-2$
		}
		for (Object r : right)
			dependencyHelper.implication(new Object[] {left}).impliesNot(r).named(name);
	}

	private void createImplication(Object left, List<?> right, Explanation name) throws ContradictionException {
		if (DEBUG) {
			Tracing.debug(name + ": " + left + "->" + right); //$NON-NLS-1$ //$NON-NLS-2$
		}
		dependencyHelper.implication(new Object[] {left}).implies(right.toArray()).named(name);
	}

	private void createImplication(Object[] left, List<?> right, Explanation name) throws ContradictionException {
		if (DEBUG) {
			Tracing.debug(name + ": " + Arrays.asList(left) + "->" + right); //$NON-NLS-1$ //$NON-NLS-2$
		}
		dependencyHelper.implication(left).implies(right.toArray()).named(name);
	}

	//Return IUPatches that are applicable for the given iu
	private IQueryResult<IInstallableUnit> getApplicablePatches(IInstallableUnit iu) {
		if (patches == null)
			patches = new QueryableArray(picker.query(QueryUtil.createIUPatchQuery(), null).toArray(IInstallableUnit.class));

		return patches.query(new ApplicablePatchQuery(iu), null);
	}

	//Create constraints to deal with singleton
	//When there is a mix of singleton and non singleton, several constraints are generated
	private void createConstraintsForSingleton() throws ContradictionException {
		Set<Entry<String, Map<Version, IInstallableUnit>>> s = slice.entrySet();
		for (Entry<String, Map<Version, IInstallableUnit>> entry : s) {
			Map<Version, IInstallableUnit> conflictingEntries = entry.getValue();
			if (conflictingEntries.size() < 2)
				continue;

			Collection<IInstallableUnit> conflictingVersions = conflictingEntries.values();
			List<IInstallableUnit> singletons = new ArrayList<IInstallableUnit>();
			List<IInstallableUnit> nonSingletons = new ArrayList<IInstallableUnit>();
			for (IInstallableUnit iu : conflictingVersions) {
				if (iu.isSingleton()) {
					singletons.add(iu);
				} else {
					nonSingletons.add(iu);
				}
			}
			if (singletons.isEmpty())
				continue;

			IInstallableUnit[] singletonArray;
			if (nonSingletons.isEmpty()) {
				singletonArray = singletons.toArray(new IInstallableUnit[singletons.size()]);
				createAtMostOne(singletonArray);
			} else {
				singletonArray = singletons.toArray(new IInstallableUnit[singletons.size() + 1]);
				for (IInstallableUnit nonSingleton : nonSingletons) {
					singletonArray[singletonArray.length - 1] = nonSingleton;
					createAtMostOne(singletonArray);
				}
			}
		}
	}

	private void createAtMostOne(IInstallableUnit[] ius) throws ContradictionException {
		if (DEBUG) {
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < ius.length; i++) {
				b.append(ius[i].toString());
			}
			Tracing.debug("At most 1 of " + b); //$NON-NLS-1$
		}
		dependencyHelper.atMost(1, (Object[]) ius).named(new Explanation.Singleton(ius));
	}

	private AbstractVariable getAbstractVariable(IRequirement req) {
		return getAbstractVariable(req, true);
	}

	private AbstractVariable getAbstractVariable(IRequirement req, boolean appearInOptFunction) {
		AbstractVariable abstractVariable = DEBUG_ENCODING ? new AbstractVariable("Abs_" + req.toString()) : new AbstractVariable(); //$NON-NLS-1$
		if (appearInOptFunction) {
			abstractVariables.add(abstractVariable);
		}
		return abstractVariable;
	}

	private AbstractVariable getNonGreedyVariable(IInstallableUnit iu) {
		AbstractVariable v = nonGreedyVariables.get(iu);
		if (v == null) {
			v = DEBUG_ENCODING ? new AbstractVariable("NG_" + iu.toString()) : new AbstractVariable(); //$NON-NLS-1$
			nonGreedyVariables.put(iu, v);
		}
		return v;
	}

	public IStatus invokeSolver(IProgressMonitor monitor) {
		if (result.getSeverity() == IStatus.ERROR)
			return result;
		// CNF filename is given on the command line
		long start = System.currentTimeMillis();
		if (DEBUG)
			Tracing.debug("Invoking solver: " + start); //$NON-NLS-1$
		try {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			if (dependencyHelper.hasASolution(assumptions)) {
				if (DEBUG) {
					Tracing.debug("Satisfiable !"); //$NON-NLS-1$
				}
				backToIU();
				long stop = System.currentTimeMillis();
				if (DEBUG)
					Tracing.debug("Solver solution found in: " + (stop - start) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				long stop = System.currentTimeMillis();
				if (DEBUG) {
					Tracing.debug("Unsatisfiable !"); //$NON-NLS-1$
					Tracing.debug("Solver solution NOT found: " + (stop - start)); //$NON-NLS-1$
				}
				result = new MultiStatus(DirectorActivator.PI_DIRECTOR, SimplePlanner.UNSATISFIABLE, result.getChildren(), Messages.Planner_Unsatisfiable_problem, null);
				result.merge(new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, SimplePlanner.UNSATISFIABLE, Messages.Planner_Unsatisfiable_problem, null));
			}
		} catch (TimeoutException e) {
			result.merge(new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, Messages.Planner_Timeout));
		} catch (Exception e) {
			result.merge(new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, Messages.Planner_Unexpected_problem, e));
		}
		if (DEBUG)
			System.out.println();
		return result;
	}

	private void backToIU() {
		solution = new ArrayList<IInstallableUnit>();
		IVec<Object> sat4jSolution = dependencyHelper.getSolution();
		for (Iterator<Object> iter = sat4jSolution.iterator(); iter.hasNext();) {
			Object var = iter.next();
			if (var instanceof IInstallableUnit) {
				IInstallableUnit iu = (IInstallableUnit) var;
				if (iu == entryPoint)
					continue;
				solution.add(iu);
			}
		}
	}

	private void printSolution(Collection<IInstallableUnit> state) {
		ArrayList<IInstallableUnit> l = new ArrayList<IInstallableUnit>(state);
		Collections.sort(l);
		Tracing.debug("Solution:"); //$NON-NLS-1$
		Tracing.debug("Numbers of IUs selected: " + l.size()); //$NON-NLS-1$
		for (IInstallableUnit s : l) {
			Tracing.debug(s.toString());
		}
	}

	public Collection<IInstallableUnit> extractSolution() {
		if (DEBUG)
			printSolution(solution);
		return solution;
	}

	public Set<Explanation> getExplanation(IProgressMonitor monitor) {
		ExplanationJob job = new ExplanationJob();
		job.schedule();
		monitor.setTaskName(Messages.Planner_NoSolution);
		IProgressMonitor pm = new InfiniteProgress(monitor);
		pm.beginTask(Messages.Planner_NoSolution, 1000);
		try {
			synchronized (job) {
				while (job.getExplanationResult() == null && job.getState() != Job.NONE) {
					if (monitor.isCanceled()) {
						job.cancel();
						throw new OperationCanceledException();
					}
					pm.worked(1);
					try {
						job.wait(100);
					} catch (InterruptedException e) {
						if (DEBUG)
							Tracing.debug("Interrupted while computing explanations"); //$NON-NLS-1$
					}
				}
			}
		} finally {
			monitor.done();
		}
		return job.getExplanationResult();
	}

	public Map<IInstallableUnitFragment, List<IInstallableUnit>> getFragmentAssociation() {
		Map<IInstallableUnitFragment, List<IInstallableUnit>> resolvedFragments = new HashMap<IInstallableUnitFragment, List<IInstallableUnit>>(fragments.size());
		for (Entry<IInstallableUnitFragment, Set<IInstallableUnit>> fragment : fragments.entrySet()) {
			if (!dependencyHelper.getBooleanValueFor(fragment.getKey()))
				continue;
			Set<IInstallableUnit> potentialHosts = fragment.getValue();
			List<IInstallableUnit> resolvedHost = new ArrayList<IInstallableUnit>(potentialHosts.size());
			for (IInstallableUnit host : potentialHosts) {
				if (dependencyHelper.getBooleanValueFor(host))
					resolvedHost.add(host);
			}
			if (resolvedHost.size() != 0)
				resolvedFragments.put(fragment.getKey(), resolvedHost);
		}
		return resolvedFragments;
	}

	private void rememberHostMatches(IInstallableUnitFragment fragment, List<IInstallableUnit> matches) {
		Set<IInstallableUnit> existingMatches = fragments.get(fragment);
		if (existingMatches == null) {
			existingMatches = new HashSet<IInstallableUnit>();
			fragments.put(fragment, existingMatches);
			existingMatches.addAll(matches);
		}
		existingMatches.retainAll(matches);
	}

	public void setUserDefined(boolean containsKey) {
		userDefinedFunction = containsKey;
	}
}