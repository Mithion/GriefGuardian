package com.mithion.griefguardian;

import java.io.File;

import com.mithion.griefguardian.claims.ClaimManager;
import com.mithion.griefguardian.commands.ClaimCommand;
import com.mithion.griefguardian.commands.DeleteClaim;
import com.mithion.griefguardian.commands.Execute;
import com.mithion.griefguardian.commands.HideClaims;
import com.mithion.griefguardian.commands.ModifyACL;
import com.mithion.griefguardian.commands.ShowClaims;
import com.mithion.griefguardian.commands.TransferClaim;
import com.mithion.griefguardian.config.Config;
import com.mithion.griefguardian.eventhandlers.ClaimGuardEventHandler;
import com.mithion.griefguardian.network.PacketSyncClaims;
import com.mithion.griefguardian.network.PacketSyncMessageHandler;
import com.mithion.griefguardian.proxy.CommonProxy;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Main class for GriefGuardian
 * A forge mod dedicated to anti-griefing
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
@Mod(modid = GriefGuardian.MODID, version = GriefGuardian.VERSION)
public class GriefGuardian
{
	@SidedProxy(modId=GriefGuardian.MODID, clientSide="com.mithion.griefguardian.proxy.ClientProxy", serverSide="com.mithion.griefguardian.proxy.CommonProxy")
	public static CommonProxy proxy;
	
    public static final String MODID = "griefguardian";
    public static final String VERSION = "0.0.0.1";
    
    public static Config config;
    public static SimpleNetworkWrapper networkWrapper;
    
    @Instance
    public static GriefGuardian instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	//init config
    	File configSave = event.getSuggestedConfigurationFile();
    	config = new Config(configSave);
    	//locate save dir for claim data based on suggested config file location
    	File claimSave = new File(configSave.getParentFile().getParentFile().getAbsolutePath() + File.separatorChar + "mods" + File.separatorChar + "ClaimData");
    	claimSave.mkdirs();
    	ClaimManager.instance.setSaveLocation(claimSave);
    	
    	//create network channel
    	networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    	//register messages
    	networkWrapper.registerMessage(PacketSyncMessageHandler.class, PacketSyncClaims.class, 0, Side.CLIENT);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartedEvent event){
    	ServerCommandManager mgr = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
    	mgr.registerCommand(new ClaimCommand());
    	mgr.registerCommand(new DeleteClaim());
    	mgr.registerCommand(new ModifyACL());
    	mgr.registerCommand(new ShowClaims());
    	mgr.registerCommand(new HideClaims());
    	mgr.registerCommand(new TransferClaim());
    	mgr.registerCommand(new Execute());
    }
}
