package com.aptana.index.core;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.Job;

abstract class IndexRequestJob extends Job
{
	/**
	 * Constants for dealing with file indexers through the extension point.
	 */
	private static final String CONTENT_TYPE_ID = "contentTypeId"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_BINDING = "contentTypeBinding"; //$NON-NLS-1$
	private static final String FILE_INDEXING_PARTICIPANTS_ID = "fileIndexingParticipants"; //$NON-NLS-1$
	private static final String TAG_FILE_INDEXING_PARTICIPANT = "fileIndexingParticipant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private URI containerURI;

	public IndexRequestJob(URI containerURI)
	{
		this(MessageFormat.format("Indexing {0}", containerURI.toString()), containerURI);
	}

	public IndexRequestJob(String name, URI containerURI)
	{
		super(name);
		this.containerURI = containerURI;
		setRule(IndexManager.MUTEX_RULE);
		setPriority(Job.BUILD);
		// setSystem(true);
	}

	@Override
	public boolean belongsTo(Object family)
	{
		if (getContainerURI() == null)
		{
			return family == null;
		}
		if (family == null)
		{
			return false;
		}
		return getContainerURI().equals(family);
	}

	protected URI getContainerURI()
	{
		return containerURI;
	}

	protected Index getIndex()
	{
		return IndexManager.getInstance().getIndex(getContainerURI());
	}

	/**
	 * Indexes a set of {@link IFileStore}s with the appropriate {@link IFileStoreIndexingParticipant}s that apply to
	 * the content types (matching is done via filename/extension).
	 * 
	 * @param index
	 * @param fileStores
	 * @param monitor
	 * @throws CoreException
	 */
	protected void indexFileStores(Index index, Set<IFileStore> fileStores, IProgressMonitor monitor)
			throws CoreException
	{
		if (index == null || fileStores == null || fileStores.isEmpty())
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, fileStores.size() * 10);
		try
		{
			// First cleanup old index entries for files
			for (IFileStore file : fileStores)
			{
				if (sub.isCanceled())
				{
					throw new CoreException(Status.CANCEL_STATUS);
				}
				index.remove(file.toURI());
				sub.worked(1);
			}

			// Now map the indexers to the files they need to/can index
			Map<IFileStoreIndexingParticipant, Set<IFileStore>> toDo = mapParticipantsToFiles(fileStores);
			sub.worked(fileStores.size());

			if (!toDo.isEmpty())
			{
				// Determine work remaining
				int sum = 0;
				for (Map.Entry<IFileStoreIndexingParticipant, Set<IFileStore>> entry : toDo.entrySet())
				{
					sum += entry.getValue().size();
				}
				sub.setWorkRemaining(sum);

				// Now do the indexing...
				for (Map.Entry<IFileStoreIndexingParticipant, Set<IFileStore>> entry : toDo.entrySet())
				{
					if (sub.isCanceled())
					{
						throw new CoreException(Status.CANCEL_STATUS);
					}
					try
					{
						entry.getKey().index(entry.getValue(), index, sub.newChild(entry.getValue().size()));
					}
					catch (CoreException e)
					{
						IndexActivator.logError(e);
					}
				}
			}
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * Take the set of {@link IFileStore}s that need to be indexed, and then generate a mapping from the index
	 * participants to the IFileStores they need to index.
	 * 
	 * @param fileStores
	 * @return
	 */
	protected Map<IFileStoreIndexingParticipant, Set<IFileStore>> mapParticipantsToFiles(Set<IFileStore> fileStores)
	{
		Map<IFileStoreIndexingParticipant, Set<IFileStore>> result = new HashMap<IFileStoreIndexingParticipant, Set<IFileStore>>();

		Map<IConfigurationElement, Set<IContentType>> participants = getFileIndexingParticipants();
		for (Map.Entry<IConfigurationElement, Set<IContentType>> entry : participants.entrySet())
		{
			Set<IFileStore> filesForParticipant = new HashSet<IFileStore>();
			for (IFileStore store : fileStores)
			{
				if (hasType(store, entry.getValue()))
				{
					filesForParticipant.add(store);
				}
			}
			if (filesForParticipant.isEmpty())
			{
				continue;
			}
			IFileStoreIndexingParticipant participant = createParticipant(entry.getKey());
			if (participant != null)
			{
				result.put(participant, filesForParticipant);
			}
		}
		return result;
	}

	/**
	 * Instantiate a {@link IFileStoreIndexingParticipant} from the {@link IConfigurationElement} pointing to it via an
	 * extension.
	 * 
	 * @param key
	 * @return
	 */
	private IFileStoreIndexingParticipant createParticipant(IConfigurationElement key)
	{
		try
		{
			return (IFileStoreIndexingParticipant) key.createExecutableExtension(ATTR_CLASS);
		}
		catch (CoreException e)
		{
			IndexActivator.logError(e);
		}
		return null;
	}

	protected boolean hasType(IFileStore store, Set<IContentType> types)
	{
		if (types == null || types.isEmpty())
		{
			return false;
		}
		for (IContentType type : types)
		{
			if (type == null)
			{
				continue;
			}
			if (type.isAssociatedWith(store.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a map from classname of the participant to a set of strings for the content type ids it applies to.
	 * 
	 * @return
	 */
	private Map<IConfigurationElement, Set<IContentType>> getFileIndexingParticipants()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		Map<IConfigurationElement, Set<IContentType>> map = new HashMap<IConfigurationElement, Set<IContentType>>();
		if (registry == null)
		{
			return map;
		}

		IExtensionPoint extensionPoint = registry.getExtensionPoint(IndexActivator.PLUGIN_ID,
				FILE_INDEXING_PARTICIPANTS_ID);
		if (extensionPoint == null)
		{
			return map;
		}

		IContentTypeManager manager = Platform.getContentTypeManager();

		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions)
		{
			IConfigurationElement[] elements = extension.getConfigurationElements();

			for (IConfigurationElement element : elements)
			{
				if (element.getName().equals(TAG_FILE_INDEXING_PARTICIPANT))
				{
					Set<IContentType> types = new HashSet<IContentType>();

					IConfigurationElement[] contentTypes = element.getChildren(CONTENT_TYPE_BINDING);
					for (IConfigurationElement contentTypeBinding : contentTypes)
					{
						String contentTypeId = contentTypeBinding.getAttribute(CONTENT_TYPE_ID);
						IContentType type = manager.getContentType(contentTypeId);
						types.add(type);
					}
					map.put(element, types);
				}
			}
		}
		return map;
	}
}
