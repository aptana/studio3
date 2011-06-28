/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.epl.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.swt.widgets.Display;

/**
 * Implements the thread that will wait for the timeout and wake up the display so it does not wait forever. The thread
 * may be restarted after it was stopped or timed out.
 * 
 * @since 3.1
 */
@SuppressWarnings("deprecation")
final class DisplayWaiter
{
	/**
	 * Timeout state of a display waiter thread.
	 */
	public final class Timeout
	{
		boolean fTimeoutState = false;

		/**
		 * Returns <code>true</code> if the timeout has been reached, <code>false</code> if not.
		 * 
		 * @return <code>true</code> if the timeout has been reached, <code>false</code> if not
		 */
		public boolean hasTimedOut()
		{
			synchronized (fMutex)
			{
				return fTimeoutState;
			}
		}

		void setTimedOut(boolean timedOut)
		{
			fTimeoutState = timedOut;
		}

		Timeout(boolean initialState)
		{
			fTimeoutState = initialState;
		}
	}

	// configuration
	private final Display fDisplay;
	private final Object fMutex = new Object();
	private final boolean fKeepRunningOnTimeout;

	/*
	 * State -- possible transitions: STOPPED -> RUNNING RUNNING -> STOPPED RUNNING -> IDLE IDLE -> RUNNING IDLE ->
	 * STOPPED
	 */
	private static final int RUNNING = 1 << 1;
	private static final int STOPPED = 1 << 2;
	private static final int IDLE = 1 << 3;

	/** The current state. */
	private int fState;
	/** The time in milliseconds (see Date) that the timeout will occur. */
	private long fNextTimeout;
	/** The thread. */
	private Thread fCurrentThread;
	/** The timeout state of the current thread. */
	private Timeout fCurrentTimeoutState;

	/**
	 * Creates a new instance on the given display and timeout.
	 * 
	 * @param display
	 *            the display to run the event loop of
	 */
	public DisplayWaiter(Display display)
	{
		this(display, false);
	}

	/**
	 * Creates a new instance on the given display and timeout.
	 * 
	 * @param display
	 *            the display to run the event loop of
	 * @param keepRunning
	 *            <code>true</code> if the thread should be kept running after timing out
	 */
	public DisplayWaiter(Display display, boolean keepRunning)
	{
		Assert.assertNotNull(display);
		fDisplay = display;
		fState = STOPPED;
		fKeepRunningOnTimeout = keepRunning;
	}

	/**
	 * Starts the timeout thread if it is not currently running. Nothing happens if a thread is already running.
	 * 
	 * @param delay
	 *            the delay from now in milliseconds
	 * @return the timeout state which can be queried for its timed out status
	 */
	public Timeout start(long delay)
	{
		Assert.assertTrue(delay > 0);
		synchronized (fMutex)
		{
			switch (fState)
			{
				case STOPPED:
					startThread();
					setNextTimeout(delay);
					break;
				case IDLE:
					unhold();
					setNextTimeout(delay);
					break;
			}

			return fCurrentTimeoutState;
		}
	}

	/**
	 * Sets the next timeout to <em>current time</em> plus <code>delay</code>.
	 * 
	 * @param delay
	 *            the delay until the next timeout occurs in milliseconds from now
	 */
	private void setNextTimeout(long delay)
	{
		long currentTimeMillis = System.currentTimeMillis();
		long next = currentTimeMillis + delay;
		if (next > currentTimeMillis)
			fNextTimeout = next;
		else
			fNextTimeout = Long.MAX_VALUE;
	}

	/**
	 * Starts the thread if it is not currently running; resets the timeout if it is.
	 * 
	 * @param delay
	 *            the delay from now in milliseconds
	 * @return the timeout state which can be queried for its timed out status
	 */
	public Timeout restart(long delay)
	{
		Assert.assertTrue(delay > 0);
		synchronized (fMutex)
		{
			switch (fState)
			{
				case STOPPED:
					startThread();
					break;
				case IDLE:
					unhold();
					break;
			}
			setNextTimeout(delay);

			return fCurrentTimeoutState;
		}
	}

	/**
	 * Stops the thread if it is running. If not, nothing happens. Another thread may be started by calling
	 * {@link #start(long)} or {@link #restart(long)}.
	 */
	public void stop()
	{
		synchronized (fMutex)
		{
			if (tryTransition(RUNNING | IDLE, STOPPED))
				fMutex.notifyAll();
		}
	}

	/**
	 * Puts the reaper thread on hold but does not stop it. It may be restarted by calling {@link #start(long)} or
	 * {@link #restart(long)}.
	 */
	public void hold()
	{
		synchronized (fMutex)
		{
			// nothing to do if there is no thread
			if (tryTransition(RUNNING, IDLE))
				fMutex.notifyAll();
		}
	}

	/**
	 * Transition to <code>RUNNING</code> and clear the timed out flag. Assume current state is <code>IDLE</code>.
	 */
	private void unhold()
	{
		checkedTransition(IDLE, RUNNING);
		fCurrentTimeoutState = new Timeout(false);
		fMutex.notifyAll();
	}

	/**
	 * Start the thread. Assume the current state is <code>STOPPED</code>.
	 */
	private void startThread()
	{
		checkedTransition(STOPPED, RUNNING);
		fCurrentTimeoutState = new Timeout(false);
		fCurrentThread = new Thread()
		{
			/**
			 * Exception thrown when a thread notices that it has been stopped and a new thread has been started.
			 */
			final class ThreadChangedException extends Exception
			{
				private static final long serialVersionUID = 1L;
			}

			/*
			 * @see java.lang.Runnable#run()
			 */
			public void run()
			{
				try
				{
					run2();
				}
				catch (InterruptedException e)
				{
					// ignore and end the thread - we never interrupt ourselves,
					// so it must be an external entity that interrupted us
					Logger.global.log(Level.FINE, "", e);
				}
				catch (ThreadChangedException e)
				{
					// the thread was stopped and restarted before we got out
					// of a wait - we're no longer used
					// we might have been notified instead of the current thread,
					// so wake it up
					Logger.global.log(Level.FINE, "", e);
					synchronized (fMutex)
					{
						fMutex.notifyAll();
					}
				}
			}

			/**
			 * Runs the thread.
			 * 
			 * @throws InterruptedException
			 *             if the thread was interrupted
			 * @throws ThreadChangedException
			 *             if the thread changed
			 */
			private void run2() throws InterruptedException, ThreadChangedException
			{
				synchronized (fMutex)
				{
					checkThread();
					tryHold(); // wait / potential state change
					assertStates(STOPPED | RUNNING);

					while (isState(RUNNING))
					{
						waitForTimeout(); // wait / potential state change

						if (isState(RUNNING))
							timedOut(); // state change
						assertStates(STOPPED | IDLE);

						tryHold(); // wait / potential state change
						assertStates(STOPPED | RUNNING);
					}
					assertStates(STOPPED);
				}
			}

			/**
			 * Check whether the current thread is this thread, throw an exception otherwise.
			 * 
			 * @throws ThreadChangedException
			 *             if the current thread changed
			 */
			private void checkThread() throws ThreadChangedException
			{
				if (fCurrentThread != this)
					throw new ThreadChangedException();
			}

			/**
			 * Waits until the next timeout occurs.
			 * 
			 * @throws InterruptedException
			 *             if the thread was interrupted
			 * @throws ThreadChangedException
			 *             if the thread changed
			 */
			private void waitForTimeout() throws InterruptedException, ThreadChangedException
			{
				long delta;
				while (isState(RUNNING) && (delta = fNextTimeout - System.currentTimeMillis()) > 0)
				{
					delta = Math.max(delta, 50); // wait at least 50ms in order to avoid timing out before the display
													// is going to sleep
					Logger.global.finest("sleeping for " + delta + "ms");
					fMutex.wait(delta);
					checkThread();
				}
			}

			/**
			 * Sets the timed out flag and wakes up the display. Transitions to <code>IDLE</code> (if in keep-running
			 * mode) or <code>STOPPED</code>.
			 */
			private void timedOut()
			{
				Logger.global.finer("timed out");
				fCurrentTimeoutState.setTimedOut(true);
				fDisplay.wake(); // wake up call!
				if (fKeepRunningOnTimeout)
					checkedTransition(RUNNING, IDLE);
				else
					checkedTransition(RUNNING, STOPPED);
			}

			/**
			 * Waits while the state is <code>IDLE</code>, then returns. The state must not be <code>RUNNING</code> when
			 * calling this method. The state is either <code>STOPPED</code> or <code>RUNNING</code> when the method
			 * returns.
			 * 
			 * @throws InterruptedException
			 *             if the thread was interrupted
			 * @throws ThreadChangedException
			 *             if the thread has changed while on hold
			 */
			private void tryHold() throws InterruptedException, ThreadChangedException
			{
				while (isState(IDLE))
				{
					fMutex.wait(0);
					checkThread();
				}
				assertStates(STOPPED | RUNNING);
			}
		};

		fCurrentThread.start();
	}

	/**
	 * Transitions to <code>nextState</code> if the current state is one of <code>possibleStates</code>. Returns
	 * <code>true</code> if the transition happened, <code>false</code> otherwise.
	 * 
	 * @param possibleStates
	 *            the states which trigger a transition
	 * @param nextState
	 *            the state to transition to
	 * @return <code>true</code> if the transition happened, <code>false</code> otherwise
	 */
	private boolean tryTransition(int possibleStates, int nextState)
	{
		if (isState(possibleStates))
		{
			Logger.global.finer(name(fState) + " > " + name(nextState) + " (" + name(possibleStates) + ")");
			fState = nextState;
			return true;
		}
		Logger.global.finest("noTransition" + name(fState) + " !> " + name(nextState) + " (" + name(possibleStates)
				+ ")");
		return false;
	}

	/**
	 * Checks the <code>possibleStates</code> and throws an assertion if it is not met, then transitions to
	 * <code>nextState</code>.
	 * 
	 * @param possibleStates
	 *            the allowed states
	 * @param nextState
	 *            the state to transition to
	 */
	private void checkedTransition(int possibleStates, int nextState)
	{
		assertStates(possibleStates);
		Logger.global.finer(name(fState) + " > " + name(nextState));
		fState = nextState;
	}

	/**
	 * Implements state consistency checking.
	 * 
	 * @param states
	 *            the allowed states
	 * @throws junit.framework.AssertionFailedError
	 *             if the current state is not in <code>states</code>
	 */
	private void assertStates(int states)
	{
		Assert.assertTrue("illegal state", isState(states));
	}

	/**
	 * Answers <code>true</code> if the current state is in the given <code>states</code>.
	 * 
	 * @param states
	 *            the possible states
	 * @return <code>true</code> if the current state is in the given states, <code>false</code> otherwise
	 */
	private boolean isState(int states)
	{
		return (states & fState) == fState;
	}

	/**
	 * Pretty print the given states.
	 * 
	 * @param states
	 *            the states
	 * @return a string representation of the states
	 */
	private String name(int states)
	{
		StringBuffer buf = new StringBuffer();
		boolean comma = false;
		if ((states & RUNNING) == RUNNING)
		{
			buf.append("RUNNING");
			comma = true;
		}
		if ((states & STOPPED) == STOPPED)
		{
			if (comma)
				buf.append(",");
			buf.append("STOPPED");
			comma = true;
		}
		if ((states & IDLE) == IDLE)
		{
			if (comma)
				buf.append(",");
			buf.append("IDLE");
		}
		return buf.toString();
	}

}