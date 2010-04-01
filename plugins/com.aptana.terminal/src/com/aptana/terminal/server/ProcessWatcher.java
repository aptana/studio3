package com.aptana.terminal.server;

public class ProcessWatcher extends Thread
{
	private ProcessWrapper _wrapper;
	
	/**
	 * ProcessWatcher
	 * 
	 * @param wrapper
	 */
	public ProcessWatcher(ProcessWrapper wrapper)
	{
		super("Process watcher");
		
		this._wrapper = wrapper;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		if (this._wrapper != null)
		{
			try
			{
				this._wrapper.getProcess().waitFor();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// fire event here
			this._wrapper.fireProcessEnded();
		}
	}
}
