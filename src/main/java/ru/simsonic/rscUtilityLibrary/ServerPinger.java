package ru.simsonic.rscUtilityLibrary;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public final class ServerPinger
{
	// IN
	private final String address;
	private final int port;
	private final int timeout;
	private final boolean verbose;
	// OUT
	private String gameVersion = "1.3.2";
	private String motd;
	private int playersOnline = 0;
	private int playersMax = 0;
	// SET
	public ServerPinger(String address, int port, int timeout, boolean verbose)
	{
		this.address = address;
		this.port = port;
		this.timeout = timeout;
		this.verbose = verbose;
	}
	public ServerPinger(String address, int port)
	{
		this(address, port, 4000, false);
	}
	// GET
	public String getGameVersion()
	{
		return this.gameVersion;
	}
	public String getMotd()
	{
		return this.motd;
	}
	public int getPlayersOnline()
	{
		return this.playersOnline;
	}
	public int getPlayersMax()
	{
		return this.playersMax;
	}
	// RUN
	public boolean ping()
	{
		try(Socket socket = new Socket())
		{
			socket.setSoTimeout(this.timeout);
			socket.connect(new InetSocketAddress(this.address, this.port), this.timeout);
			try
			{
				final InputStream inputStream = socket.getInputStream();
				final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-16BE"));
				try
				{
					final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataOutputStream.write(new byte[] { (byte)0xFE, (byte)0x01 });
					final int packetId = inputStream.read();
					if(packetId == -1)
						throw new IOException("Premature end of stream.");
					if(packetId != 0xFF)
						throw new IOException("Invalid packet ID (" + packetId + ").");
					final int length = inputStreamReader.read();
					if(length == -1)
						throw new IOException("Premature end of stream.");
					if(length == 0)
						throw new IOException("Invalid string length.");
					final char[] chars = new char[length];
					if(inputStreamReader.read(chars, 0, length) != length)
						throw new IOException("Premature end of stream.");
					final String string = new String(chars);
					if(string.startsWith("§"))
					{
						final String[] data = string.split("\0");
						// this.pingVersion = Integer.parseInt(data[0].substring(1));
						// this.protocolVersion = Integer.parseInt(data[1]);
						this.gameVersion = data[2];
						this.motd = data[3];
						this.playersOnline = Integer.parseInt(data[4]);
						this.playersMax = Integer.parseInt(data[5]);
					} else {
						final String[] data = string.split("§");
						this.motd = data[0];
						this.playersOnline = Integer.parseInt(data[1]);
						this.playersMax = Integer.parseInt(data[2]);
					}
				} catch(IOException | NumberFormatException ex) {
					throw(ex);
				}
			} catch(IOException | NumberFormatException ex) {
				throw(ex);
			}
		} catch(Exception ex) {
			if(verbose)
				System.out.println("[vc] Ping(" + this.address + ":" + Integer.toString(port)
					+ "), timeout = " + Integer.toString(timeout) + ", contents:\n" + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}
}
