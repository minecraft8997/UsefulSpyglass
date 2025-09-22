package com.brokenkeyboard.usefulspyglass.handler;

import com.brokenkeyboard.usefulspyglass.EntityFinder;
import com.brokenkeyboard.usefulspyglass.InfoOverlay;
import com.brokenkeyboard.usefulspyglass.ModRegistry;
import com.brokenkeyboard.usefulspyglass.platform.Services;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ClientHandler {
    public interface Pickable {
        HitResult pick(Camera camera, double interactionRange, float partialTick, MultiPlayerGameMode gameMode);
    }

    public static Boolean HAS_SHOULDER_SURFING;
    public static Pickable SHOULDER_SURFING_PICK_FUNCTION;

    public static void handleClientTick(Minecraft client) {
        Player player = client.player;
        Camera camera = client.gameRenderer.getMainCamera();
        if (player != null && player.isScoping()) {
            Entity cameraEntity = camera.getEntity();
            HitResult result = null;
            if (HAS_SHOULDER_SURFING != null && HAS_SHOULDER_SURFING) {
                result = SHOULDER_SURFING_PICK_FUNCTION.pick(camera, 0, client.getDeltaFrameTime(), client.gameMode);
            }
            if (result == null) {
                result = EntityFinder.getAimedObject(player.level(), cameraEntity, camera.getPosition(), cameraEntity.getViewVector(client.getFrameTimeNs()));
            }
            if (result instanceof BlockHitResult blockHit && client.player != null &&
                    client.player.level().getBlockState(blockHit.getBlockPos()).getBlock() instanceof AirBlock
            ) {
                result = null;
            }
            InfoOverlay.setHitResult(result);
            if (!player.getCooldowns().isOnCooldown(Items.SPYGLASS) && client.options.keyAttack.isDown()
                    && (Services.PLATFORM.hasSpyglassEnchant(player, ModRegistry.MARKING) || Services.PLATFORM.hasSpyglassEnchant(player, ModRegistry.SPOTTER))) {
                Services.PLATFORM.useSpyglassEnch();
            }
        } else {
            InfoOverlay.setHitResult(null);
        }
    }
}