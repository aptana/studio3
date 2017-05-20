/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;

import com.aptana.configurations.ConfigurationsPlugin;
import com.aptana.configurations.ConfigurationsUtil;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;

/**
 * A base class that should be used for all {@link IConfigurationProcessor} implementations.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class AbstractConfigurationProcessor implements IConfigurationProcessor
{
	protected ConfigurationStatus configurationStatus;
	protected ListenerList listeners;

	protected static final String CONFIG_ATTR = "configurations"; //$NON-NLS-1$
	protected static final String COMPATIBILITY_OK = "ok"; //$NON-NLS-1$
	protected static final String COMPATIBILITY_UPDATE = "update"; //$NON-NLS-1$
	protected static final String ITEM_EXISTS = "exists"; //$NON-NLS-1$
	protected static final String YES = "yes"; //$NON-NLS-1$
	protected static final String NO = "no"; //$NON-NLS-1$
	protected static final String ITEM_VERSION = "version"; //$NON-NLS-1$
	protected static final String ITEM_COMPATIBILITY = "compatibility"; //$NON-NLS-1$
	protected static final String ITEM_VERSION_OUTPUT = "rawOutput"; //$NON-NLS-1$

	private String processorID;
	private String processorName;
	private String[] categories;

	protected String[] urls;
	protected Map<String, String> attributesMap;
	protected Expression enablementExpression;

	/**
	 * Constructs a new configuration processor.<br>
	 * Since this is done through an extension point, make sure that the processor ID is set right after initialization.
	 */
	public AbstractConfigurationProcessor()
	{
		listeners = new ListenerList(ListenerList.IDENTITY);
	}

	/**
	 * Set the configuration processor ID. This call should be made immediately after initializing an instance of this
	 * class.
	 * 
	 * @param processorID
	 * @throws IllegalStateException
	 *             In case the processor ID was already set once
	 */
	public void setID(String processorID)
	{
		if (this.processorID != null)
		{
			throw new IllegalStateException("The processor id was already set for this processor!"); //$NON-NLS-1$
		}
		this.processorID = processorID;
		// Make sure that we load or create the status right when we have the id
		loadStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessor#getID()
	 */
	public String getID()
	{
		return processorID;
	}

	/**
	 * Set the processor's enablement {@link Expression}.
	 * 
	 * @param enablementExpression
	 *            An {@link Expression} that will be evaluated to determine the enablement of this processor. May be
	 *            <code>null</code> to indicate that the processor is always enabled.
	 */
	public void setEnablement(Expression enablementExpression)
	{
		this.enablementExpression = enablementExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessor#isEnabled()
	 */
	public boolean isEnabled()
	{
		boolean enabled = ConfigurationsUtil.evaluateEnablement(enablementExpression);
		return enabled && hasEnabledDelegates();
	}

	/**
	 * Set the configuration processor name. This call should be made immediately after initializing an instance of this
	 * class.
	 * 
	 * @param processorName
	 * @throws IllegalStateException
	 *             In case the processor name was already set once
	 */
	public void setName(String processorName)
	{
		if (this.processorName != null)
		{
			throw new IllegalStateException("The processor name was already set for this processor!"); //$NON-NLS-1$
		}
		this.processorName = processorName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessor#getName()
	 */
	public String getName()
	{
		return processorName;
	}

	/**
	 * By default, the AbstractConfigurationProcessor uses the given attributes only when the refresh is true and
	 * passing it to the {@link #computeStatus(IProgressMonitor, Object)} method.<br>
	 * Clients may override this behavior.
	 * 
	 * @see IConfigurationProcessor#getStatus(IProgressMonitor, Object, boolean)
	 */
	public ConfigurationStatus getStatus(IProgressMonitor progressMonitor, Object attributes, boolean refresh)
	{
		if (!refresh)
		{
			// just return the status that we have
			return configurationStatus;
		}
		configurationStatus = computeStatus(progressMonitor, attributes);
		return configurationStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessor#addConfigurationProcessorListener(com.aptana.
	 * configurations.processor.IConfigurationProcessorListener)
	 */
	public void addConfigurationProcessorListener(IConfigurationProcessorListener listener)
	{
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessor#removeConfigurationProcessorListener(com.aptana.
	 * configurations.processor.IConfigurationProcessorListener)
	 */
	public void removeConfigurationProcessorListener(IConfigurationProcessorListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Notify the configuration processor listeners about changes with the configuration state.
	 * 
	 * @param attributes
	 *            A Set of attributes
	 */
	protected void notifyListeners(Set<String> attributes)
	{
		Object[] processorListeners = listeners.getListeners();
		for (Object obj : processorListeners)
		{
			((IConfigurationProcessorListener) obj).configurationStateChanged(configurationStatus, attributes);
		}
	}

	/**
	 * Set a categories list that this processor can be associated with.
	 * 
	 * @param categories
	 */
	public void setCategories(String[] categories)
	{
		this.categories = categories;
	}

	/**
	 * Returns the categories that this processor can be associated with.
	 * 
	 * @return An array of categories, or an empty array.
	 */
	public String[] getCategories()
	{
		if (categories == null)
		{
			categories = new String[0];
		}
		return categories;
	}

	/**
	 * Compute the status of this configuration and return an updated ConfigurationStatus.<br>
	 * This computation is already done in a job and should not start other jobs/threads without joining them.
	 * 
	 * @param progressMonitor
	 *            An optional progress monitor.
	 * @param attributes
	 *            An arbitrary attributes object that may contain some data needed for the computation. For example, a
	 *            list of plug-in names. This argument can be null
	 * @return An updated ConfigurationStatus after recomputing its status.
	 */
	public abstract ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes);

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.configurations.processor.IConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 * java.lang.Object)
	 */
	public abstract ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes);

	/**
	 * Returns the Shell command path.
	 * 
	 * @return The shell command path.
	 */
	public static String getShellPath()
	{
		try
		{
			IPath path = ShellExecutable.getPath();
			if (path != null)
			{
				return path.toOSString();
			}
		}
		catch (CoreException e)
		{
			IdeLog.logWarning(ConfigurationsPlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if this processor has no delegates, of if it has delegates and at least one delegate is
	 * enabled.
	 * 
	 * @return <code>true</code> if there are no delegates, or there is at least one enabled delegate.
	 */
	protected boolean hasEnabledDelegates()
	{
		Set<IConfigurationProcessorDelegate> delegates = ConfigurationProcessorsRegistry.getInstance()
				.getProcessorDelegators(getID());
		if (!delegates.isEmpty())
		{
			for (IConfigurationProcessorDelegate delegate : delegates)
			{
				if (delegate.isEnabled())
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Creates a new UNKNOWN status instance, or load it from the cache in case exists.
	 * 
	 * @throws IllegalStateException
	 *             In case this method was called before setting the processor id.
	 * @see #setID(String)
	 */
	protected void loadStatus()
	{
		if (getID() == null)
		{
			throw new IllegalStateException(
					"Could not create/load a configuration status before setting the configuration processor id!"); //$NON-NLS-1$
		}
		// Initializing this configuration status will try to load previously saved status if exists.
		// If not, the status will just be ConfigurationStatus.UNKNOWN.
		// Pass this instance to the status constructor to be notified when the status changed.
		configurationStatus = new ConfigurationStatus(getID(), this);
	}

	/**
	 * Set the given ConfigurationStatus to indicate an error and set an error attribute to contain the message.
	 * 
	 * @param configurationStatus
	 * @param msg
	 *            The error message attribute (can be null)
	 * @see #clearErrorAttributes()
	 */
	protected void applyErrorAttributes(String msg)
	{
		if (msg != null)
		{
			configurationStatus.setAttribute(ConfigurationStatus.ERROR, msg);
		}
		configurationStatus.setStatus(ConfigurationStatus.ERROR);
	}

	/**
	 * Clear the error attribute from the current configuration status.<br>
	 * Note: this does not change the actual status of the configuration-status. Just the Error message attribute.
	 * 
	 * @see #applyErrorAttributes(String)
	 */
	protected void clearErrorAttributes()
	{
		configurationStatus.removeAttribute(ConfigurationStatus.ERROR);
	}

	/**
	 * Loads the installation attributes.<br>
	 * We expects a common structure of installation attributes, which follows these rules:
	 * <p>
	 * <ul>
	 * <li>The attributes object is expected to be an array of size 1 or 2.</li>
	 * <li>The first element of the attributes array is an array of strings, representing the URLs to be used with this
	 * installer.</li>
	 * <li>The second element of the attributes array is <b>optional</b>. If exists, it should contain a Map of extra
	 * attributes key-value strings.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param attributes
	 *            An array of attributes, with the structure described above.
	 * @return A status indicating the loading state.
	 */
	@SuppressWarnings("unchecked")
	protected IStatus loadAttributes(Object attributes)
	{
		if (!(attributes instanceof Object[]))
		{
			return createErrorStatus(Messages.AbstractConfigurationProcessor_expectedArrayError + attributes);
		}
		Object[] attrArray = (Object[]) attributes;
		if (attrArray.length == 1 || attrArray.length == 2)
		{
			// We only expects the URLs array
			if (!(attrArray[0] instanceof Object[]))
			{
				return createErrorStatus(Messages.AbstractConfigurationProcessor_expectedURLsArrayError + attributes);
			}
			Object[] attrURL = (Object[]) attrArray[0];
			if (attrURL.length == 0)
			{
				return createErrorStatus(Messages.AbstractConfigurationProcessor_emptyURLsArrayError);
			}
			// Load the urls
			urls = new String[attrURL.length];
			for (int i = 0; i < attrURL.length; i++)
			{
				urls[i] = attrURL[i].toString();
			}
			if (attrArray.length == 2)
			{
				// We also expects an extra attributes Map
				if (!(attrArray[1] instanceof Map))
				{
					return createErrorStatus(Messages.AbstractConfigurationProcessor_expectedMapError + attrArray[1]);
				}
				// save this map
				attributesMap = (Map<String, String>) attrArray[1];
			}
			else
			{
				// assign an empty map
				attributesMap = Collections.emptyMap();
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Creates an returns an error status.
	 * 
	 * @param errorMessage
	 * @return An error status
	 */
	protected IStatus createErrorStatus(String errorMessage)
	{
		return new Status(IStatus.ERROR, ConfigurationsPlugin.PLUGIN_ID, errorMessage);
	}
}
