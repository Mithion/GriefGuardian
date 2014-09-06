package com.mithion.griefguardian.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.mithion.griefguardian.claims.Claim;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

/**
 * Message for transferring all claims owned by a player from server
 * to client.  Needed so clients can render their claims.
 * 
 * Also can be used to send an update to a single claim, and to tell the 
 * client player to stop rendering claims.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class PacketSyncClaims implements IMessage {

	private HashMap<String, ArrayList<Claim>> data;
	//causes the claims to be updated rather than overwriting the entire list.
	//this is useful for updating one or two altered claims without needing the 
	//network overhead of all of them
	private boolean updateClaims = false;
	private boolean isStopRenderPacket = false;

	public PacketSyncClaims(){
		data = new HashMap<String, ArrayList<Claim>>();
	}

	public void addBoundingBox(String owner, Claim claim){
		if (!data.containsKey(owner))
			data.put(owner, new ArrayList<Claim>());
		data.get(owner).add(claim);
	}

	/**
	 * Causes the claims to be updated rather than overwriting the entire list.
	 * This is useful for updating one or two altered claims without needing the 
	 * network overhead of updating all of them.
	 */
	public void setSingleClaimUpdate(){
		this.updateClaims = true;
	}

	/**
	 * Sets this packet as a stop render packet.  This will cause all added bounding boxes
	 * to be ignored and left out of the packet data.  Used when a client runs the /hideclaims
	 * command.
	 */
	public void setStopRenderPacket(){
		this.isStopRenderPacket = true;
	}

	public boolean isStopRenderPacket(){
		return this.isStopRenderPacket;
	}

	public boolean updateClaims(){
		return this.updateClaims;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.isStopRenderPacket = buf.readBoolean();
		if (!isStopRenderPacket){
			this.updateClaims = buf.readBoolean();
			int numRecords = buf.readInt();
			for (int i = 0; i < numRecords; ++i){
				int numClaimsInList = buf.readInt();
				for (int x = 0; x < numClaimsInList; x++){
					Claim claim = new Claim(buf);
					if (!data.containsKey(claim.getOwner()))
						data.put(claim.getOwner(), new ArrayList<Claim>());
					data.get(claim.getOwner()).add(claim);
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (isStopRenderPacket){
			buf.writeBoolean(true);
		}else{
			buf.writeBoolean(false);
			buf.writeBoolean(updateClaims);
			//how many records?
			buf.writeInt(data.size());
			for (String s : data.keySet()){
				ArrayList<Claim> claims = data.get(s);
				buf.writeInt(claims.size());
				for (Claim claim : claims)
					claim.writeToByteBuf(buf);
			}
		}
	}	

	public HashMap<String, ArrayList<Claim>> getData(){
		return (HashMap<String, ArrayList<Claim>>) data.clone();
	}
}
