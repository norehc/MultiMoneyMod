package fr.norehc.multimoneymod.task;

import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.ConfigManager;
import fr.norehc.multimoneymod.fileManager.PlayerMoneyFileManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AutoSave {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter >= ConfigManager.SAVE_INTERVAL_TICKS * 1200) {
            tickCounter = 0;
            saveAllPlayerStats();
        }
    }

    private static void saveAllPlayerStats() {
        MultiMoneyMod.LOGGER.info("[PityLegendary] Sauvegarde automatique des stats des joueurs...");

        MultiMoneyMod.playerMoney.forEach(PlayerMoneyFileManager::saveStats);

        MultiMoneyMod.LOGGER.info("[PityLegendary] Fin de la sauvegarde automatique des stats des joueurs...");

    }
}