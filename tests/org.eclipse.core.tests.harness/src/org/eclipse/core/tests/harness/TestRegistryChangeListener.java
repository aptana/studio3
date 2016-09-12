/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;

/**
 * Allows test cases to wait for event notification.
 */
public class TestRegistryChangeListener implements IRegistryChangeListener {

	/**
	 * Indicates that no matching even has been received
	 */
	public static final int NO_EVENT = -1;

	private List<IRegistryChangeEvent> events = new LinkedList<>();
	private List<Integer> simpleEvents = new LinkedList<>();
	private String xpNamespace;
	private String xpId;
	private String extNamespace;
	private String extId;

	/**
	 * Creates a new listener. The parameters allow filtering events based on extension point/extension's
	 * namespaces/ids.
	 */
	public TestRegistryChangeListener(String xpNamespace, String xpId, String extNamespace, String extId) {
		if (xpId != null && xpNamespace == null) {
			throw new IllegalArgumentException();
		}
		if (extId != null && extNamespace == null) {
			throw new IllegalArgumentException();
		}
		if (xpId == null && extId != null) {
			throw new IllegalArgumentException();
		}
		this.xpNamespace = xpNamespace;
		this.xpId = xpId;
		this.extNamespace = extNamespace;
		this.extId = extId;
	}

	/**
	 * @see IRegistryChangeListener#registryChanged
	 */
	@Override
	public synchronized void registryChanged(IRegistryChangeEvent newEvent) {
		IExtensionDelta delta = null;
		if (xpId != null) {
			if (extId != null) {
				delta = newEvent.getExtensionDelta(xpNamespace, xpId, extNamespace + '.' + extId);
			} else {
				IExtensionDelta[] deltas = newEvent.getExtensionDeltas(xpNamespace, xpId);
				if (deltas.length != 0) {
					delta = deltas[0];
				}
			}
		}
		if (delta == null) {
			return; // this is not the event we are interested in
		}
		events.add(newEvent);
		simpleEvents.add(Integer.valueOf(delta.getKind()));
		notifyAll();
	}

	/**
	 * Returns the first event that is received, blocking for at most <code>timeout</code> milliseconds.
	 * Returns <code>null</code> if a event was not received for the time allowed.
	 * <p>
	 * Note: registry elements referred to by the event returned from this method might be
	 * invalid. Method is preserved for backward compatibility, but users are strongly encouraged
	 * to switch to {@link #eventTypeReceived(long)}.
	 * </p>
	 *
	 * @param timeout the maximum time to wait in milliseconds. If zero, this method will
	 * block until an event is received
	 * @return the first event received, or <code>null</code> if none was received
	 *
	 * @deprecated use {@link #eventTypeReceived(long)} instead
	 */
	public synchronized IRegistryChangeEvent getEvent(long timeout) {
		if (!events.isEmpty()) {
			return events.remove(0);
		}
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			// who cares?
		}
		return events.isEmpty() ? null : (IRegistryChangeEvent) events.remove(0);
	}

	/**
	 * Wait for a registry event that fits IDs specified in the constructor, blocking for
	 * at most <code>timeout</code> milliseconds.
	 * <p>
	 * Note: do NOT mix calls to {@link #getEvent(long)} with calls to this method in the same
	 * instance of this class.
	 * </p>
	 *
	 * @param timeout the maximum time to wait in milliseconds. If zero, this method will
	 * block until an event is received
	 * @return event type
	 *
	 * @since 3.4
	 */
	public synchronized int eventTypeReceived(long timeout) {
		long start = System.currentTimeMillis();
		while (simpleEvents.isEmpty()) {
			try {
				long sleepTime = timeout - (System.currentTimeMillis()-start);
				if (sleepTime <= 0) {
					break;
				}
				wait(sleepTime);
			} catch (InterruptedException e) {
				// who cares?
			}
		}
		return simpleEvents.isEmpty() ? NO_EVENT : simpleEvents.remove(0).intValue();
	}

	/**
	 * Wait for a registry event that fits IDs specified in the constructor, blocking for
	 * at most <code>timeout</code> milliseconds.
	 * <p>
	 * Note: do NOT mix calls to {@link #getEvent(long)} with calls to this method in the same
	 * instance of this class.
	 * </p>
	 *
	 * @param timeout the maximum time to wait in milliseconds. If zero, this method will
	 * block until an event is received
	 * @return <code>true</code> if event was received; <code>false</code> otherwise
	 *
	 * @since 3.4
	 */
	public synchronized boolean eventReceived(long timeout) {
		int notified = eventTypeReceived(timeout);
		return notified != TestRegistryChangeListener.NO_EVENT;
	}

	public void register() {
		Platform.getExtensionRegistry().addRegistryChangeListener(this, xpNamespace);
	}

	public void unregister() {
		Platform.getExtensionRegistry().removeRegistryChangeListener(this);
	}

	public synchronized void reset() {
		events.clear();
		simpleEvents.clear();
	}

}
