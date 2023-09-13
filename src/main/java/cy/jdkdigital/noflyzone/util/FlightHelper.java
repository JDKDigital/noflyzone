package cy.jdkdigital.noflyzone.util;

import cy.jdkdigital.noflyzone.Config;
import cy.jdkdigital.noflyzone.NoFlyZone;
import cy.jdkdigital.noflyzone.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlightHelper
{
    public static boolean isAllowedToFly(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return true;
        }
        return isAllowedDimension(player) && isAllowedBiome(player) && isAllowedStructure(player);
    }

    private static boolean isAllowedDimension(Player player) {
        return Config.dimensions.isEmpty() || !Config.dimensions.contains(player.level().dimension());
    }

    private static boolean isAllowedBiome(Player player) {
        if (Config.enableBiomeCheck) {
            return !player.level().getBiome(player.blockPosition()).is(NoFlyZone.BIOME_BLACKLIST);
        }
        return true;
    }

    private static boolean isAllowedStructure(Player player) {
        if (Config.enableStructureCheck && player.level() instanceof ServerLevel serverLevel) {
            for (ResourceKey<Structure> structureKey: getBlacklistedStructures(serverLevel)){
                var test = serverLevel.structureManager().getStructureWithPieceAt(player.blockPosition(), structureKey);
                if (test.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }

    static List<ResourceKey<Structure>> cachedStructureResourceKeys = new ArrayList<>();
    private static List<ResourceKey<Structure>> getBlacklistedStructures(ServerLevel level) {
        if (cachedStructureResourceKeys.isEmpty()) {
            var structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
            structureRegistry.getTag(NoFlyZone.STRUCTURE_BLACKLIST).ifPresent(holders -> cachedStructureResourceKeys = holders.stream().map(structureHolder -> {
                return structureRegistry.getResourceKey(structureHolder.get()).orElse(null);
            }).filter(Objects::nonNull).toList());
        }
        return cachedStructureResourceKeys;
    }

    public static void stopFlying(Player player) {
        // Stop creative style flight
        player.getAbilities().flying = false;

        // Stop elytra flight
        if (!Config.allowElytraFlight && player.isFallFlying()) {
            player.stopFallFlying();
        }

        // Stop jetpack style flight
        if (!Config.allowFlyingDevices && CompatHandler.isUsingFlyingDevice(player)) {
            CompatHandler.disableFlyingDevice(player);
        }

        long blockUnder = BlockPos.betweenClosedStream(player.blockPosition(), player.blockPosition().below(10)).filter(blockPos -> !player.level().getBlockState(blockPos).isAir()).count();
        if (blockUnder == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60));
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.sendStopFlightToPlayer(serverPlayer);
        }

        sendFlightNotice(player);
    }

    public static void sendFlightNotice(Player player) {
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("noflightzone.disallowed_flightzone"), false);
        }
    }

    public static void sendTeleportNotice(Player player) {
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("noflightzone.disallowed_teleport"), false);
        }
    }

    public static boolean isFlying(Player player) {
        return player.getAbilities().flying || (!Config.allowElytraFlight && player.isFallFlying()) || (!Config.allowFlyingDevices && CompatHandler.isUsingFlyingDevice(player));
    }
}
