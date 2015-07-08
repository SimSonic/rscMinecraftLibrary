package ru.simsonic.rscMinecraftLibrary.Bukkit.ServerListPinger;

import java.io.*;
import java.nio.charset.Charset;
import ru.simsonic.rscMinecraftLibrary.Bukkit.ServerListPinger.Pinger.StatusResponse;

public final class ServerPing15 implements Pinger.VersionedPinger
{
	@Override
	public StatusResponse ping(DataInputStream dis, DataOutputStream dos) throws IOException
	{
		try
		{
			final InputStreamReader inputStreamReader = new InputStreamReader(dis, Charset.forName("UTF-16BE"));
			dos.write(new byte[] { (byte)0xFE, (byte)0x01 });
			final int packetId = dis.read();
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
			final StatusResponse result = new Pinger.StatusResponse();
			if(string.startsWith("§"))
			{
				// Release 1.4 ... Release 1.5
				final String[] data = string.split("\0");
				result.version = new Pinger.Version();
				result.version.protocol = data[0].substring(1) + " (" + data[1] + ")";
				result.version.name = data[2];
				result.description = data[3];
				result.players = new Pinger.Players();
				result.players.online = Integer.parseInt(data[4]);
				result.players.max = Integer.parseInt(data[5]);
				result.pingerVersion = Pinger.ServerVersion.V_RELEASE_1_4;
			} else {
				// Beta 1.8 ... Release 1.3
				final String[] data = string.split("§");
				result.description = data[0];
				result.players = new Pinger.Players();
				result.players.online = Integer.parseInt(data[1]);
				result.players.max = Integer.parseInt(data[2]);
				result.pingerVersion = Pinger.ServerVersion.V_BETA;
			}
			return result;
		} catch(IOException ex) {
			throw(ex);
		} catch(NumberFormatException ex) {
			throw new IOException(ex);
		}
	}
}
