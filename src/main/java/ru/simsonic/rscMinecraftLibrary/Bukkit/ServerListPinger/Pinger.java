package ru.simsonic.rscMinecraftLibrary.Bukkit.ServerListPinger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class Pinger
{
	private final static String defaultHost    = "localhost";
	private final static int    defaultPort    = 25565;
	private final static int    defaultTimeout = 7000;
	private final InetSocketAddress target;
	private final int               timeout;
	public static enum ServerVersion
	{
		V_BETA,        // Beta 1.8 ... Release 1.3
		V_RELEASE_1_4, // Release 1.4 ... Release 1.5
		V_RELEASE_1_6, // Release 1.6
		V_RELEASE_1_7, // Release 1.7
		V_RELEASE_1_8, // Release 1.8
		V_UNKNOWN,     // ?!?
	}
	private ServerVersion lastKnownVersion = ServerVersion.V_UNKNOWN;
	public Pinger() throws IOException
	{
		target = new InetSocketAddress(defaultHost, defaultPort);
		this.timeout = defaultTimeout;
		// final Socket socket = new Socket();
		// socket.connect(target, defaultTimeout);
	}
	public Pinger(String host)
	{
		target = new InetSocketAddress(host, defaultPort);
		this.timeout = defaultTimeout;
	}
	public Pinger(String host, int port)
	{
		target = new InetSocketAddress(host, port);
		this.timeout = defaultTimeout;
	}
	public Pinger(String host, int port, int timeout)
	{
		target = new InetSocketAddress(host, port);
		this.timeout = timeout;
	}
	public static class StatusResponse
	{
		ServerVersion pingerVersion;
		String  description;
		Players players;
		Version version;
		String  favicon;
		int     time;
		public String getDescription()
		{
			return description;
		}
		public Players getPlayers()
		{
			return players;
		}
		public Version getVersion()
		{
			return version;
		}
		public String getFavicon()
		{
			return favicon;
		}
		public int getTime()
		{
			return time;
		}
		public void setTime(int time)
		{
			this.time = time;
		}
	}
	public static class Players
	{
		int max;
		int online;
		List<Player> sample;
		public int getMax()
		{
			return max;
		}
		public int getOnline()
		{
			return online;
		}
		public List<Player> getSample()
		{
			return sample;
		}
	}
	public static class Player
	{
		String name;
		String id;
		public String getName()
		{
			return name;
		}
		public String getId()
		{
			return id;
		}
	}
	public static class Version
	{
		String name;
		String protocol;
		public String getName()
		{
			return name;
		}
		public String getProtocol()
		{
			return protocol;
		}
	}
	interface VersionedPinger
	{
		public StatusResponse ping(DataInputStream dis, DataOutputStream dos) throws IOException;
	}
	StatusResponse ping() throws IOException
	{
		for(ServerVersion version : ServerVersion.values())
		{
			try
			{
				final StatusResponse result = ping(version);
				this.lastKnownVersion = version;
				return result;
			} catch(IOException ex) {
			}
		}
		throw new IOException("Cannot detect version");
	}
	public StatusResponse ping(ServerVersion knownVersion) throws IOException
	{
		try(final Socket socket = new Socket())
		{
			socket.connect(target, timeout);
			try(
				final DataInputStream dis = new DataInputStream(socket.getInputStream());
				final DataOutputStream dos = new DataOutputStream(socket.getOutputStream()))
			{
				switch(knownVersion)
				{
					case V_BETA:
					case V_RELEASE_1_4:
						return new ServerPing15().ping(dis, dos);
					case V_RELEASE_1_6:
						return new ServerPing16().ping(dis, dos);
					case V_RELEASE_1_7:
					case V_RELEASE_1_8:
					case V_UNKNOWN:
						break;
				}
				throw new IOException("This version is still unsuported by pinger.");
			}
		}
	}
}
