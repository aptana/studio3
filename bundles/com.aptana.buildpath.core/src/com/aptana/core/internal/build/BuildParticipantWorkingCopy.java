/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.build;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.build.BuildContext;

/**
 * A utility class for batching changes to a build participant and querying the changes. Used mainly to wrap a build
 * participant and apply the changes across all the participants at once in preferences.
 * 
 * @author cwilliams
 */
public class BuildParticipantWorkingCopy implements IBuildParticipantWorkingCopy
{
	private IBuildParticipant wrapped;
	private Boolean enabledForBuild;
	private Boolean enabledForReconcile;
	private String[] filters;
	private Map<String, Object> preferences;
	private String qualifier;

	public BuildParticipantWorkingCopy(IBuildParticipant wrapped, String qualifier)
	{
		this.wrapped = wrapped;
		this.qualifier = qualifier;
	}

	public void clean(IProject project, IProgressMonitor monitor)
	{
		wrapped.clean(project, monitor);
	}

	public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
	{
		wrapped.buildStarting(project, kind, monitor);
	}

	public void buildEnding(IProgressMonitor monitor)
	{
		wrapped.buildEnding(monitor);
	}

	public int getPriority()
	{
		return wrapped.getPriority();
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		wrapped.buildFile(context, monitor);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		wrapped.deleteFile(context, monitor);
	}

	public Set<IContentType> getContentTypes()
	{
		return wrapped.getContentTypes();
	}

	public String getName()
	{
		return wrapped.getName();
	}

	public String getId()
	{
		return wrapped.getId();
	}

	public boolean isEnabled(BuildType type)
	{
		switch (type)
		{
			case BUILD:
				if (enabledForBuild != null)
				{
					return enabledForBuild;
				}
				break;

			case RECONCILE:
				if (enabledForReconcile != null)
				{
					return enabledForReconcile;
				}
				break;

			default:
				break;
		}
		return wrapped.isEnabled(type);
	}

	public void setEnabled(BuildType type, boolean enabled)
	{
		if (isRequired())
		{
			return;
		}

		switch (type)
		{
			case BUILD:
				enabledForBuild = enabled;
				break;

			case RECONCILE:
				enabledForReconcile = enabled;
				break;
			default:
				break;
		}
	}

	public void restoreDefaults()
	{
		wrapped.restoreDefaults();
		enabledForBuild = null;
		enabledForReconcile = null;
		filters = null;
	}

	public boolean isRequired()
	{
		return wrapped.isRequired();
	}

	public List<String> getFilters()
	{
		if (filters != null)
		{
			return Arrays.asList(filters);
		}
		return wrapped.getFilters();
	}

	public boolean isEnabled(IProject project)
	{
		return wrapped.isEnabled(project);
	}

	public void setFilters(String... filters)
	{
		this.filters = filters;
	}

	public boolean needsRebuild()
	{
		return enabledForBuild != null
				|| (wrapped.isEnabled(BuildType.BUILD) && (filters != null || preferences != null));
	}

	public IBuildParticipant doSave()
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(qualifier);
		if (enabledForBuild != null)
		{
			prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(getId(), BuildType.BUILD), enabledForBuild);
			enabledForBuild = null;
		}
		if (enabledForReconcile != null)
		{
			prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(getId(), BuildType.RECONCILE),
					enabledForReconcile);
			enabledForReconcile = null;
		}
		if (filters != null)
		{
			prefs.put(PreferenceUtil.getFiltersKey(getId()), PreferenceUtil.serializeFilters(filters));
			filters = null;
		}
		if (preferences != null)
		{
			setPreferences(prefs, preferences);
			preferences = null;
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
		}

		return wrapped;
	}

	private void setPreferences(IEclipsePreferences prefs, Map<String, ? extends Object> preferences)
	{
		if (CollectionsUtil.isEmpty(preferences))
		{
			return;
		}

		for (Map.Entry<String, ? extends Object> entry : preferences.entrySet())
		{
			Object value = entry.getValue();
			if (value instanceof Boolean)
			{
				prefs.putBoolean(entry.getKey(), (Boolean) value);
			}
			else if (value instanceof Long)
			{
				prefs.putLong(entry.getKey(), (Long) value);
			}
			else if (value instanceof Integer)
			{
				prefs.putInt(entry.getKey(), (Integer) value);
			}
			else if (value instanceof Double)
			{
				prefs.putDouble(entry.getKey(), (Double) value);
			}
			else if (value != null)
			{
				prefs.put(entry.getKey(), value.toString());
			}
		}
	}

	public boolean needsReconcile()
	{
		return enabledForReconcile != null
				|| (wrapped.isEnabled(BuildType.RECONCILE) && (filters != null || preferences != null));
	}

	public IBuildParticipant getOriginal()
	{
		if (wrapped instanceof LazyBuildParticipant)
		{
			return ((LazyBuildParticipant) wrapped).getParticipant();
		}
		return wrapped;
	}

	public String getPreferenceString(String prefKey)
	{
		if (preferences != null && preferences.containsKey(prefKey))
		{
			return (String) preferences.get(prefKey);
		}

		return ((AbstractBuildParticipant) wrapped).getPreferenceString(prefKey);
	}

	public boolean getPreferenceBoolean(String prefKey)
	{
		if (preferences != null && preferences.containsKey(prefKey))
		{
			return (Boolean) preferences.get(prefKey);
		}

		return wrapped.getPreferenceBoolean(prefKey);
	}

	public void setPreference(String prefKey, Object value)
	{
		if (preferences == null)
		{
			preferences = new HashMap<String, Object>();
		}
		preferences.put(prefKey, value);
	}

	public IBuildParticipantWorkingCopy getWorkingCopy()
	{
		return this;
	}

	public int getPreferenceInt(String prefKey, int defaultValue)
	{
		if (preferences != null && preferences.containsKey(prefKey))
		{
			return (Integer) preferences.get(prefKey);
		}

		return wrapped.getPreferenceInt(prefKey, defaultValue);
	}
}