package cy.jdkdigital.noflyzone.util;

import cy.jdkdigital.noflyzone.NoFlyZone;
import cy.jdkdigital.noflyzone.util.compat.IronJetpackCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class CompatHandler
{
    public static boolean isUsingFlyingDevice(Player player) {
        boolean isUsingFlyingDevice = player.getUseItem().is(NoFlyZone.ITEM_BLACKLIST);
        if (!isUsingFlyingDevice && ModList.get().isLoaded("ironjetpacks")) {
            isUsingFlyingDevice = IronJetpackCompat.isUsingJetpack(player);
        }

        // TODO
        // ars nouveau flying ritual
        // ars nouveau elytra spell, copied to a pylon to make it infinite, and firework spell

        // TODO 1.19
        // https://www.curseforge.com/minecraft/mc-mods/projecte swiftwolf ring
        // Simply Jetpacks
        return isUsingFlyingDevice;
    }

    public static void disableFlyingDevice(Player player) {
        if (ModList.get().isLoaded("ironjetpacks") && IronJetpackCompat.isUsingJetpack(player)) {
            IronJetpackCompat.disableJetpack(player);
        }
        if (player.getUseItem().is(NoFlyZone.ITEM_BLACKLIST)) {
            player.releaseUsingItem();
        }
    }

    public static boolean isFlyingDevice(ItemStack itemStack) {
        return itemStack.is(NoFlyZone.ITEM_BLACKLIST);
    }
}
