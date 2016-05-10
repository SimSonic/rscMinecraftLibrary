package ru.simsonic.rscMinecraftLibrary.AutoUpdater;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.simsonic.rscCommonsLibrary.RestartableThread;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;

public final class BukkitUpdater implements Listener
{
	private final JavaPlugin      plugin;
	private final String          latestURL;
	private final String          chatPrefix;
	private final HashSet<Player> staff = new HashSet<>();
	private final static String   TEXT_IS_LATEST    = "You are using the latest version.";
	private final static String   TEXT_DOWNLOADING  = "Downloading update ...";
	private final static String[] TEXT_DOWNLOAD_ERR = new String[]
	{
		"{_LR}Downloading error!",
		"Cannot download update file. Please try later.",
	};
	private final static String   TEXT_INSTALLING   = "Installing update ...";
	private final static String[] TEXT_INSTALL_ERR  = new String[]
	{
		// EMPTY FOR NOW
	};
	private final static String[] TEXT_UPDATE_OK    = new String[]
	{
		"{_LG}Installation complete!",
		"Please restart your server to avoid errors.",
	};
	public BukkitUpdater(JavaPlugin plugin, String latestURL, String chatPrefix)
	{
		this.plugin     = plugin;
		this.latestURL  = latestURL;
		this.chatPrefix = chatPrefix;
	}
	public void onEnable()
	{
		plugin.getServer().getPluginManager().registerEvents(BukkitUpdater.this, plugin);
		checkUpdate(null);
	}
	public void checkUpdate(Player sender)
	{
		if(sender != null)
			staff.add(sender);
		threadCheck.start();
	}
	public void doUpdate(Player sender)
	{
		if(sender != null)
			staff.add(sender);
		threadUpdate.start();
	}
	private final RestartableThread threadCheck = new RestartableThread()
	{
		@Override
		public void run()
		{
			checkForUpdate();
			final ArrayList<String> lines = latestToLines();
			if(lines != null)
				runLines(lines.toArray(new String[lines.size()]));
			else
				runLines(TEXT_IS_LATEST);
		}
	};
	private final RestartableThread threadUpdate = new RestartableThread()
	{
		@Override
		public void run()
		{
			runLines(TEXT_DOWNLOADING);
			if(downloadUpdate())
			{
				// SUCCESS
				runLines(TEXT_INSTALLING);
				installUpdate();
				if(false) // WOW! :)
					runLines(TEXT_INSTALL_ERR);
				else
					runLines(TEXT_UPDATE_OK);
			} else {
				// FAILED
				runLines(TEXT_DOWNLOAD_ERR);
			}
		}
	};
	private Latest latest = new Latest();
	private void checkForUpdate()
	{
		try
		{
			this.latest = new Gson().fromJson(downloadJson(latestURL), Latest.class);
		} catch(IOException ex) {
			this.latest = new Latest();
		}
		if(latest.version == null)
			latest.version = plugin.getDescription().getVersion();
		if(latest.note == null)
			latest.note = "New version: " + latest.version;
		if(latest.notes == null)
			latest.notes = new String[] { latest.note };
	}
	private void runLines(final String line)
	{
		runLines(new String[] { line });
	}
	private void runLines(final String[] lines)
	{
		final Runnable syncTask = new Runnable()
		{
			@Override
			public synchronized void run()
			{
				// CONSOLE
				final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
				for(String line : lines)
					if(line != null)
						console.sendMessage(GenericChatCodes.processStringStatic(chatPrefix + line));
				// PLAYERS
				for(Player online : staff)
					for(String line : lines)
						if(line != null)
							online.sendMessage(GenericChatCodes.processStringStatic(chatPrefix + line));
				notify();
			}
		};
		try
		{
			synchronized(syncTask)
			{
				plugin.getServer().getScheduler().runTask(plugin, syncTask);
				syncTask.wait();
			}
		} catch(InterruptedException ex) {
		}
	}
	private ArrayList<String> latestToLines()
	{
		// THERE IS NO UPDATE
		if(plugin.getDescription().getVersion().equals(latest.version))
			return null;
		// THERE IS AN UPDATE
		final ArrayList<String> result = new ArrayList<>();
		result.add("New "
			+ (latest.snapshot ? "{_DS}snapshot {_LS}" : "{_WH}release {_LS}")
			+ "version {_LG}" + latest.version + "{_LS} is available!");
		result.addAll(Arrays.asList(latest.notes));
		result.add("Apply this update with command {GOLD}/rscfjd update do");
		return result;
	}
	public void onAdminJoin(Player player, boolean fromEvent)
	{
		staff.add(player);
		if(fromEvent)
		{
			final ArrayList<String> lines = latestToLines();
			if(lines != null)
				for(String line : lines)
					if(line != null)
						player.sendMessage(GenericChatCodes.processStringStatic(chatPrefix + line));
		}
	}
	@EventHandler
	protected void onPlayerQuit(PlayerQuitEvent event)
	{
		staff.add(event.getPlayer());
	}
	@EventHandler
	protected void onPlayerKick(PlayerKickEvent event)
	{
		staff.add(event.getPlayer());
	}
	private static String downloadJson(String url) throws IOException
	{
		try
		{
			final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setUseCaches(false);
			final int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
				return readUnicodeStream(connection.getInputStream());
			throw new IOException(new StringBuilder()
				.append(Integer.toString(responseCode))
				.append("Erroneous result of executing web-method: ")
				.append(connection.getResponseMessage())
				.append("\r\n")
				.append(readUnicodeStream(connection.getErrorStream()))
				.toString());
		} catch(JsonParseException | MalformedURLException ex) {
			throw new IOException(ex);
		} catch(IOException ex) {
			throw ex;
		}
	}
	private static String readUnicodeStream(InputStream is) throws IOException
	{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			final byte[] buffer = new byte[1024];
			for(int length = 0; length != -1; length = is.read(buffer))
				baos.write(buffer, 0, length);
			return new String(baos.toByteArray(), "UTF-8");
		}
	}
	private boolean downloadUpdate()
	{
		final File target = new File(getUpdateTempname());
		try(FileOutputStream fos = new FileOutputStream(target))
		{
			final ReadableByteChannel rbc = Channels.newChannel(new URL(latest.url).openStream());
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.flush();
			return true;
		} catch(IOException ex) {
			System.err.println(ex);
		}
		return false;
	}
	private void installUpdate()
	{
		try
		{
			// SAVE OLD VERSION AS BACKUP
			final File outdatedJarSrc = new File(getCurrentFilename());
			final File outdatedJarDst = new File(getCurrentBackupname());
			outdatedJarDst.delete();
			outdatedJarSrc.renameTo(outdatedJarDst);
		} catch(URISyntaxException ex) {
		}
		// RENAME NEW VERSION
		final File updatedJarSrc = new File(getUpdateTempname());
		final File updatedJarDst = new File(getUpdateFilename());
		updatedJarSrc.renameTo(updatedJarDst);
	}
	private String getCurrentFilename() throws URISyntaxException
	{
		return plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	}
	private String getCurrentBackupname() throws URISyntaxException
	{
		return getCurrentFilename() + "-outdated";
	}
	private String getUpdateFilename()
	{
		return plugin.getDataFolder().getParent() + File.separatorChar + plugin.getName() + "_v" + latest.version + ".jar";
	}
	private String getUpdateTempname()
	{
		return getUpdateFilename() + "-downloaded";
	}
}
