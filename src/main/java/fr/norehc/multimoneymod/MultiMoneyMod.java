package fr.norehc.multimoneymod;

import com.envyful.papi.api.PlaceholderFactory;
import com.pixelmonmod.pixelmon.Pixelmon;
import fr.norehc.multimoneymod.command.MultiMoneyCommand;
import fr.norehc.multimoneymod.events.CaptureListenerEvent;
import fr.norehc.multimoneymod.events.EndBattleEvent;
import fr.norehc.multimoneymod.events.LoginEvent;
import fr.norehc.multimoneymod.events.LogoutEvent;
import fr.norehc.multimoneymod.fileManager.ConfigManager;
import fr.norehc.multimoneymod.placeholder.MultiMoneyPlaceHolder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("multimoneymod")
public class MultiMoneyMod {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static MultiMoneyMod multiMoney;
    public static Map<UUID, Map<Integer, Double>> playerMoney = new HashMap<>();

    public MultiMoneyMod() {
        multiMoney = this;
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LoginEvent());
        MinecraftForge.EVENT_BUS.register(new LogoutEvent());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("MultiMoney Starting");
        Pixelmon.EVENT_BUS.register(new EndBattleEvent());
        Pixelmon.EVENT_BUS.register(new CaptureListenerEvent());
        PlaceholderFactory.register(new MultiMoneyPlaceHolder());
        LOGGER.info("MultiMoney Loading Config");
        ConfigManager.loadConfig();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        MultiMoneyCommand.register(event.getDispatcher());
    }
}
