package fr.norehc.multimoneymod.events;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.ConfigManager;
import fr.norehc.multimoneymod.models.DropModel;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CaptureListenerEvent {

    public CaptureListenerEvent() {

    }

    @SubscribeEvent
    public void onCapture(CaptureEvent.SuccessfulCapture event) {
        String name = event.getPokemon().getSpecies().getName();
        String palette = event.getPokemon().getPalette().getName();
        Pokemon pokemon = event.getPokemon().getPokemon();
        ServerPlayerEntity player = event.getPlayer();

        ConfigManager.getAllMoneys().forEach((idMoney, dataMoney) -> {
            if (dataMoney.dropModel.containsKey("capture")) {
                boolean isOk = false;


                DropModel param = dataMoney.dropModel.get("capture");
                if (param.selectedPokemon.get(0).equalsIgnoreCase("all")) {
                    if (param.selectedPalette.get(0).equalsIgnoreCase("all")) {
                        isOk = true;
                    } else {
                        if (param.selectedPalette.stream().anyMatch(s -> s.equalsIgnoreCase(palette))) {
                            isOk = true;
                        }
                    }
                } else {
                    for (String pokemonName : param.selectedPokemon) {
                        if (param.selectedPalette.get(0).equalsIgnoreCase("all")) {
                            if (pokemonName.equalsIgnoreCase(name)) isOk = true;
                        } else {
                            if (param.selectedPalette.stream().anyMatch(s -> s.equalsIgnoreCase(palette))) isOk = true;
                        }
                    }
                }

                if (isOk) {
                    AtomicReference<Integer> moneyToGive = new AtomicReference<>(0);
                    Random randomNumbers = new Random();

                    if (isSpecial(pokemon)) {
                        if (pokemon.isShiny()) {
                            param.values.get("legendary").forEach((id, valueDrop) -> {
                                if (randomNumbers.nextInt(100) < valueDrop.probability) {
                                    moneyToGive.updateAndGet(v -> v + randomNumbers.nextInt(valueDrop.valueMax - valueDrop.valueMin) + valueDrop.valueMin);
                                }
                            });
                            param.values.get("shiny").forEach((id, valueDrop) -> {
                                if (randomNumbers.nextInt(100) < valueDrop.probability) {
                                    moneyToGive.updateAndGet(v -> v + randomNumbers.nextInt(valueDrop.valueMax - valueDrop.valueMin) + valueDrop.valueMin);
                                }
                            });
                        } else {
                            param.values.get("legendary").forEach((id, valueDrop) -> {
                                if (randomNumbers.nextInt(100) < valueDrop.probability) {
                                    moneyToGive.updateAndGet(v -> v + randomNumbers.nextInt(valueDrop.valueMax - valueDrop.valueMin) + valueDrop.valueMin);
                                }
                            });
                        }
                    } else if (pokemon.isShiny()) {
                        param.values.get("shiny").forEach((id, valueDrop) -> {
                            if (randomNumbers.nextInt(100) < valueDrop.probability) {
                                moneyToGive.updateAndGet(v -> v + randomNumbers.nextInt(valueDrop.valueMax - valueDrop.valueMin) + valueDrop.valueMin);
                            }
                        });
                    } else {
                        param.values.get("other").forEach((id, valueDrop) -> {
                            if (randomNumbers.nextInt(100) < valueDrop.probability) {
                                moneyToGive.updateAndGet(v -> v + randomNumbers.nextInt(valueDrop.valueMax - valueDrop.valueMin) + valueDrop.valueMin);
                            }
                        });
                    }

                    double alreadyMoney = MultiMoneyMod.playerMoney.get(player.getUUID()).get(idMoney);
                    MultiMoneyMod.playerMoney.get(player.getUUID()).replace(idMoney, alreadyMoney + moneyToGive.get());
                }
            }

        });
    }

    private boolean isSpecial(Pokemon pokemon) {
        return pokemon.isLegendary() || pokemon.isMythical() || pokemon.isUltraBeast();
    }
}
