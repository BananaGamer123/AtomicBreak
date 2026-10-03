package com.atomicbreak;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;

import java.util.concurrent.ThreadLocalRandom;

public class AtomicBreak implements ModInitializer {

    public static final String MOD_ID = "atomicbreak";

    // Chance padrão: 0,000001%
    // 1 em 100 milhões de blocos quebrados.
    public static double chance = 0.00000001D;

    // Potência da explosão fictícia.
    public static float explosionPower = 8.0F;

    @Override
    public void onInitialize() {

        PlayerBlockBreakEvents.AFTER.register(
            (level, player, pos, state, blockEntity) -> {

                if (!(level instanceof ServerLevel serverLevel)) {
                    return;
                }

                if (!(player instanceof ServerPlayer serverPlayer)) {
                    return;
                }

                if (ThreadLocalRandom.current().nextDouble() < chance) {
                    triggerReaction(serverLevel, pos, serverPlayer);
                }
            }
        );
    }

    private static void triggerReaction(
            ServerLevel level,
            BlockPos pos,
            ServerPlayer player) {

        player.sendSystemMessage(
            net.minecraft.network.chat.Component.literal(
                "§c§lFISSAO ATOMICA! §r§7Um atomo foi cortado..."
            )
        );

        level.sendParticles(
            net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            1,
            0,
            0,
            0,
            0
        );

        level.playSound(
            null,
            pos,
            net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
            net.minecraft.sounds.SoundSource.BLOCKS,
            4.0F,
            0.5F
        );

        level.explode(
            player,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            explosionPower,
            Explosion.BlockInteraction.DESTROY
        );
    }
}