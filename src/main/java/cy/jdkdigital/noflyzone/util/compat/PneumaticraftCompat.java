package cy.jdkdigital.noflyzone.util.compat;

import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonUpgradeHandlers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PneumaticraftCompat
{
    public static boolean isUsingPneumaticBoots(Player player) {
        ItemStack jetBoots = player.getItemBySlot(EquipmentSlot.FEET);
        return CommonArmorHandler.getHandlerForPlayer(player).upgradeUsable(CommonUpgradeHandlers.jetBootsHandler, true) && !player.getCooldowns().isOnCooldown(jetBoots.getItem());
    }

    public static void disablePneumaticBoots(Player player) {
        ItemStack jetBoots = player.getItemBySlot(EquipmentSlot.FEET);
        player.getCooldowns().addCooldown(jetBoots.getItem(), 40);
    }
}
