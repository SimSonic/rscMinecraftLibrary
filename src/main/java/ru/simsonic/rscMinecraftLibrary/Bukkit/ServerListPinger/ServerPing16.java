package ru.simsonic.rscMinecraftLibrary.Bukkit.ServerListPinger;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.simsonic.rscCommonsLibrary.VarInt;
import ru.simsonic.rscMinecraftLibrary.Bukkit.ServerListPinger.Pinger.StatusResponse;

final class ServerPing16 implements Pinger.VersionedPinger
{
	@Override
	public StatusResponse ping(DataInputStream dis, DataOutputStream dos) throws IOException
	{
		final String hostString = "localhost";
		final short  port = 25565;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream handshake = new DataOutputStream(baos);
		handshake.writeByte(0x00); // packet id for handshake
		VarInt.writeVarInt(handshake, 4); // protocol version
		VarInt.writeVarInt(handshake, hostString.length()); // host length
		handshake.writeBytes(hostString); // host string
		handshake.writeShort(port); // port
		VarInt.writeVarInt(handshake, 1); // state (1 for handshake)
		VarInt.writeVarInt(dos, baos.size()); // prepend size
		dos.write(baos.toByteArray()); // write handshake packet
		dos.writeByte(0x01); // size is only 1
		dos.writeByte(0x00); // packet id for ping
		int size = VarInt.readVarInt(dis); // size of packet
		int id = VarInt.readVarInt(dis); // packet id
		if(id == -1)
			throw new IOException("Premature end of stream.");
		if(id != 0x00) // we want a status response
			throw new IOException("Invalid packetID");
		int length = VarInt.readVarInt(dis); // length of json string
		if(length == -1)
			throw new IOException("Premature end of stream.");
		if(length == 0)
			throw new IOException("Invalid string length.");
		byte[] in = new byte[length];
		dis.readFully(in);  // read json string
		String json = new String(in);
		final long now = System.currentTimeMillis();
		dos.writeByte(0x09); // size of packet
		dos.writeByte(0x01); // 0x01 for ping
		dos.writeLong(now);  // time!?
		VarInt.readVarInt(dis);
		id = VarInt.readVarInt(dis);
		if(id == -1)
			throw new IOException("Premature end of stream.");
		if(id != 0x01)
			throw new IOException("Invalid packetID");
		long pingtime = dis.readLong(); // read response
		final Gson gson = new Gson();
		final StatusResponse result = gson.fromJson(json, StatusResponse.class);
		result.setTime((int)(now - pingtime));
		result.pingerVersion = Pinger.ServerVersion.V_RELEASE_1_6;
		return result;
	}
}
