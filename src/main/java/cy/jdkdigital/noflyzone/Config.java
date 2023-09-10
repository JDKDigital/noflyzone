package cy.jdkdigital.noflyzone;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = NoFlyZone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue CHECK_INTERVAL = BUILDER
            .comment("No-fly zone checks happen every x player ticks, which can be taxing on the system when there's many players. Increase this number for better performance.")
            .defineInRange("checkInterval", 10, 1, Integer.MAX_VALUE);

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

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS = BUILDER
            .comment("A list of blacklisted dimensions.")
            .defineListAllowEmpty("dimensions", List.of(), (i) -> true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int checkInterval;
    public static boolean allowElytraFlight;
    public static boolean allowFlyingDevices;
    public static boolean allowTeleporting;
    public static boolean enableBiomeCheck;
    public static boolean enableStructureCheck;
    public static Set<ResourceKey<Level>> dimensions;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        checkInterval = CHECK_INTERVAL.get();
        allowElytraFlight = ALLOW_ELYTRA_FLIGHT.get();
        allowFlyingDevices = ALLOW_FLYING_DEVICES.get();
        allowTeleporting = ALLOW_TELEPORTING.get();
        enableBiomeCheck = ENABLE_BIOME_CHECK.get();
        enableStructureCheck = ENABLE_STRUCTURE_CHECK.get();

        // convert the list of strings into a set of items
        dimensions = DIMENSIONS.get().stream()
                .map(dimName -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimName)))
                .collect(Collectors.toSet());
    }
}
