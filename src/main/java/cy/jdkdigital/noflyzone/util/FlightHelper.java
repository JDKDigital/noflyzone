package cy.jdkdigital.noflyzone.util;

import cy.jdkdigital.noflyzone.Config;
import cy.jdkdigital.noflyzone.NoFlyZone;
import cy.jdkdigital.noflyzone.network.PacketHandler;
import cy.jdkdigital.noflyzone.util.compat.GameStagesCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.*;

public class FlightHelper
{
    static Map<UUID, Map<BlockPos, Boolean>> cache = new HashMap<>();
    public static boolean isAllowedToFly(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return true;
        }

        if (!cache.containsKey(player.getUUID()) || cache.get(player.getUUID()).size() > 10000) {
            cache.put(player.getUUID(), new HashMap<>());
        }

        // Return cached result when player hasn't moved to a new position
        if (cache.get(player.getUUID()).containsKey(player.blockPosition())) {
            return cache.get(player.getUUID()).get(player.blockPosition());
        }

        var isAllowed = isAllowedDimension(player) && isAllowedBiome(player) && isAllowedStructure(player);

        cache.get(player.getUUID()).put(player.blockPosition(), isAllowed);

        return isAllowed;
    }

    private static boolean isAllowedDimension(Player player) {
        if (!Config.dimensions.isEmpty() && Config.dimensions.contains(player.level().dimension())) {
            if (ModList.get().isLoaded("gamestages") && Config.dimensionUnlocks.containsKey(player.level().dimension())) {
                var stage = Config.dimensionUnlocks.get(player.level().dimension());
                if (GameStagesCompat.stageExists(stage)) {
                    return GameStagesCompat.hasUnlockedStage(player, stage);
                }
            }
            return false;
        }
        return true;
    }

    private static boolean isAllowedBiome(Player player) {
        if (Config.enableBiomeCheck) {
            var biome = player.level().getBiome(player.blockPosition());
            if (ModList.get().isLoaded("gamestages") && biome.unwrapKey().isPresent() && Config.biomeUnlocks.containsKey(biome.unwrapKey().get())) {
                var stage = Config.biomeUnlocks.get(biome.unwrapKey().get());
                if (GameStagesCompat.stageExists(stage)) {
                    return GameStagesCompat.hasUnlockedStage(player, stage);
                }
            }
            return !biome.is(NoFlyZone.BIOME_BLACKLIST);
        }
        return true;
    }

    private static boolean isAllowedStructure(Player player) {
        if (Config.enableStructureCheck && player.level() instanceof ServerLevel serverLevel) {
            for (ResourceKey<Structure> structureKey: getBlacklistedStructures(serverLevel)){
                if (ModList.get().isLoaded("gamestages") && Config.structureUnlocks.containsKey(structureKey)) {
                    var stage = Config.structureUnlocks.get(structureKey);
                    if (GameStagesCompat.stageExists(stage)) {
                        return GameStagesCompat.hasUnlockedStage(player, stage);
                    }
                }
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

    static Map<UUID, List<Integer>> repeatCheck = new HashMap<>();
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

        // slow fall if you're high up
        if (player instanceof ServerPlayer serverPlayer) {
            if (Config.enableSlowFall) {
                boolean isHighUp = BlockPos.betweenClosedStream(player.blockPosition(), player.blockPosition().below(10)).filter(blockPos -> !player.level().getBlockState(blockPos).isAir()).count() == 0;
                if (isHighUp) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60));
                }
            }

            PacketHandler.sendStopFlightToPlayer(serverPlayer);
        }

        sendFlightNotice(player);

        // Check if the player is trying to cheat
        if (Config.enablePunishOffenders) {
            if (repeatCheck.containsKey(player.getUUID())) {
                var entries = repeatCheck.get(player.getUUID());
                entries.add(player.tickCount);
                if (entries.size() > (Config.zapInterval / Config.checkInterval * 10)) {
                    // More than 30 tries per 1000 player ticks gets you zapped
                    if ((player.tickCount - entries.get(0)) < 1000) {
                        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(player.level());
                        if (lightningbolt != null) {
                            lightningbolt.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
                            lightningbolt.setVisualOnly(false);
                            player.level().addFreshEntity(lightningbolt);
                        }
                    }
                    // reset
                    repeatCheck.put(player.getUUID(), new ArrayList<>());
                }
            } else {
                repeatCheck.put(player.getUUID(), new ArrayList<>()
                {{
                    add(player.tickCount);
                }});
            }
        }
    }

    static Map<UUID, Integer> lastNotified = new HashMap<>();
    public static void sendFlightNotice(Player player) {
        if (!player.level().isClientSide) {
            if (!lastNotified.containsKey(player.getUUID()) || lastNotified.get(player.getUUID()) < player.tickCount - 60) {
                player.displayClientMessage(Component.translatable("noflightzone.disallowed_flightzone"), false);
            }
            lastNotified.put(player.getUUID(), player.tickCount);
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
