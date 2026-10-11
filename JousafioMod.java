package com.jousafio;

import com.jousafio.command.RuletaCommand;
import com.jousafio.difficulty.DifficultyManager;
import com.jousafio.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JousafioMod.MOD_ID)
public class JousafioMod {
    public static final String MOD_ID = "jousafio";
    public static final Logger LOGGER = LogManager.getLogger();

    public JousafioMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        ModItems.ITEMS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DifficultyManager());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("JOUSAFIO v1.0 cargado - Listo para el caos");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RuletaCommand.register(event.getDispatcher());
    }
}
