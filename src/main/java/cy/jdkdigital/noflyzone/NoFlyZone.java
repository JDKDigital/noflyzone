package cy.jdkdigital.noflyzone;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.noflyzone.network.PacketHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoFlyZone.MODID)
public class NoFlyZone
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "noflyzone";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static TagKey<Biome> BIOME_BLACKLIST = TagKey.create(Registries.BIOME, new ResourceLocation(MODID, "blacklist"));
    public static TagKey<Structure> STRUCTURE_BLACKLIST = TagKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, "blacklist"));
    public static TagKey<Item> ITEM_BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "disallowed_flying_devices"));
    public static TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, "disallowed_flying_entities"));

    public NoFlyZone() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
}
