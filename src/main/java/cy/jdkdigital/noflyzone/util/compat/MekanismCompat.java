package cy.jdkdigital.noflyzone.util.compat;

import mekanism.common.item.gear.ItemJetpack;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registries.MekanismModules;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MekanismCompat
{
    public static boolean isUsingJetpack(Player player) {
        ItemStack jetpack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (jetpack.getItem() instanceof IJetpackItem jetpackItem) {
            return jetpackItem.canUseJetpack(jetpack) && jetpackItem.getJetpackMode(jetpack) != IJetpackItem.JetpackMode.DISABLED;
        }
        return false;
    }

    public static void disableFlight(Player player) {
        ItemStack jetpack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (jetpack.getItem() instanceof ItemJetpack jetpackItem) {
            jetpackItem.setMode(jetpack, IJetpackItem.JetpackMode.DISABLED);
        }
        if (jetpack.getItem() instanceof ItemMekaSuitArmor jetpackItem) {
            var module = jetpackItem.getModule(jetpack, MekanismModules.JETPACK_UNIT);
            if (module != null) {
                module.getCustomInstance().changeMode(module, player, jetpack, 1, false);
            }
        }
    }
}
