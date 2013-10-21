/*******************************************************************************
 *  Copyright (c) 2008, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *     Sonatype, Inc. - ongoing development
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import java.util.*;
import org.eclipse.equinox.internal.p2.core.helpers.CollectionUtils;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;

/**
 * @noreference This class was unintentionally left in the provisional API package and
 * 	is intended to be made internal in Eclipse 3.7. Clients should create and manipulate 
 * 	profile change requests via the API {@link IPlanner#createChangeRequest(IProfile)}
 * 	and methods on {@link IProfileChangeRequest}.
 */
public class ProfileChangeRequest implements Cloneable, IProfileChangeRequest {

	private final IProfile profile;
	private ArrayList<IInstallableUnit> iusToRemove = null; // list of ius to remove
	private ArrayList<IInstallableUnit> iusToAdd = null; // list of ius to add
	private ArrayList<String> propertiesToRemove = null; // list of keys for properties to be removed
	private HashMap<String, String> propertiesToAdd = null; // map of key->value for properties to be added
	private HashMap<IInstallableUnit, Map<String, String>> iuPropertiesToAdd = null; // map iu->map of key->value pairs for properties to be added for an iu
	private HashMap<IInstallableUnit, List<String>> iuPropertiesToRemove = null; // map of iu->list of property keys to be removed for an iu
	private ArrayList<IRequirement> additionalRequirements;

	public static ProfileChangeRequest createByProfileId(IProvisioningAgent agent, String profileId) {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			throw new IllegalStateException(Messages.Planner_no_profile_registry);
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			throw new IllegalArgumentException("Profile id " + profileId + " is not registered."); //$NON-NLS-1$//$NON-NLS-2$
		return new ProfileChangeRequest(profile);
	}

	public ProfileChangeRequest(IProfile profile) {
		this.profile = profile;
	}

	public void setProfile(IProfile profile) {
		if (profile == null)
			throw new IllegalArgumentException("Profile cannot be null."); //$NON-NLS-1$
	}

	public IProfile getProfile() {
		return profile;
	}

	public Map<String, String> getProfileProperties() {
		Map<String, String> result = new HashMap<String, String>(profile.getProperties());
		if (propertiesToRemove != null) {
			for (String key : propertiesToRemove) {
				result.remove(key);
			}
		}
		if (propertiesToAdd != null)
			result.putAll(propertiesToAdd);

		return result;
	}

	//done
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#addInstallableUnit(org.eclipse.equinox.p2.metadata.IInstallableUnit)
	 */
	public void add(IInstallableUnit toInstall) {
		if (iusToAdd == null)
			iusToAdd = new ArrayList<IInstallableUnit>();
		iusToAdd.add(toInstall);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#addInstallableUnits(java.util.Collection)
	 */
	public void addAll(Collection<IInstallableUnit> toInstall) {
		for (IInstallableUnit iu : toInstall)
			add(iu);
	}

	public void addInstallableUnits(IInstallableUnit... toInstall) {
		for (int i = 0; i < toInstall.length; i++)
			add(toInstall[i]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#removeInstallableUnit(org.eclipse.equinox.p2.metadata.IInstallableUnit)
	 */
	public void remove(IInstallableUnit toUninstall) {
		if (iusToRemove == null)
			iusToRemove = new ArrayList<IInstallableUnit>();
		iusToRemove.add(toUninstall);
	}

	public void removeInstallableUnits(IInstallableUnit[] toUninstall) {
		for (int i = 0; i < toUninstall.length; i++)
			remove(toUninstall[i]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#removeInstallableUnits(java.util.Collection)
	 */
	public void removeAll(Collection<IInstallableUnit> toUninstall) {
		for (IInstallableUnit iu : toUninstall)
			remove(iu);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#setProfileProperty(java.lang.String, java.lang.String)
	 */
	public void setProfileProperty(String key, String value) {
		if (propertiesToAdd == null)
			propertiesToAdd = new HashMap<String, String>();
		propertiesToAdd.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#removeProfileProperty(java.lang.String)
	 */
	public void removeProfileProperty(String key) {
		if (propertiesToRemove == null)
			propertiesToRemove = new ArrayList<String>(1);
		propertiesToRemove.add(key);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#setInstallableUnitProfileProperty(org.eclipse.equinox.p2.metadata.IInstallableUnit, java.lang.String, java.lang.String)
	 */
	public void setInstallableUnitProfileProperty(IInstallableUnit iu, String key, String value) {
		if (iuPropertiesToAdd == null)
			iuPropertiesToAdd = new HashMap<IInstallableUnit, Map<String, String>>();
		Map<String, String> properties = iuPropertiesToAdd.get(iu);
		if (properties == null) {
			properties = new HashMap<String, String>();
			iuPropertiesToAdd.put(iu, properties);
		}
		properties.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#removeInstallableUnitProfileProperty(org.eclipse.equinox.p2.metadata.IInstallableUnit, java.lang.String)
	 */
	public void removeInstallableUnitProfileProperty(IInstallableUnit iu, String key) {
		if (iuPropertiesToRemove == null)
			iuPropertiesToRemove = new HashMap<IInstallableUnit, List<String>>();
		List<String> keys = iuPropertiesToRemove.get(iu);
		if (keys == null) {
			keys = new ArrayList<String>();
			iuPropertiesToRemove.put(iu, keys);
		}
		if (!keys.contains(key))
			keys.add(key);
	}

	public Collection<IInstallableUnit> getRemovals() {
		if (iusToRemove == null)
			return CollectionUtils.emptyList();
		return Collections.unmodifiableList(iusToRemove);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#getAddedInstallableUnits()
	 */
	public Collection<IInstallableUnit> getAdditions() {
		if (iusToAdd == null)
			return CollectionUtils.emptyList();
		return Collections.unmodifiableList(iusToAdd);
	}

	// String [key, key, key] names of properties to remove
	public String[] getPropertiesToRemove() {
		if (propertiesToRemove == null)
			return new String[0];
		return propertiesToRemove.toArray(new String[propertiesToRemove.size()]);
	}

	// map of key value pairs
	public Map<String, String> getPropertiesToAdd() {
		if (propertiesToAdd == null)
			return CollectionUtils.emptyMap();
		return propertiesToAdd;
	}

	// map of iu->list of property keys to be removed for an iu	
	public Map<IInstallableUnit, List<String>> getInstallableUnitProfilePropertiesToRemove() {
		if (iuPropertiesToRemove == null)
			return CollectionUtils.emptyMap();
		return iuPropertiesToRemove;
	}

	// TODO This can be represented and returned in whatever way makes most sense for planner/engine
	// map iu->map of key->value pairs for properties to be added for an iu
	public Map<IInstallableUnit, Map<String, String>> getInstallableUnitProfilePropertiesToAdd() {
		if (iuPropertiesToAdd == null)
			return CollectionUtils.emptyMap();
		return iuPropertiesToAdd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#setInstallableUnitInclusionRules(org.eclipse.equinox.p2.metadata.IInstallableUnit, java.lang.String)
	 */
	public void setInstallableUnitInclusionRules(IInstallableUnit iu, String value) {
		setInstallableUnitProfileProperty(iu, SimplePlanner.INCLUSION_RULES, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.director.IPCR#removeInstallableUnitInclusionRules(org.eclipse.equinox.p2.metadata.IInstallableUnit)
	 */
	public void removeInstallableUnitInclusionRules(IInstallableUnit iu) {
		removeInstallableUnitProfileProperty(iu, SimplePlanner.INCLUSION_RULES);
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		ProfileChangeRequest result = new ProfileChangeRequest(profile);
		result.iusToRemove = iusToRemove == null ? null : (ArrayList<IInstallableUnit>) iusToRemove.clone();
		result.iusToAdd = iusToAdd == null ? null : (ArrayList<IInstallableUnit>) iusToAdd.clone();
		result.propertiesToRemove = propertiesToRemove == null ? null : (ArrayList<String>) propertiesToRemove.clone();
		result.propertiesToAdd = propertiesToAdd == null ? null : (HashMap<String, String>) propertiesToAdd.clone();
		result.iuPropertiesToAdd = iuPropertiesToAdd == null ? null : (HashMap<IInstallableUnit, Map<String, String>>) iuPropertiesToAdd.clone();
		result.iuPropertiesToRemove = iuPropertiesToRemove == null ? null : (HashMap<IInstallableUnit, List<String>>) iuPropertiesToRemove.clone();
		result.additionalRequirements = additionalRequirements == null ? null : (ArrayList<IRequirement>) additionalRequirements.clone();
		return result;
	}

	public String toString() {
		StringBuffer result = new StringBuffer(1000);
		result.append("==Profile change request for "); //$NON-NLS-1$
		result.append(profile.getProfileId());
		result.append('\n');
		if (iusToAdd != null) {
			result.append("==Additions=="); //$NON-NLS-1$
			result.append('\n');
			for (IInstallableUnit iu : iusToAdd) {
				result.append('\t');
				result.append(iu);
				result.append('\n');
			}
		}
		if (iusToRemove != null) {
			result.append("==Removals=="); //$NON-NLS-1$
			result.append('\n');
			for (IInstallableUnit iu : iusToRemove) {
				result.append('\t');
				result.append(iu);
				result.append('\n');
			}
		}
		return result.toString();
	}

	public void addExtraRequirements(Collection<IRequirement> requirements) {
		if (additionalRequirements == null)
			additionalRequirements = new ArrayList<IRequirement>(requirements.size());
		additionalRequirements.addAll(requirements);
	}

	public Collection<IRequirement> getExtraRequirements() {
		return additionalRequirements;
	}

	public void clearExtraRequirements() {
		additionalRequirements = null;
	}
}
