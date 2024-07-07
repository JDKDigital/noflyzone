package cy.jdkdigital.noflyzone.util.compat;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLeap;
import net.minecraft.world.item.ItemStack;

public class ArsCompat
{
    public static boolean isFlyingSpell(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ICasterTool casterTool) {
            ISpellCaster caster = casterTool.getSpellCaster(itemStack);
            Spell spell = caster.getSpell();
            for (AbstractSpellPart abstractSpellPart : spell.recipe) {
                if (abstractSpellPart instanceof EffectLeap) {
                    return true;
                }
            }
        }
        return false;
    }
}
