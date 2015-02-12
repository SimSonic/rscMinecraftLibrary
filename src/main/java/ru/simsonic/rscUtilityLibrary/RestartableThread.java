package ru.simsonic.rscUtilityLibrary;

public abstract class RestartableThread implements Runnable
{
	private Thread thread;
	public final void start()
	{
		stop();
		thread = new Thread(this);
		thread.start();
	}
	public final void stop()
	{
		if(thread != null)
		{
			try
			{
				thread.interrupt();
				thread.join();
			} catch(InterruptedException ex) {
			} finally {
				thread = null;
			}
		}
	}
	public final void join() throws InterruptedException
	{
		if(thread != null)
			thread.join();
	}
}
