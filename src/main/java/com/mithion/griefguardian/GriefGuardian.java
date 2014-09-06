package com.mithion.griefguardian;

import com.mithion.griefguardian.commands.ClaimCommand;
import com.mithion.griefguardian.commands.DeleteClaim;
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
    	config = new Config(event.getSuggestedConfigurationFile());
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
    	mgr.registerCommand(new TransferClaim());
    }
}
