/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.hover;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.projection.AnnotationBag;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

import com.aptana.core.util.StringUtil;

/**
 * A common annotation hover implementation that follows the {@link DefaultAnnotationHover}. The main difference between
 * those implementations is that multiple markers descriptions will appear in order. That is, in case there are multiple
 * markers on the same line, the annotation-hover will display the markers information in the same order of their
 * offsets.
 * 
 * @author sgibly@appcelerator.com
 */
@SuppressWarnings("restriction")
public class CommonAnnotationHover implements IAnnotationHover
{

	/**
	 * Tells whether the line number should be shown when no annotation is found under the cursor.
	 */
	private boolean fShowLineNumber;

	/**
	 * Creates a new default annotation hover.
	 */
	public CommonAnnotationHover()
	{
		this(false);
	}

	/**
	 * Creates a new default annotation hover.
	 * 
	 * @param showLineNumber
	 *            <code>true</code> if the line number should be shown when no annotation is found
	 */
	public CommonAnnotationHover(boolean showLineNumber)
	{
		fShowLineNumber = showLineNumber;
	}

	/*
	 * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer,
	 * int)
	 */
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber)
	{
		// The returns set is sorted by the marker's offset or line number.
		Set<Annotation> javaAnnotations = getAnnotationsForLine(sourceViewer, lineNumber);
		if (javaAnnotations != null)
		{

			if (javaAnnotations.size() == 1)
			{
				Annotation annotation = javaAnnotations.iterator().next();
				String message = annotation.getText();
				if (!StringUtil.isEmpty(message))
				{
					return formatSingleMessage(message);
				}
			}
			else
			{
				List<String> messages = new ArrayList<String>();

				Iterator<Annotation> e = javaAnnotations.iterator();
				while (e.hasNext())
				{
					Annotation annotation = e.next();
					String message = annotation.getText();
					if (!StringUtil.isEmpty(message))
						messages.add(message.trim());
				}

				if (messages.size() == 1)
				{
					return formatSingleMessage((String) messages.get(0));
				}
				if (messages.size() > 1)
				{
					return formatMultipleMessages(messages);
				}
			}
		}

		if (fShowLineNumber && lineNumber > -1)
		{
			return MessageFormat.format(Messages.CommonAnnotationHover_lineNumber, Integer.toString(lineNumber + 1));
		}
		return null;
	}

	/**
	 * Tells whether the annotation should be included in the computation.
	 * 
	 * @param annotation
	 *            the annotation to test
	 * @return <code>true</code> if the annotation is included in the computation
	 */
	protected boolean isIncluded(Annotation annotation)
	{
		return true;
	}

	/**
	 * Hook method to format the given single message.
	 * <p>
	 * Subclasses can change this to create a different format like HTML.
	 * </p>
	 * 
	 * @param message
	 *            the message to format
	 * @return the formatted message
	 */
	protected String formatSingleMessage(String message)
	{
		StringBuffer buffer = new StringBuffer();

		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
		HTMLPrinter.addPageEpilog(buffer);

		return buffer.toString();
	}

	/**
	 * Hook method to formats the given messages.
	 * <p>
	 * Subclasses can change this to create a different format like HTML.
	 * </p>
	 * 
	 * @param messages
	 *            the messages to format (element type: {@link String})
	 * @return the formatted message
	 */
	@SuppressWarnings("rawtypes")
	protected String formatMultipleMessages(List messages)
	{
		StringBuffer buffer = new StringBuffer();

		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer,
				HTMLPrinter.convertToHTMLContent(Messages.CommonAnnotationHover_multipleMarkersMessage));
		HTMLPrinter.startBulletList(buffer);
		Iterator e = messages.iterator();
		while (e.hasNext())
		{
			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
		}
		HTMLPrinter.endBulletList(buffer);
		HTMLPrinter.addPageEpilog(buffer);

		return buffer.toString();
	}

	private boolean isRulerLine(Position position, IDocument document, int line)
	{
		if (position.getOffset() > -1 && position.getLength() > -1)
		{
			try
			{
				return line == document.getLineOfOffset(position.getOffset());
			}
			catch (BadLocationException x)
			{
			}
		}
		return false;
	}

	private IAnnotationModel getAnnotationModel(ISourceViewer viewer)
	{
		if (viewer instanceof ISourceViewerExtension2)
		{
			ISourceViewerExtension2 extension = (ISourceViewerExtension2) viewer;
			return extension.getVisualAnnotationModel();
		}
		return viewer.getAnnotationModel();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isDuplicateAnnotation(Map<Position, Object> messagesAtPosition, Position position, String message)
	{
		if (messagesAtPosition.containsKey(position))
		{
			Object value = messagesAtPosition.get(position);
			if (message.equals(value))
			{
				return true;
			}

			if (value instanceof List)
			{
				List messages = (List) value;
				if (messages.contains(message))
				{
					return true;
				}
				messages.add(message);
			}
			else
			{
				List<Object> messages = new ArrayList<Object>();
				messages.add(value);
				messages.add(message);
				messagesAtPosition.put(position, messages);
			}
		}
		else
			messagesAtPosition.put(position, message);
		return false;
	}

	private boolean includeAnnotation(Annotation annotation, Position position, Map<Position, Object> messagesAtPosition)
	{
		if (!isIncluded(annotation))
		{
			return false;
		}

		String text = annotation.getText();
		return (text != null && !isDuplicateAnnotation(messagesAtPosition, position, text));
	}

	private Set<Annotation> getAnnotationsForLine(ISourceViewer viewer, int line)
	{
		IAnnotationModel model = getAnnotationModel(viewer);
		if (model == null)
		{
			return null;
		}

		IDocument document = viewer.getDocument();
		Set<Annotation> javaAnnotations = new TreeSet<Annotation>(new SimpleMarkerAnnotationComparator());
		Map<Position, Object> messagesAtPosition = new HashMap<Position, Object>();
		Iterator<?> iterator = model.getAnnotationIterator();

		while (iterator.hasNext())
		{
			Annotation annotation = (Annotation) iterator.next();

			Position position = model.getPosition(annotation);
			if (position == null)
			{
				continue;
			}

			if (!isRulerLine(position, document, line))
			{
				continue;
			}

			if (annotation instanceof AnnotationBag)
			{
				AnnotationBag bag = (AnnotationBag) annotation;
				Iterator<?> e = bag.iterator();
				while (e.hasNext())
				{
					annotation = (Annotation) e.next();
					position = model.getPosition(annotation);
					if (position != null && includeAnnotation(annotation, position, messagesAtPosition))
					{
						javaAnnotations.add(annotation);
					}
				}
				continue;
			}

			if (includeAnnotation(annotation, position, messagesAtPosition))
			{
				javaAnnotations.add(annotation);
			}
		}

		return javaAnnotations;
	}

	private class SimpleMarkerAnnotationComparator implements Comparator<Annotation>
	{

		public int compare(Annotation o1, Annotation o2)
		{
			if (o1 instanceof SimpleMarkerAnnotation && o2 instanceof SimpleMarkerAnnotation)
			{
				// Compare the marker offset first. In case it was not set with an offset, compare via the line numbers.
				IMarker m1 = ((SimpleMarkerAnnotation) o1).getMarker();
				IMarker m2 = ((SimpleMarkerAnnotation) o2).getMarker();
				if (m1 != null && m2 != null)
				{
					// try comparing by offset
					int pos1 = m1.getAttribute(IMarker.CHAR_START, -1);
					int pos2 = m2.getAttribute(IMarker.CHAR_START, -1);
					if (pos1 > -1 && pos2 > -1)
					{
						return pos1 - pos2;
					}
					// in case one of the char-start values was not set, try comparing using the line number
					pos1 = m1.getAttribute(IMarker.LINE_NUMBER, -1);
					pos2 = m2.getAttribute(IMarker.LINE_NUMBER, -1);
					if (pos1 > -1 && pos2 > -1)
					{
						return pos1 - pos2;
					}
				}
			}
			// just return 0, as we can't really compare those.
			return 0;
		}

	}
}
