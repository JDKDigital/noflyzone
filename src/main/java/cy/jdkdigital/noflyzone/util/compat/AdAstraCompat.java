package cy.jdkdigital.noflyzone.util.compat;

import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AdAstraCompat
{
    public static boolean isUsingJetsuit(Player player) {
        ItemStack jetpackSuit = player.getItemBySlot(EquipmentSlot.CHEST);
        return SpaceSuitItem.hasFullJetSuitSet(player) && !player.onGround() && !player.getCooldowns().isOnCooldown(jetpackSuit.getItem());
    }

    public static void disableJetsuit(Player player) {
        ItemStack jetpackSuit = player.getItemBySlot(EquipmentSlot.CHEST);
        player.getCooldowns().addCooldown(jetpackSuit.getItem(), 40);
    }
}
