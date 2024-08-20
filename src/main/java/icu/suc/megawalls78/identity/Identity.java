package icu.suc.megawalls78.identity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.mw78.identity.mythic.assassin.Assassin;
import icu.suc.mw78.identity.mythic.assassin.gathering.ArrowCatch;
import icu.suc.mw78.identity.mythic.assassin.passive.MasterAlchemist;
import icu.suc.mw78.identity.mythic.assassin.passive.ShadowStep;
import icu.suc.mw78.identity.mythic.assassin.skill.ShadowCloak;
import icu.suc.mw78.identity.mythic.moleman.Moleman;
import icu.suc.mw78.identity.mythic.moleman.gathering.Stockpile;
import icu.suc.mw78.identity.mythic.moleman.passive.JunkFood;
import icu.suc.mw78.identity.mythic.moleman.passive.Shortcut;
import icu.suc.mw78.identity.mythic.moleman.skill.Dig;
import icu.suc.mw78.identity.mythic.renegade.Renegade;
import icu.suc.mw78.identity.mythic.renegade.gathering.AmmoBin;
import icu.suc.mw78.identity.mythic.renegade.passive.GrapplingHook;
import icu.suc.mw78.identity.mythic.renegade.passive.Looting;
import icu.suc.mw78.identity.mythic.renegade.skill.Rend;
import icu.suc.mw78.identity.mythic.werewolf.Werewolf;
import icu.suc.mw78.identity.mythic.werewolf.gathering.Carnivore;
import icu.suc.mw78.identity.mythic.werewolf.passive.BloodLust;
import icu.suc.mw78.identity.mythic.werewolf.passive.Devour;
import icu.suc.mw78.identity.mythic.werewolf.skill.Lycanthropy;
import icu.suc.mw78.identity.next.vex.Vex;
import icu.suc.mw78.identity.next.vex.gathering.SoulBound;
import icu.suc.mw78.identity.next.vex.passive.Shriek;
import icu.suc.mw78.identity.next.vex.passive.Transience;
import icu.suc.mw78.identity.next.vex.skill.Flight;
import icu.suc.mw78.identity.next.vindicator.Vindicator;
import icu.suc.mw78.identity.next.vindicator.gathering.Lumberjack;
import icu.suc.mw78.identity.next.vindicator.passive.DoorBreaker;
import icu.suc.mw78.identity.next.vindicator.passive.Tetanus;
import icu.suc.mw78.identity.next.vindicator.skill.Johnny;
import icu.suc.mw78.identity.next.warden.Warden;
import icu.suc.mw78.identity.next.warden.gathering.Spreads;
import icu.suc.mw78.identity.next.warden.passive.Anger;
import icu.suc.mw78.identity.next.warden.passive.Sniffs;
import icu.suc.mw78.identity.next.warden.skill.SonicBoom;
import icu.suc.mw78.identity.regular.cow.Cow;
import icu.suc.mw78.identity.regular.cow.gathering.UltraPasteurized;
import icu.suc.mw78.identity.regular.cow.passive.BucketBarrier;
import icu.suc.mw78.identity.regular.cow.passive.RefreshingSip;
import icu.suc.mw78.identity.regular.cow.skill.SoothingMoo;
import icu.suc.mw78.identity.regular.dreadlord.Dreadlord;
import icu.suc.mw78.identity.regular.dreadlord.gathering.DarkMatter;
import icu.suc.mw78.identity.regular.dreadlord.passive.SoulEater;
import icu.suc.mw78.identity.regular.dreadlord.passive.SoulSiphon;
import icu.suc.mw78.identity.regular.dreadlord.skill.ShadowBurst;
import icu.suc.mw78.identity.regular.enderman.Enderman;
import icu.suc.mw78.identity.regular.enderman.gathering.Enderblocks;
import icu.suc.mw78.identity.regular.enderman.passive.EnderHeart;
import icu.suc.mw78.identity.regular.enderman.passive.SoulCharge;
import icu.suc.mw78.identity.regular.enderman.skill.Teleport;
import icu.suc.mw78.identity.regular.golem.Golem;
import icu.suc.mw78.identity.regular.golem.gathering.Momentum;
import icu.suc.mw78.identity.regular.golem.passive.IronConstitution;
import icu.suc.mw78.identity.regular.golem.passive.IronHeart;
import icu.suc.mw78.identity.regular.golem.skill.IronPunch;
import icu.suc.mw78.identity.regular.herobrine.Herobrine;
import icu.suc.mw78.identity.regular.herobrine.gathering.TreasureHunter;
import icu.suc.mw78.identity.regular.herobrine.passive.Flurry;
import icu.suc.mw78.identity.regular.herobrine.passive.Power;
import icu.suc.mw78.identity.regular.herobrine.skill.Wrath;
import icu.suc.mw78.identity.regular.hunter.Hunter;
import icu.suc.mw78.identity.regular.hunter.gathering.GoldenGlasses;
import icu.suc.mw78.identity.regular.hunter.passive.AnimalCompanion;
import icu.suc.mw78.identity.regular.hunter.passive.ForceOfNature;
import icu.suc.mw78.identity.regular.hunter.skill.EagleEye;
import icu.suc.mw78.identity.regular.hunter.skill.PigRider;
import icu.suc.mw78.identity.regular.shaman.Shaman;
import icu.suc.mw78.identity.regular.shaman.gathering.SpiritGathering;
import icu.suc.mw78.identity.regular.shaman.passive.Heroism;
import icu.suc.mw78.identity.regular.shaman.passive.WolfPack;
import icu.suc.mw78.identity.regular.shaman.skill.Tornado;
import icu.suc.mw78.identity.regular.skeleton.Skeleton;
import icu.suc.mw78.identity.regular.skeleton.gathering.Efficiency;
import icu.suc.mw78.identity.regular.skeleton.passive.Agile;
import icu.suc.mw78.identity.regular.skeleton.passive.Salvaging;
import icu.suc.mw78.identity.regular.skeleton.skill.ExplosiveArrow;
import icu.suc.mw78.identity.regular.spider.Spider;
import icu.suc.mw78.identity.regular.spider.gathering.IronRush;
import icu.suc.mw78.identity.regular.spider.passive.Skitter;
import icu.suc.mw78.identity.regular.spider.passive.VenomStrike;
import icu.suc.mw78.identity.regular.spider.skill.Leap;
import icu.suc.mw78.identity.regular.spider.skill.Locus;
import icu.suc.mw78.identity.regular.squid.Squid;
import icu.suc.mw78.identity.regular.squid.gathering.LuckOfTheSea;
import icu.suc.mw78.identity.regular.squid.passive.InnerInk;
import icu.suc.mw78.identity.regular.squid.passive.Rejuvenate;
import icu.suc.mw78.identity.regular.squid.skill.SquidSplash;
import icu.suc.mw78.identity.regular.zombie.Zombie;
import icu.suc.mw78.identity.regular.zombie.gathering.WellTrained;
import icu.suc.mw78.identity.regular.zombie.passive.Berserk;
import icu.suc.mw78.identity.regular.zombie.passive.Toughness;
import icu.suc.mw78.identity.regular.zombie.skill.CircleOfHealing;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.identity.trait.skill.Skill.Trigger;
import icu.suc.mw78.identity.regular.arcanist.Arcanist;
import icu.suc.mw78.identity.regular.arcanist.gathering.ArcaneMining;
import icu.suc.mw78.identity.regular.arcanist.passive.ArcaneExplosion;
import icu.suc.mw78.identity.regular.arcanist.passive.Tempest;
import icu.suc.mw78.identity.regular.arcanist.skill.ArcaneBeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public enum Identity {
    COW("cow", NamedTextColor.LIGHT_PURPLE, Material.MILK_BUCKET, Cow.class, 100, Map.of(EnergyWay.MELEE_PER, 25F, EnergyWay.BOW_PER, 20F), Map.of(Trigger.SWORD, SoothingMoo.class, Trigger.BOW, SoothingMoo.class), List.of(BucketBarrier.class, RefreshingSip.class), UltraPasteurized.class),
    HUNTER("hunter", NamedTextColor.GREEN, Material.BOW, Hunter.class, 100, Map.of(EnergyWay.MELEE_PER, 4F, EnergyWay.BOW_PER, 8F, EnergyWay.GAME, 0.75F, EnergyWay.DM, 0.75F), Map.of(Trigger.SWORD, EagleEye.class, Trigger.BOW, EagleEye.class, Trigger.CARROT_ON_A_STICK, PigRider.class), List.of(AnimalCompanion.class, ForceOfNature.class), GoldenGlasses.class),
    ARCANIST("arcanist", NamedTextColor.RED, Material.FIREWORK_ROCKET, Arcanist.class, 100, Map.of(EnergyWay.MELEE_PER, 34F, EnergyWay.BOW_PER, 34F), Map.of(Trigger.SWORD, ArcaneBeam.class, Trigger.BOW, ArcaneBeam.class), List.of(Tempest.class, ArcaneExplosion.class), ArcaneMining.class),
    DREADLORD("dreadlord", NamedTextColor.DARK_RED, Material.NETHER_BRICK, Dreadlord.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F), Map.of(Trigger.SWORD, ShadowBurst.class, Trigger.BOW, ShadowBurst.class), List.of(SoulEater.class, SoulSiphon.class), DarkMatter.class),
    GOLEM("golem", NamedTextColor.GRAY, Material.IRON_CHESTPLATE, Golem.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F), Map.of(Trigger.SWORD, IronPunch.class, Trigger.BOW, IronPunch.class), List.of(IronHeart.class, IronConstitution.class), Momentum.class),
    HEROBRINE("herobrine", NamedTextColor.YELLOW, Material.DIAMOND_SWORD, Herobrine.class, 100, Map.of(EnergyWay.MELEE_PER, 25F, EnergyWay.BOW_PER, 25F), Map.of(Trigger.SWORD, Wrath.class, Trigger.BOW, Wrath.class), List.of(Power.class, Flurry.class), TreasureHunter.class),
    ZOMBIE("zombie", NamedTextColor.DARK_GREEN, Material.ROTTEN_FLESH, Zombie.class, 100, Map.of(EnergyWay.MELEE_PER, 12F, EnergyWay.BOW_PER, 12F, EnergyWay.MELEE_WHEN, 1F, EnergyWay.BOW_WHEN, 2F), Map.of(Trigger.SWORD, CircleOfHealing.class, Trigger.BOW, CircleOfHealing.class), List.of(Toughness.class, Berserk.class), WellTrained.class),
    ENDERMAN("enderman", NamedTextColor.DARK_PURPLE, Material.ENDER_PEARL, Enderman.class, 100, Map.of(EnergyWay.MELEE_PER, 20F, EnergyWay.BOW_PER, 20F), Map.of(Trigger.SWORD, Teleport.class, Trigger.BOW, Teleport.class), List.of(EnderHeart.class, SoulCharge.class), Enderblocks.class),
    SHAMAN("shaman", NamedTextColor.GREEN, Material.ENCHANTING_TABLE, Shaman.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F), Map.of(Trigger.SWORD, Tornado.class, Trigger.BOW, Tornado.class), List.of(Heroism.class, WolfPack.class), SpiritGathering.class),
    SQUID("squid", NamedTextColor.BLUE, Material.INK_SAC, Squid.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F), Map.of(Trigger.SWORD, SquidSplash.class, Trigger.BOW, SquidSplash.class), List.of(InnerInk.class, Rejuvenate.class), LuckOfTheSea.class),
    SKELETON("skeleton", NamedTextColor.AQUA, Material.BONE, Skeleton.class, 100, Map.of(EnergyWay.BOW_PER, 20F, EnergyWay.DM, 1F), Map.of(Trigger.SWORD, ExplosiveArrow.class, Trigger.BOW, ExplosiveArrow.class), List.of(Salvaging.class, Agile.class), Efficiency.class),
    SPIDER("spider", NamedTextColor.DARK_PURPLE, Material.COBWEB, Spider.class, 100, Map.of(EnergyWay.MELEE_PER, 8F, EnergyWay.BOW_PER, 8F, EnergyWay.GAME, 4F, EnergyWay.DM, 4F), Map.of(Trigger.SWORD, Leap.class, Trigger.BOW, Leap.class, Trigger.SHOVEL, Locus.class), List.of(VenomStrike.class, Skitter.class), IronRush.class),
    WEREWOLF("werewolf", NamedTextColor.GREEN, Material.COOKED_BEEF, Werewolf.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F, EnergyWay.MELEE_WHEN, 2F, EnergyWay.BOW_WHEN, 2F), Map.of(Trigger.SWORD, Lycanthropy.class, Trigger.BOW, Lycanthropy.class), List.of(BloodLust.class, Devour.class), Carnivore.class),
    ASSASSIN("assassin", NamedTextColor.GRAY, Material.BLACK_STAINED_GLASS, Assassin.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F, EnergyWay.PREPARATION, 2F, EnergyWay.GAME, 2F, EnergyWay.DM, 2F), Map.of(Trigger.SWORD, ShadowCloak.class, Trigger.BOW, ShadowCloak.class), List.of(ShadowStep.class, MasterAlchemist.class), ArrowCatch.class),
    MOLEMAN("moleman", NamedTextColor.YELLOW, Material.GOLDEN_SHOVEL, Moleman.class, 100, Map.of(EnergyWay.MELEE_PER, 10F, EnergyWay.BOW_PER, 10F, EnergyWay.PREPARATION, 5F, EnergyWay.GAME, 3F, EnergyWay.DM, 3F), Map.of(Trigger.SWORD, Dig.class, Trigger.BOW, Dig.class, Trigger.SHOVEL, Dig.class), List.of(Shortcut.class, JunkFood.class), Stockpile.class),
    RENEGADE("renegade", NamedTextColor.YELLOW, Material.ARROW, Renegade.class, 100, Map.of(EnergyWay.MELEE_PER, 17F, EnergyWay.BOW_PER, 13F), Map.of(Trigger.SWORD, Rend.class, Trigger.BOW, Rend.class), List.of(GrapplingHook.class, Looting.class), AmmoBin.class),
    WARDEN("warden", NamedTextColor.DARK_AQUA, Material.ECHO_SHARD, Warden.class, 150, Map.of(EnergyWay.MELEE_WHEN, 10F, EnergyWay.BOW_WHEN, 5F), Map.of(Trigger.SWORD, SonicBoom.class, Trigger.BOW, SonicBoom.class, Trigger.AXE, SonicBoom.class), List.of(Sniffs.class, Anger.class), Spreads.class),
    VEX("vex", NamedTextColor.AQUA, Material.ELYTRA, Vex.class, 100, Map.of(EnergyWay.MELEE_PER, 20F), Map.of(Trigger.SWORD, Flight.class, Trigger.BOW, Flight.class), List.of(Shriek.class, Transience.class), SoulBound.class),
    VINDICATOR("vindicator", NamedTextColor.DARK_GREEN, Material.IRON_AXE, Vindicator.class, 100, Map.of(EnergyWay.MELEE_PER, 20F, EnergyWay.BOW_PER, 10F), Map.of(Trigger.SWORD, Johnny.class, Trigger.BOW, Johnny.class, Trigger.AXE, Johnny.class), List.of(DoorBreaker.class, Tetanus.class), Lumberjack.class);

    private final String id;
    private final TextColor color;
    private final Material material;
    private final Class<? extends Kit> kitClass;
    private final float energy;
    private final Map<EnergyWay, Float> energyWay;
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
             float energy,
             Map<EnergyWay, Float> energyWay,
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

    public float getEnergy() {
        return energy;
    }

    public float getEnergyByWay(EnergyWay way) {
        return this.energyWay.getOrDefault(way, 0.0F);
    }

    public Map<Skill.Trigger, Skill> getSkills(GamePlayer player, List<Passive> passives) {
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
                    Skill skill = skillClass.getConstructor().newInstance();
                    skill.PLAYER(player);
                    Class<? extends Passive> passiveClass = skill.getInternal();
                    if (passiveClass != null) {
                        Passive passive = passiveClass.getConstructor().newInstance();
                        passive.PLAYER(player);
                        passives.add(passive);
                        skill.setPassive(passive);
                    }
                    skills.put(trigger, skill);
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
                passive.PLAYER(player);
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
            gathering.PLAYER(player);
            Class<? extends Passive> passiveClass = gathering.getInternal();
            if (passiveClass != null) {
                Passive passive = passiveClass.getConstructor().newInstance();
                passive.PLAYER(player);
                passives.add(passive);
                gathering.setPassive(passive);
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
