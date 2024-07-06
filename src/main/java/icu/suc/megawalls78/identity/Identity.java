package icu.suc.megawalls78.identity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.energy.EnergyHit;
import icu.suc.megawalls78.identity.impl.herebrine.passive.Flurry;
import icu.suc.megawalls78.identity.impl.herebrine.passive.Power;
import icu.suc.megawalls78.identity.impl.herebrine.skill.Wrath;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.identity.trait.Trigger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public enum Identity {
    COW("cow",
            Material.MILK_BUCKET,
            icu.suc.megawalls78.identity.impl.cow.Kit.class,
            100,
            Map.of(EnergyHit.MELEE, 25, EnergyHit.BOW, 20),
            Map.of(Trigger.SWORD, Wrath.class,
                    Trigger.BOW, Wrath.class),
            List.of()),
    HEROBRINE("herobrine",
            Material.DIAMOND_SWORD,
            icu.suc.megawalls78.identity.impl.herebrine.Kit.class,
            100,
            Map.of(EnergyHit.MELEE, 25, EnergyHit.BOW, 25),
            Map.of(Trigger.SWORD, Wrath.class,
                    Trigger.BOW, Wrath.class),
            List.of(Power.class, Flurry.class));

    private final String id;
    private final Material material;
    private final Class<? extends Kit> kitClass;
    private final int energy;
    private final Map<EnergyHit, Integer> energyHit;
    private final Map<Trigger, Class<? extends Skill>> skillClasses;
    private final List<Class<? extends Passive>> passiveClasses;

    private Kit kit;
    private Component name;
    private Component abbr;
    private Component icon;

    Identity(String id,
             Material material,
             Class<? extends Kit> kitClass,
             int energy,
             Map<EnergyHit, Integer> energyHit,
             Map<Trigger, Class<? extends Skill>> skillClasses,
             List<Class<? extends Passive>> passiveClasses) {
        this.id = id;
        this.material = material;
        this.kitClass = kitClass;
        this.energy = energy;
        this.energyHit = energyHit == null ? Maps.newIdentityHashMap() : energyHit;
        this.skillClasses = skillClasses;
        this.passiveClasses = passiveClasses;

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

    public int getEnergyHit(EnergyHit energyHit) {
        return this.energyHit.getOrDefault(energyHit, 0);
    }

    public Map<Trigger, Skill> getSkills() {
        try {
            Map<Trigger, Skill> skills = Maps.newHashMap();
            for (Trigger trigger : skillClasses.keySet()) {
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

    public static Identity getIdentity(String id) {
        for (Identity identity : values()) {
            if (identity.getId().equals(id)) {
                return identity;
            }
        }
        return null;
    }
}
