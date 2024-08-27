package icu.suc.mw78.identity.regular.squid.gathering;

import icu.suc.megawalls78.event.ChestRollEvent;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.Trait;
import icu.suc.megawalls78.identity.trait.passive.ChargePassive;
import icu.suc.megawalls78.util.InventoryUtil;
import icu.suc.megawalls78.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Trait(value = "luck_of_the_sea", internal = LuckOfTheSea.Internal.class)
public final class LuckOfTheSea extends Gathering {

    private static final ItemBuilder POTION = ItemBuilder.of(Material.POTION)
            .setDisplayName(Component.translatable("item.minecraft.potion"))
            .setAmount(3)
            .addPrefix(Identity.SQUID.getName().appendSpace())
            .addDecoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .setMaxStackSize(64)
            .addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1), false);

    @Trait(charge = 5)
    public static final class Internal extends ChargePassive {

        @EventHandler
        public void onChestRoll(ChestRollEvent.Post event) {
//            if (PASSIVE(event.getPlayer()) && condition() && CHARGE()) {
            if (PASSIVE(event.getPlayer()) && CHARGE()) {
                handle(event);
                CHARGE_RESET();
            }
        }

        private static void handle(ChestRollEvent.Post event) {
            InventoryUtil.addItemRandomSlot(event.getInventory(), POTION.build());
        }

//        private static boolean condition() {
//            return !MegaWalls78.getInstance().getGameManager().getRunner().isDm();
//        }
//
//        @Override
//        public Component acb() {
//            return Type.CHARGE_STATE.accept(CHARGE_COUNT(), CHARGE, condition());
//        }
    }
}
