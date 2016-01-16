package ru.simsonic.rscMinecraftLibrary.AutoUpdater;

import java.io.File;
import java.io.IOException;
import ru.simsonic.rscCommonsLibrary.GenericWebConnection;

public class AutoUpdater
{
	private final String url;
	private final Object parent;
	private Latest latest = new Latest();
	public AutoUpdater(Object parent, String latestJsonURL)
	{
		this.url    = latestJsonURL;
		this.parent = parent;
	}
	public Latest checkForUpdate()
	{
		try
		{
			this.latest = GenericWebConnection.webExecute(url, "", Latest.class);
		} catch(IOException ex) {
		}
		return latest;
	}
	public void downloadUpdate()
	{
	}
	public void applyUpdate()
	{
		// RENAME OLD VERSION
		final String outdatedJarPath = parent.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		final File outdatedJarSrc = new File(outdatedJarPath);
		final File outdatedJarDst = new File(outdatedJarPath + "-outdated");
		outdatedJarSrc.renameTo(outdatedJarDst);
	}
}
