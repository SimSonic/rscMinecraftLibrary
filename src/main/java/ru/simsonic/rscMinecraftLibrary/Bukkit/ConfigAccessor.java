package ru.simsonic.rscMinecraftLibrary.Bukkit;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigAccessor implements Closeable
{
	private final Plugin plugin;
	private final String fileName;
	private File configFile;
	private FileConfiguration fileConfiguration;
	public ConfigAccessor(Plugin plugin, String fileName)
	{
		this.plugin = plugin;
		this.fileName = fileName;
		if(plugin.getDataFolder() != null)
			this.configFile = new File(plugin.getDataFolder(), fileName);
	}
	public void reloadConfig()
	{
		fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
		InputStream defConfigStream = plugin.getResource(fileName);
		if(defConfigStream != null)
		{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
				new InputStreamReader(defConfigStream, Charset.forName("UTF-8")));
			fileConfiguration.setDefaults(defConfig);
		}
	}
	public FileConfiguration getConfig()
	{
		if(fileConfiguration == null)
			this.reloadConfig();
		return fileConfiguration;
	}
	public void saveConfig()
	{
		try
		{
			if(fileConfiguration != null && configFile != null)
				getConfig().save(configFile);
		} catch(IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "[rscAPI] Could not save config to " + configFile, ex.getLocalizedMessage());
		}
	}
	public void saveDefaultConfig()
	{
		if(!configFile.exists())
			this.plugin.saveResource(fileName, false);
	}
	@Override
	public void close()
	{
		saveConfig();
	}
}