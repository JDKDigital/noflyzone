package cy.jdkdigital.noflyzone;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = NoFlyZone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue CHECK_INTERVAL = BUILDER
            .comment("No-fly zone checks happen every x player ticks, which can be taxing on the system when there's many players. Increase this number for better performance.")
            .defineInRange("checkInterval", 5, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ALLOW_ELYTRA_FLIGHT = BUILDER
            .comment("Whether to allow flight using an elytra in a no flight zone")
            .define("allowElytraFlight", false);

    private static final ForgeConfigSpec.BooleanValue ALLOW_FLYING_DEVICES = BUILDER
            .comment("Whether to allow flight using a jetpack device in a no flight zone")
            .define("allowFlyingDevices", false);

    private static final ForgeConfigSpec.BooleanValue ALLOW_TELEPORTING = BUILDER
            .comment("Allow player teleportation in a no flight zone")
            .define("allowTeleporting", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_BIOME_CHECK = BUILDER
            .comment("For performance reasons biome checks are off by default. Set it to true to disallow biomes listed in the noflyzone:worldgen/biome/blacklist tag.")
            .define("enableBiomeCheck", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_STRUCTURE_CHECK = BUILDER
            .comment("For performance reasons structure checks are off by default. Set it to true to disallow structures listed in the noflyzone:worldgen/structure/blacklist tag.")
            .define("enableStructureCheck", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_SLOW_FALL = BUILDER
            .comment("Give the player slow fall when stopping flight.")
            .define("enableSlowFall", true);

    private static final ForgeConfigSpec.BooleanValue PUNISH_OFFENDER = BUILDER
            .comment("Punish repeat offenders with lightning.")
            .define("enablePunishOffenders", false);

    private static final ForgeConfigSpec.IntValue ZAP_INTERVAL = BUILDER
            .comment("This number represents how many failed flight attempts a player can make within 1000 ticks (50 seconds) before getting zapped.")
            .defineInRange("zapInterval", 30, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS = BUILDER
            .comment("A list of blacklisted dimensions.")
            .defineListAllowEmpty("dimensions", List.of(), (i) -> true);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_UNLOCKS = BUILDER
            .comment("A list of game stage configurations unlocking flight in a dimension. The format is \"checkType,check,game_stage\" ex: \"dimension,minecraft:the_nether,nether_unlocked\" or \"biome,minecraft:plains,stage_name\"")
            .defineListAllowEmpty("dimension_unlocks", List.of(), (i) -> true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int checkInterval;
    public static boolean allowElytraFlight;
    public static boolean allowFlyingDevices;
    public static boolean allowTeleporting;
    public static boolean enableBiomeCheck;
    public static boolean enableStructureCheck;
    public static boolean enableSlowFall;
    public static boolean enablePunishOffenders;
    public static int zapInterval;
    public static Set<ResourceKey<Level>> dimensions;
    public static Map<ResourceKey<Level>, String> dimensionUnlocks;
    public static Map<ResourceKey<Biome>, String> biomeUnlocks;
    public static Map<ResourceKey<Structure>, String> structureUnlocks;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        checkInterval = CHECK_INTERVAL.get();
        allowElytraFlight = ALLOW_ELYTRA_FLIGHT.get();
        allowFlyingDevices = ALLOW_FLYING_DEVICES.get();
        allowTeleporting = ALLOW_TELEPORTING.get();
        enableBiomeCheck = ENABLE_BIOME_CHECK.get();
        enableStructureCheck = ENABLE_STRUCTURE_CHECK.get();
        enableSlowFall = ENABLE_SLOW_FALL.get();
        enablePunishOffenders = PUNISH_OFFENDER.get();
        zapInterval = ZAP_INTERVAL.get();

        dimensions = DIMENSIONS.get().stream()
                .map(dimName -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimName)))
                .collect(Collectors.toSet());

        dimensionUnlocks = DIMENSION_UNLOCKS.get().stream().filter(s -> s.startsWith("dimension,"))
                .collect(Collectors.toMap(inString -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(inString.split(",")[1])), inString -> inString.split(",")[2]));
        biomeUnlocks = DIMENSION_UNLOCKS.get().stream().filter(s -> s.startsWith("biome,"))
                .collect(Collectors.toMap(inString -> ResourceKey.create(Registries.BIOME, new ResourceLocation(inString.split(",")[1])), inString -> inString.split(",")[2]));
        structureUnlocks = DIMENSION_UNLOCKS.get().stream().filter(s -> s.startsWith("structure,"))
                .collect(Collectors.toMap(inString -> ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(inString.split(",")[1])), inString -> inString.split(",")[2]));
    }
}
