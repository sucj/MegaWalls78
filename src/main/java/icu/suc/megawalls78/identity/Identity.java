package icu.suc.megawalls78.identity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.impl.cow.gathering.UltraPasteurized;
import icu.suc.megawalls78.identity.impl.cow.passive.BucketBarrier;
import icu.suc.megawalls78.identity.impl.cow.passive.RefreshingSip;
import icu.suc.megawalls78.identity.impl.cow.skill.SoothingMoo;
import icu.suc.megawalls78.identity.impl.enderman.gathering.Enderblocks;
import icu.suc.megawalls78.identity.impl.enderman.passive.EnderHeart;
import icu.suc.megawalls78.identity.impl.enderman.passive.SoulCharge;
import icu.suc.megawalls78.identity.impl.enderman.skill.Teleport;
import icu.suc.megawalls78.identity.impl.herebrine.gathering.TreasureHunter;
import icu.suc.megawalls78.identity.impl.herebrine.passive.Flurry;
import icu.suc.megawalls78.identity.impl.herebrine.passive.Power;
import icu.suc.megawalls78.identity.impl.herebrine.skill.Wrath;
import icu.suc.megawalls78.identity.impl.moleman.gathering.Stockpile;
import icu.suc.megawalls78.identity.impl.moleman.passive.JunkFood;
import icu.suc.megawalls78.identity.impl.moleman.passive.Shortcut;
import icu.suc.megawalls78.identity.impl.moleman.skill.Dig;
import icu.suc.megawalls78.identity.impl.spider.gathering.IronRush;
import icu.suc.megawalls78.identity.impl.spider.passive.Skitter;
import icu.suc.megawalls78.identity.impl.spider.passive.VenomStrike;
import icu.suc.megawalls78.identity.impl.spider.skill.Leap;
import icu.suc.megawalls78.identity.impl.zombie.gathering.WellTrained;
import icu.suc.megawalls78.identity.impl.zombie.passive.Berserk;
import icu.suc.megawalls78.identity.impl.zombie.passive.Toughness;
import icu.suc.megawalls78.identity.impl.zombie.skill.CircleOfHealing;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.identity.trait.Skill.Trigger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public enum Identity {
    COW("cow", NamedTextColor.LIGHT_PURPLE, Material.MILK_BUCKET, icu.suc.megawalls78.identity.impl.cow.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 25, EnergyWay.BOW_PER, 20), Map.of(Trigger.SWORD, SoothingMoo.class, Trigger.BOW, SoothingMoo.class), List.of(BucketBarrier.class, RefreshingSip.class), UltraPasteurized.class),
    HEROBRINE("herobrine", NamedTextColor.YELLOW, Material.DIAMOND_SWORD, icu.suc.megawalls78.identity.impl.herebrine.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 25, EnergyWay.BOW_PER, 25), Map.of(Trigger.SWORD, Wrath.class, Trigger.BOW, Wrath.class), List.of(Power.class, Flurry.class), TreasureHunter.class),
    ZOMBIE("zombie", NamedTextColor.DARK_GREEN, Material.ROTTEN_FLESH, icu.suc.megawalls78.identity.impl.zombie.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 12, EnergyWay.BOW_PER, 12, EnergyWay.MELEE_WHEN, 1, EnergyWay.BOW_WHEN, 2), Map.of(Trigger.SWORD, CircleOfHealing.class, Trigger.BOW, CircleOfHealing.class), List.of(Toughness.class, Berserk.class), WellTrained.class),
    ENDERMAN("enderman", NamedTextColor.DARK_PURPLE, Material.ENDER_PEARL, icu.suc.megawalls78.identity.impl.enderman.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 20, EnergyWay.BOW_PER, 20), Map.of(Trigger.SWORD, Teleport.class, Trigger.BOW, Teleport.class), List.of(EnderHeart.class, SoulCharge.class), Enderblocks.class),
    SPIDER("spider", NamedTextColor.DARK_PURPLE, Material.COBWEB, icu.suc.megawalls78.identity.impl.spider.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 8, EnergyWay.BOW_PER, 8, EnergyWay.GAME, 4, EnergyWay.DM, 4), Map.of(Trigger.SWORD, Leap.class, Trigger.BOW, Leap.class), List.of(VenomStrike.class, Skitter.class), IronRush.class),
    MOLEMAN("moleman", NamedTextColor.YELLOW, Material.GOLDEN_SHOVEL, icu.suc.megawalls78.identity.impl.moleman.Kit.class, 100, Map.of(EnergyWay.MELEE_PER, 10, EnergyWay.BOW_PER, 10, EnergyWay.PREPARATION, 5, EnergyWay.GAME, 3, EnergyWay.DM, 3), Map.of(Trigger.SWORD, Dig.class, Trigger.BOW, Dig.class, Trigger.SHOVEL, Dig.class), List.of(Shortcut.class, JunkFood.class), Stockpile.class);

    private final String id;
    private final TextColor color;
    private final Material material;
    private final Class<? extends Kit> kitClass;
    private final int energy;
    private final Map<EnergyWay, Integer> energyWay;
    private final Map<Skill.Trigger, Class<? extends Skill>> skillClasses;
    private final List<Class<? extends Passive>> passiveClasses;
    private final Class<? extends Gathering> gatheringClass;

    private Kit kit;
    private final Component name;
    private final Component abbr;
    private final Component icon;

    Identity(String id,
             TextColor color,
             Material material,
             Class<? extends Kit> kitClass,
             int energy,
             Map<EnergyWay, Integer> energyWay,
             Map<Skill.Trigger, Class<? extends Skill>> skillClasses,
             List<Class<? extends Passive>> passiveClasses,
             Class<? extends Gathering> gatheringClass) {
        this.id = id;
        this.color = color;
        this.material = material;
        this.kitClass = kitClass;
        this.energy = energy;
        this.energyWay = energyWay;
        this.skillClasses = skillClasses;
        this.passiveClasses = passiveClasses;
        this.gatheringClass = gatheringClass;

        this.name = Component.translatable("mw78.id." + id + ".name");
        this.abbr = Component.translatable("mw78.id." + id + ".abbr");
        this.icon = Component.translatable("mw78.id." + id + ".icon");
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }

    public TextColor getColor() {
        return color;
    }

    public Component getAbbr() {
        return abbr;
    }

    public Component getIcon() {
        return icon;
    }

    public Material getMaterial() {
        return material;
    }

    public Kit getKit() {
        if (kit == null) {
            try {
                kit = kitClass.getConstructor(getClass()).newInstance(this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return kit;
    }

    public int getEnergy() {
        return energy;
    }

    public int getEnergyByWay(EnergyWay way) {
        return this.energyWay.getOrDefault(way, 0);
    }

    public Map<Skill.Trigger, Skill> getSkills() {
        try {
            Map<Skill.Trigger, Skill> skills = Maps.newHashMap();
            for (Skill.Trigger trigger : skillClasses.keySet()) {
                add:
                {
                    Class<? extends Skill> skillClass = skillClasses.get(trigger);
                    for (Skill skill : skills.values()) {
                        if (skill.getClass().equals(skillClass)) {
                            skills.put(trigger, skill);
                            break add;
                        }
                    }
                    skills.put(trigger, skillClass.getConstructor().newInstance());
                }
            }
            return skills;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Passive> getPassives(GamePlayer player) {
        try {
            List<Passive> passives = Lists.newArrayList();
            for (Class<? extends Passive> passiveClass : passiveClasses) {
                Passive passive = passiveClass.getConstructor().newInstance();
                passive.setPlayer(player);
                passives.add(passive);
            }
            return passives;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Gathering getGathering(GamePlayer player, List<Passive> passives) {
        try {
            Gathering gathering = gatheringClass.getConstructor().newInstance();
            Class<? extends Passive> passiveClass = gathering.getInternal();
            if (passiveClass != null) {
                Passive passive = passiveClass.getConstructor().newInstance();
                passive.setPlayer(player);
                passives.add(passive);
            }
            return gathering;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Identity getIdentity(String id) {
        for (Identity identity : values()) {
            if (identity.getId().equals(id)) {
                return identity;
            }
        }
        return null;
    }
}
