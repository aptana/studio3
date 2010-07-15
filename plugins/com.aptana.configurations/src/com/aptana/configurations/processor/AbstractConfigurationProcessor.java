package com.aptana.configurations.processor;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;

import com.aptana.configurations.ConfigurationsPlugin;
import com.aptana.core.ShellExecutable;

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
			ConfigurationsPlugin.logError(e);
		}

		return null;
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
}
