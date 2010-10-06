/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.epl.tests;

import org.eclipse.swt.widgets.Display;

/**
 * Runs the event loop of the given display until {@link #condition()} becomes
 * <code>true</code> or no events have occurred for the supplied timeout.
 * Between running the event loop, {@link Display#sleep()} is called.
 * <p>
 * There is a caveat: the given timeouts must be long enough that the calling
 * thread can enter <code>Display.sleep()</code> before the timeout elapses,
 * otherwise, the waiter may time out before <code>sleep</code> is called and
 * the sleeping thread may never be waken up.
 * </p>
 *
 * @since 3.1
 */
public abstract class DisplayHelper {
	/**
	 * Creates a new instance.
	 */
	protected DisplayHelper() {
	}

	/**
	 * Until {@link #condition()} becomes <code>true</code> or the timeout
	 * elapses, call {@link Display#sleep()} and run the event loop.
	 * <p>
	 * If <code>timeout &lt; 0</code>, the event loop is never driven and
	 * only the condition is checked. If <code>timeout == 0</code>, the event
	 * loop is driven at most once, but <code>Display.sleep()</code> is never
	 * invoked.
	 * </p>
	 *
	 * @param display the display to run the event loop of
	 * @param timeout the timeout in milliseconds
	 * @return <code>true</code> if the condition became <code>true</code>,
	 *         <code>false</code> if the timeout elapsed
	 */
	public final boolean waitForCondition(Display display, long timeout) {
		// if the condition already holds, succeed
		if (condition())
			return true;

		if (timeout < 0)
			return false;

		// if driving the event loop once makes the condition hold, succeed
		// without spawning a thread.
		driveEventQueue(display);
		if (condition())
			return true;

		// if the timeout is negative or zero, fail
		if (timeout == 0)
			return false;

		// repeatedly sleep until condition becomes true or timeout elapses
		DisplayWaiter waiter= new DisplayWaiter(display);
		DisplayWaiter.Timeout timeoutState= waiter.start(timeout);
		boolean condition;
		try {
			do {
				if (display.sleep())
					driveEventQueue(display);
				condition= condition();
			} while (!condition && !timeoutState.hasTimedOut());
		} finally {
			waiter.stop();
		}
		return condition;
	}

	/**
	 * Call {@link Display#sleep()} and run the event loop until the given
	 * timeout has elapsed.
	 * <p>
	 * If <code>timeout &lt; 0</code>, nothing happens. If
	 * <code>timeout == 0</code>, the event loop is driven exactly once, but
	 * <code>Display.sleep()</code> is never invoked.
	 * </p>
	 *
	 * @param display the display to run the event loop of
	 * @param millis the timeout in milliseconds
	 */
	public static void sleep(Display display, long millis) {
		new DisplayHelper() {
			public boolean condition() {
				return false;
			}
		}.waitForCondition(display, millis);
	}

	/**
	 * Call {@link Display#sleep()} and run the event loop once if
	 * <code>sleep</code> returns before the timeout elapses. Returns
	 * <code>true</code> if any events were processed, <code>false</code> if
	 * not.
	 * <p>
	 * If <code>timeout &lt; 0</code>, nothing happens and false is returned.
	 * If <code>timeout == 0</code>, the event loop is driven exactly once,
	 * but <code>Display.sleep()</code> is never invoked.
	 * </p>
	 *
	 * @param display the display to run the event loop of
	 * @param timeout the timeout in milliseconds
	 * @return <code>true</code> if any event was taken off the event queue,
	 *         <code>false</code> if not
	 */
	public static boolean runEventLoop(Display display, long timeout) {
		if (timeout < 0)
			return false;

		if (timeout == 0)
			return driveEventQueue(display);

		// repeatedly sleep until condition becomes true or timeout elapses
		DisplayWaiter waiter= new DisplayWaiter(display);
		DisplayWaiter.Timeout timeoutState= waiter.start(timeout);
		boolean events= false;
		if (display.sleep() && !timeoutState.hasTimedOut()) {
			driveEventQueue(display);
			events= true;
		}
		waiter.stop();
		return events;
	}

	/**
	 * The condition which has to be met in order for {@link #waitForCondition(Display, long)} to
	 * return before the timeout elapses.
	 * 
	 * @return <code>true</code> if the condition is met, <code>false</code> if the event loop
	 *         should be driven some more
	 */
	protected abstract boolean condition();

	/**
	 * Runs the event loop on the given display.
	 *
	 * @param display the display
	 * @return if <code>display.readAndDispatch</code> returned
	 *         <code>true</code> at least once
	 */
	private static boolean driveEventQueue(Display display) {
		boolean events= false;
		while (display.readAndDispatch()) {
			events= true;
		}
		return events;
	}

	/**
	 * Until {@link #condition()} becomes <code>true</code> or the timeout
	 * elapses, call {@link Display#sleep()} and run the event loop.
	 * <p>
	 * If <code>timeout &lt; 0</code>, the event loop is never driven and
	 * only the condition is checked. If <code>timeout == 0</code>, the event
	 * loop is driven at most once, but <code>Display.sleep()</code> is never
	 * invoked.
	 * </p>
	 * <p>
	 * The condition gets rechecked every <code>interval</code> milliseconds, even
	 * if no events were read from the queue.
	 * </p>
	 *
	 * @param display the display to run the event loop of
	 * @param timeout the timeout in milliseconds
	 * @param interval the interval to re-check the condition in milliseconds
	 * @return <code>true</code> if the condition became <code>true</code>,
	 *         <code>false</code> if the timeout elapsed
	 */
	public final boolean waitForCondition(Display display, long timeout, long interval) {
		// if the condition already holds, succeed
		if (condition())
			return true;

		if (timeout < 0)
			return false;

		// if driving the event loop once makes the condition hold, succeed
		// without spawning a thread.
		driveEventQueue(display);
		if (condition())
			return true;

		// if the timeout is negative or zero, fail
		if (timeout == 0)
			return false;

		// repeatedly sleep until condition becomes true or timeout elapses
		DisplayWaiter waiter= new DisplayWaiter(display, true);
		long currentTimeMillis= System.currentTimeMillis();
		long finalTimeout= timeout + currentTimeMillis;
		if (finalTimeout < currentTimeMillis)
			finalTimeout= Long.MAX_VALUE;
		boolean condition;
		try {
			do {
				waiter.restart(interval);
				if (display.sleep())
					driveEventQueue(display);
				condition= condition();
			} while (!condition && finalTimeout > System.currentTimeMillis());
		} finally {
			waiter.stop();
		}
		return condition;
	}

}