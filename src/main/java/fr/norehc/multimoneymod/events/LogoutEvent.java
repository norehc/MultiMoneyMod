package fr.norehc.multimoneymod.events;

import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.PlayerMoneyFileManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LogoutEvent {

    public LogoutEvent() {

    }

    @SubscribeEvent
    public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        PlayerMoneyFileManager.saveStats(player.getUUID(), MultiMoneyMod.playerMoney.get(player.getUUID()));
        MultiMoneyMod.playerMoney.remove(player.getUUID());
    }
}
