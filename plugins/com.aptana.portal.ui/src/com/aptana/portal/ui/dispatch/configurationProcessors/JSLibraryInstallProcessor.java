package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * An installer (import) processor for JavaScript libraries, such as jQuery and Prototype.<br>
 * This processor download and place the JS library under a custom javascript folder in the selected (or active)
 * project. It also allows the use to select the location manually.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSLibraryInstallProcessor extends InstallerConfigurationProcessor
{

	private static final String JS_LIBRARY = "JS Library"; //$NON-NLS-1$
	private static boolean installationInProgress;
	private String libraryName;

	/**
	 * Returns the JS Library name.
	 */
	@Override
	protected String getApplicationName()
	{
		return libraryName;
	}

	/**
	 * Install a JavaScript library into a user-specified project.<br>
	 * The configuration will grab the name and the location of the library from the given attributes. <br>
	 * The expected structure of attributes array is as follows:<br>
	 * <ul>
	 * <li>The first item in the array should contain a String name of the library we are installing (e.g. Prototype,
	 * jQuery etc.)</li>
	 * <li>The second item in the array should contain a non-empty array with an arbitrary amount of resource URLs that
	 * will be downloaded and placed under the 'javascript' directory (or any other user-selected directory).</li>
	 * </ul>
	 * 
	 * @param attributes
	 *            A non-empty string array, which contains the URLs for the JS library file(s).
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// Get a Class lock to avoid multiple installations at the same time even with multiple instances of this
		// RubyInstallProcessor
		synchronized (this.getClass())
		{
			if (installationInProgress)
			{
				return configurationStatus;
			}
			installationInProgress = true;
		}
		try
		{
			configurationStatus.removeAttribute(CONFIG_ATTR);
			clearErrorAttributes();
			if (attributes == null || !(attributes instanceof Object[]))
			{
				String err = NLS.bind(Messages.InstallProcessor_missingInstallURLs, JS_LIBRARY);
				applyErrorAttributes(err);
				PortalUIPlugin.logError(new Exception(err));
				return configurationStatus;
			}
			Object[] attrArray = (Object[]) attributes;
			if (attrArray.length != 2)
			{
				// structure error
				String err = NLS.bind(Messages.InstallProcessor_wrongNumberOfInstallLinks, new Object[] { JS_LIBRARY,
						1, attrArray.length });
				applyErrorAttributes(err);
				PortalUIPlugin.logError(new Exception(err));
				return configurationStatus;
			}
			// Check that the second array element contains a non-empty array
			if (!(attrArray[1] instanceof Object[]) || ((Object[]) attrArray[1]).length == 0)
			{
				String err = NLS.bind(Messages.InstallProcessor_missingInstallURLs, JS_LIBRARY);
				applyErrorAttributes(err);
				PortalUIPlugin.logError("We expected an array of URLs, but got an empty array.", new Exception(err)); //$NON-NLS-1$
				return configurationStatus;
			}
			// Start the installation...
			configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
			libraryName = (String) attrArray[0];
			IStatus status = download((Object[]) attrArray[1], progressMonitor);
			if (status.isOK())
			{
				status = install(progressMonitor);
			}
			switch (status.getSeverity())
			{
				case IStatus.OK:
				case IStatus.INFO:
				case IStatus.WARNING:
					displayMessageInUIThread(MessageDialog.INFORMATION, NLS.bind(
							Messages.InstallProcessor_installerTitle, libraryName), NLS.bind(
							Messages.InstallProcessor_installationSuccessful, libraryName));
					configurationStatus.setStatus(ConfigurationStatus.OK);
					break;
				case IStatus.ERROR:
					configurationStatus.setStatus(ConfigurationStatus.ERROR);
					break;
				case IStatus.CANCEL:
					configurationStatus.setStatus(ConfigurationStatus.INCOMPLETE);
					break;
				default:
					configurationStatus.setStatus(ConfigurationStatus.UNKNOWN);
			}
			return configurationStatus;
		}
		finally
		{
			synchronized (this.getClass())
			{
				installationInProgress = false;
			}
		}
	}

	/**
	 * Install the library.<br>
	 * The installation will display a selection dialog, displaying the projects in the workspace, and selecting the
	 * active project by default. It also takes into account the type of the project (nature) when suggesting the
	 * location to save the JS libraries.
	 * 
	 * @param progressMonitor
	 * @return A status indication of the process success or failure.
	 */
	protected IStatus install(IProgressMonitor progressMonitor)
	{
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}
}
