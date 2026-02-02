package fr.norehc.multimoneymod.placeholder;

import com.envyful.papi.api.manager.AbstractPlaceholderManager;
import net.minecraft.entity.player.ServerPlayerEntity;


public class MultiMoneyPlaceHolder extends AbstractPlaceholderManager<ServerPlayerEntity> {

    private static final String IDENTIFIER = "multimoney";
    private static final String[] AUTHORS = new String[] { "Norehc" };
    private static final String VERSION = "1.0.0";
    private static final String NAME = "multimoney";

    public MultiMoneyPlaceHolder() {
        super(IDENTIFIER, AUTHORS, VERSION, NAME, ServerPlayerEntity.class);

        this.registerPlaceholder(new MultiMoneyExtension());
    }
}
