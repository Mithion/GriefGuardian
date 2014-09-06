package com.mithion.griefguardian.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.mithion.griefguardian.claims.Claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketSyncClaims implements IMessage {

	private HashMap<String, ArrayList<Claim>> data;
	
	public PacketSyncClaims(){
		data = new HashMap<String, ArrayList<Claim>>();
	}
	
	public void addBoundingBox(String owner, Claim claim){
		if (!data.containsKey(owner))
			data.put(owner, new ArrayList<Claim>());
		data.get(owner).add(claim);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int numRecords = buf.readInt();
		for (int i = 0; i < numRecords; ++i){
			Claim claim = new Claim(buf);
			if (!data.containsKey(claim.getOwner()))
				data.put(claim.getOwner(), new ArrayList<Claim>());
			data.get(claim.getOwner()).add(claim);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//how many records?
		buf.writeInt(data.size());
		for (String s : data.keySet()){
			ArrayList<Claim> claims = data.get(s);
			for (Claim claim : claims)
				claim.writeToByteBuf(buf);
		}
	}	

	public HashMap<String, ArrayList<Claim>> getData(){
		return (HashMap<String, ArrayList<Claim>>) data.clone();
	}
}
