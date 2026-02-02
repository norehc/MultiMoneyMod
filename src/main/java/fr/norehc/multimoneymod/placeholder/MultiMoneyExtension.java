package fr.norehc.multimoneymod.placeholder;

import com.envyful.papi.api.manager.extensions.type.SimpleExtension;
import com.google.common.collect.Lists;
import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.fileManager.ConfigManager;
import fr.norehc.multimoneymod.models.MoneyModel;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class MultiMoneyExtension extends SimpleExtension<ServerPlayerEntity> {

    private static final String NAME = "balance";
    private static final int PRIORITY = 1;
    private static final List<String> DESCRIPTION = Lists.newArrayList("Gets the player's specific money");
    private static final List<String> EXAMPLES = Lists.newArrayList("%multimoney_balance_newcoins%");

    public MultiMoneyExtension() {
        super(NAME, PRIORITY, DESCRIPTION, EXAMPLES);
    }

    @Override
    public boolean matches(ServerPlayerEntity player, String placeholder) {
        return placeholder.startsWith(this.getName() + "_");
    }

    @Override
    public String parse(ServerPlayerEntity player, String s) {
        String[] args = s.split("_");

        if(!MultiMoneyMod.playerMoney.containsKey(player.getUUID()) || args.length < 2) {
            return "error";
        }

        int moneyId = ConfigManager.getIdMoneyFromName(args[1]);

        if(moneyId == -1) {
            return "error";
        }

        MoneyModel moneyModel = ConfigManager.getMoneyData(moneyId);

        if(!MultiMoneyMod.playerMoney.get(player.getUUID()).containsKey(moneyId)) {
            return ConfigManager.translateColors("0.0" + moneyModel.symbol);
        }

        double playerMoney = MultiMoneyMod.playerMoney.get(player.getUUID()).get(moneyId);

        return ConfigManager.translateColors(playerMoney + moneyModel.symbol);
    }
}
