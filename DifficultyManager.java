package com.jousafio.difficulty;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class DifficultyManager {

    private static final Map<String, DifficultyChange> CHANGES = new LinkedHashMap<>();
    public static boolean NO_SAPLINGS = false;
    public static boolean LETHAL_BUTTONS = false;
    public static boolean SANDWICH_MODE = false;
    public static boolean TOXIC_GAS = false;
    public static boolean RANDOM_TELEPORT = false;
    public static boolean NO_NATURAL_REGEN = false;
    public static boolean FIRE_RAIN = false;
    public static boolean MOB_BOOST = false;

    static {
        register("nosaplings", "Arboles sin Saplings", "Los arboles ya no dropean saplings", () -> NO_SAPLINGS = true);
        register("lethalbuttons", "Botones y Placas Letales", "Presionar botones o placas te mata", () -> LETHAL_BUTTONS = true);
        register("sandwich", "El Sandwich", "Y>120 y Y<40 hacen dano", () -> SANDWICH_MODE = true);
        register("toxicgas", "Gas Toxico", "Veneno periodico en superficie", () -> TOXIC_GAS = true);
        register("randomtp", "Teletransporte Aleatorio", "TP random cada cierto tiempo", () -> RANDOM_TELEPORT = true);
        register("noregen", "Sin Regeneracion Natural", "La comida no regenera vida facil", () -> NO_NATURAL_REGEN = true);
        register("firerain", "Lluvia de Fuego", "Cae fuego del cielo", () -> FIRE_RAIN = true);
        register("mobboost", "Mobs Mejorados", "Mobs mas peligrosos", () -> MOB_BOOST = true);
        register("creepers", "Creepers Electrificados", "Creepers mucho mas fuertes", () -> {});
        register("noshields", "Sin Escudos", "Los escudos se rompen rapido", () -> {});
        register("wardens", "Wardens Naturales", "Wardens pueden aparecer", () -> {});
        register("reset", "RESET", "Desactiva todos los cambios", () -> {
            NO_SAPLINGS = LETHAL_BUTTONS = SANDWICH_MODE = TOXIC_GAS = false;
            RANDOM_TELEPORT = NO_NATURAL_REGEN = FIRE_RAIN = MOB_BOOST = false;
        });
    }

    private static void register(String id, String name, String desc, Runnable action) {
        CHANGES.put(id, new DifficultyChange(id, name, desc, action));
    }

    public static List<DifficultyChange> getAllChanges() { return new ArrayList<>(CHANGES.values()); }
    public static DifficultyChange getChangeById(String id) { return CHANGES.get(id.toLowerCase()); }

    public static boolean applyChange(MinecraftServer server, DifficultyChange change) {
        try { change.apply(); return true; } catch (Exception e) { return false; }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!LETHAL_BUTTONS || event.getWorld().isRemote) return;
        if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STONE_BUTTON ||
            event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.OAK_BUTTON ||
            event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STONE_PRESSURE_PLATE ||
            event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.OAK_PRESSURE_PLATE) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ServerPlayerEntity p = (ServerPlayerEntity) event.getPlayer();
                p.setHealth(0);
                p.sendStatusMessage(new StringTextComponent("¡Botones y placas son LETALES!").mergeStyle(TextFormatting.DARK_RED, TextFormatting.BOLD), false);
            }
        }
    }

    private int tick = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tick++;
        MinecraftServer server = net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        if (SANDWICH_MODE && tick % 40 == 0) {
            for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                double y = p.getPosY();
                if (y > 120 || y < 40) {
                    p.attackEntityFrom(net.minecraft.util.DamageSource.MAGIC, 2.0f);
                    p.sendStatusMessage(new StringTextComponent("¡El Sandwich te daña!").mergeStyle(TextFormatting.RED), true);
                }
            }
        }
        if (TOXIC_GAS && tick % 100 == 0) {
            for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                if (p.getPosY() > 60 && p.getPosY() < 100) {
                    p.addPotionEffect(new EffectInstance(Effects.POISON, 60, 0));
                }
            }
        }
        if (RANDOM_TELEPORT && tick % 1200 == 0) {
            for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                ServerWorld w = p.getServerWorld();
                double x = p.getPosX() + (w.rand.nextDouble() - 0.5) * 40;
                double z = p.getPosZ() + (w.rand.nextDouble() - 0.5) * 40;
                double y = w.getHeight(net.minecraft.world.gen.Heightmap.Type.MOTION_BLOCKING, (int)x, (int)z);
                p.teleport(w, x, y, z, p.rotationYaw, p.rotationPitch);
                p.sendStatusMessage(new StringTextComponent("¡Teletransporte aleatorio!").mergeStyle(TextFormatting.AQUA), false);
            }
        }
        if (FIRE_RAIN && tick % 60 == 0) {
            for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                ServerWorld w = p.getServerWorld();
                for (int i = 0; i < 2; i++) {
                    double x = p.getPosX() + (w.rand.nextDouble() - 0.5) * 16;
                    double z = p.getPosZ() + (w.rand.nextDouble() - 0.5) * 16;
                    w.setBlockState(new BlockPos(x, p.getPosY() + 12, z), Blocks.FIRE.getDefaultState());
                }
            }
        }
        if (NO_NATURAL_REGEN && tick % 20 == 0) {
            for (ServerPlayerEntity p : server.getPlayerList().getPlayers()) {
                if (p.getFoodStats().getFoodLevel() > 17) p.getFoodStats().setFoodLevel(17);
            }
        }
    }
}
