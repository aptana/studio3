package com.aptana.editor.common;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.build.IProblem;

public interface ICommonAnnotationModel
{

	/**
	 * Signals the end of problem reporting.
	 * 
	 * @param map
	 *            the map of Marker types to collection of "markers/problems" to report
	 */
	public void reportProblems(Map<String, Collection<IProblem>> map, IProgressMonitor monitor);

}