package fr.norehc.multimoneymod.events;

import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.PlayerMoneyFileManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LoginEvent {

    public LoginEvent() {

    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        MultiMoneyMod.playerMoney.put(player.getUUID(), PlayerMoneyFileManager.loadStats(player.getUUID()));
        //PityLegendaryMod.playersData.put(player.getUUID(), PlayerPityFileManager.loadStats(player.getUUID()));
    }
}
