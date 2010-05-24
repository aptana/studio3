package com.aptana.editor.html.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.IParserPool;
import com.aptana.editor.common.ParserPoolFactory;
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFileIndexingParticipant implements IFileIndexingParticipant
{
	private static final String[] HTML_EXTENSIONS = { "html", "htm" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		monitor = SubMonitor.convert(monitor, files.size());
		for (IFile file : files)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			try
			{
				if (file == null || !isHTMLFile(file))
				{
					continue;
				}
				monitor.subTask(file.getLocation().toPortableString());
				try
				{
					String fileContents = IOUtil.read(file.getContents());
					HTMLParseState parseState = new HTMLParseState();
					parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
					IParserPool pool = ParserPoolFactory.getInstance().getParserPool(HTMLNode.LANGUAGE);
					IParser htmlParser = pool.checkOut();
					IParseNode parseNode = htmlParser.parse(parseState);
					pool.checkIn(htmlParser);
					walkNode(index, file, parseNode);
				}
				catch (Exception e)
				{
					Activator.logError(
							MessageFormat.format(Messages.HTMLFileIndexingParticipant_Error_During_Indexing,
									file.getName()), e);
				}
			}
			finally
			{
				monitor.worked(1);
			}
		}
		monitor.done();
	}

	private boolean isHTMLFile(IFile file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();
		try
		{
			stream = file.getContents();
			IContentType[] types = manager.findContentTypesFor(stream, file.getName());
			for (IContentType type : types)
			{
				if (type.getId().equals(IHTMLConstants.CONTENT_TYPE_HTML))
					return true;
			}
		}
		catch (CoreException e)
		{
			Activator.logError(e);
		}
		catch (Exception e)
		{
			Activator.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		// fall back to file extensions
		String fileExtension = file.getFileExtension();
		return (HTML_EXTENSIONS[0].equalsIgnoreCase(fileExtension) || HTML_EXTENSIONS[1]
				.equalsIgnoreCase(fileExtension));
	}

	public static void walkNode(Index index, IFile file, IParseNode parent)
	{
		if (parent == null)
			return;

		if (parent instanceof HTMLSpecialNode)
		{
			HTMLSpecialNode htmlSpecialNode = (HTMLSpecialNode) parent;
			IParseNode child = htmlSpecialNode.getChild(0);
			if (child != null)
			{
				String language = child.getLanguage();
				if (ICSSParserConstants.LANGUAGE.equals(language))
				{
					CSSFileIndexingParticipant.walkNode(index, file, child);
				}
			}
			if (htmlSpecialNode.getName().equalsIgnoreCase(ELEMENT_SCRIPT))
			{
				String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);
				if (jsSource != null)
				{
					IFile jsFile = file.getParent().getFile(new Path(jsSource));
					if (jsFile.exists())
					{
						addIndex(index, file, HTMLIndexConstants.RESOURCE_JS, jsFile.getProjectRelativePath()
								.toPortableString());
					}
				}
			}
		}
		else if (parent instanceof HTMLElementNode)
		{
			HTMLElementNode element = (HTMLElementNode) parent;
			String cssClass = element.getCSSClass();
			if (cssClass != null && cssClass.trim().length() > 0)
			{
				StringTokenizer tokenizer = new StringTokenizer(cssClass);
				while (tokenizer.hasMoreTokens())
					addIndex(index, file, CSSIndexConstants.CLASS, tokenizer.nextToken());
			}
			String id = element.getID();
			if (id != null && id.trim().length() > 0)
			{
				addIndex(index, file, CSSIndexConstants.IDENTIFIER, id);
			}
			if (element.getName().equalsIgnoreCase(ELEMENT_LINK))
			{
				String cssLink = element.getAttributeValue(ATTRIBUTE_HREF);
				if (cssLink != null)
				{
					IFile cssFile = file.getParent().getFile(new Path(cssLink));
					if (cssFile.exists())
					{
						addIndex(index, file, HTMLIndexConstants.RESOURCE_CSS, cssFile.getProjectRelativePath()
								.toPortableString());
					}
				}
			}
		}

		for (IParseNode child : parent.getChildren())
		{
			walkNode(index, file, child);
		}
	}

	private static void addIndex(Index index, IFile file, String category, String word)
	{
		index.addEntry(category, word, file.getProjectRelativePath().toPortableString());
	}

}
