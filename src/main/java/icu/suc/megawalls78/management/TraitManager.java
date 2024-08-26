package icu.suc.megawalls78.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.EnergyWay;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.annotation.*;
import icu.suc.megawalls78.identity.trait.passive.NullPassive;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.Trigger;
import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TraitManager {

    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, String> ID_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Component> NAME_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Component> DESCRIBE_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Float> COST_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Long> COOLDOWN_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Long> DURATION_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Integer> CHARGE_MAP = Maps.newHashMap();
    private static final Map<Class<? extends icu.suc.megawalls78.identity.trait.Trait>, Class<? extends Passive>> INTERNAL_MAP = Maps.newHashMap();
    private static final Map<Identity, Book> PAGES = Maps.newHashMap();

    private static final Component C_HOME = Component.translatable("mw78.brackets", NamedTextColor.GRAY, Component.translatable("mw78.gui.trait.home")).clickEvent(ClickEvent.changePage(1));
    private static final Component C_EG = Component.translatable("mw78.gui.trait.energy_generation");
    private static final Component C_S = Component.translatable("mw78.gui.trait.skill").decorate(TextDecoration.BOLD);
    private static final Component C_P = Component.translatable("mw78.gui.trait.passive").decorate(TextDecoration.BOLD);
    private static final Component C_G = Component.translatable("mw78.gui.trait.gathering").decorate(TextDecoration.BOLD);
    private static final Component C_ST = Component.translatable("mw78.gui.trait.skill_trigger");
    private static final Component C_SD = Component.translatable("mw78.gui.trait.skill_description");
    private static final Component C_PD = Component.translatable("mw78.gui.trait.passive_description");
    private static final Component C_GD = Component.translatable("mw78.gui.trait.gathering_description");

    public static <T extends icu.suc.megawalls78.identity.trait.Trait> T trait(Class<? extends T> clazz, GamePlayer player) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return trait(clazz, player, null);
    }

    public static <T extends icu.suc.megawalls78.identity.trait.Trait> T trait(Class<? extends T> clazz, GamePlayer player, String id) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Trait annotation = clazz.getAnnotation(Trait.class);

        boolean flag = id == null;

        id = flag ? annotation.value() : id;
        ID_MAP.put(clazz, id);
        NAME_MAP.put(clazz, Component.translatable("mw78.trait." + id));
        DESCRIBE_MAP.put(clazz, Component.translatable("mw78.trait." + id + ".description"));

        if (flag) {
            float cost = annotation.cost();
            if (cost != -1) {
                COST_MAP.put(clazz, cost);
            }

            long cooldown = annotation.cooldown();
            if (cooldown != -1) {
                COOLDOWN_MAP.put(clazz, cooldown);
            }

            long duration = annotation.duration();
            if (duration != -1) {
                DURATION_MAP.put(clazz, duration);
            }

            int charge = annotation.charge();
            if (charge != -1) {
                CHARGE_MAP.put(clazz, charge);
            }

            Class<? extends Passive> internal = annotation.internal();
            if (internal != NullPassive.class) {
                INTERNAL_MAP.put(clazz, internal);
            }
        }

        T trait = clazz.getConstructor().newInstance();
        trait.PLAYER(player);
        return trait;
    }

    public static String id(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return ID_MAP.get(clazz);
    }

    public static Component name(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return NAME_MAP.get(clazz);
    }

    public static Component description(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return DESCRIBE_MAP.get(clazz);
    }

    public static float cost(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return COST_MAP.get(clazz);
    }

    public static long cooldown(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return COOLDOWN_MAP.get(clazz);
    }

    public static long duration(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return DURATION_MAP.get(clazz);
    }

    public static int charge(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return CHARGE_MAP.get(clazz);
    }

    public static Class<? extends Passive> internal(Class<? extends icu.suc.megawalls78.identity.trait.Trait> clazz) {
        return INTERNAL_MAP.get(clazz);
    }

    public static Book book(Identity identity) {
        return PAGES.computeIfAbsent(identity, k -> {
            List<Component> pages = Lists.newArrayList();
            Component name = identity.getName();

            Component info = Component.empty().append(name.decorate(TextDecoration.BOLD))
                    .appendNewline()
                    .append(Component.translatable("mw78.gui.trait.energy", NamedTextColor.DARK_GRAY, Component.text(Formatters.NUMBER.format(identity.getEnergy()), NamedTextColor.DARK_BLUE)))
                    .appendNewline()
                    .appendNewline();
            Map<EnergyWay, Float> wayMap = identity.getEnergyWay();
            if (!wayMap.isEmpty()) {
                List<Map.Entry<EnergyWay, Component>> ways = Lists.newArrayList();
                for (Map.Entry<EnergyWay, Float> entry : wayMap.entrySet()) {
                    EnergyWay way = entry.getKey();
                    ways.add(new AbstractMap.SimpleEntry<>(way, Component.translatable("mw78.gui.trait.energy." + entry.getKey().getId(), NamedTextColor.GRAY, Component.text(Formatters.NUMBER.format(entry.getValue()), NamedTextColor.AQUA))));
                }
                ways.sort(Map.Entry.comparingByKey());
                List<Component> components = Lists.newArrayList();
                components.add(C_EG);
                for (Map.Entry<EnergyWay, Component> way : ways) {
                    components.add(way.getValue());
                }
                info = info.append(Component.translatable("mw78.brackets", C_EG.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(Component.join(JoinConfiguration.newlines(), components)))
                        .appendNewline()
                        .appendNewline();
            }

            int i = 1;

            info = info.append(C_S).appendNewline();
            Map<Trigger, Class<? extends icu.suc.megawalls78.identity.trait.skill.Skill>> skillClasses = identity.getSkillClasses();
            List<Component> skills = Lists.newArrayList();
            List<Component> sPages = Lists.newArrayList();
            for (Class<? extends icu.suc.megawalls78.identity.trait.skill.Skill> skill : Sets.newLinkedHashSet(skillClasses.values())) {
                Component skillName = name(skill);
                Component sPage = Component.empty().append(skillName.decorate(TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline();
                List<Component> components = Lists.newArrayList();
                components.add(C_ST);
                for (Trigger trigger : Trigger.values()) {
                    if (Objects.equals(skillClasses.get(trigger), skill)) {
                        components.add(Component.translatable("mw78.gui.trait.skill.trigger", NamedTextColor.GRAY, trigger.getName(), trigger.getAction().getName().color(NamedTextColor.AQUA)));
                    }
                }
                sPage = sPage.append(Component.translatable("mw78.brackets", C_ST.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(Component.join(JoinConfiguration.newlines(), components)))
                        .appendNewline()
                        .appendNewline();
                Component skillDescription = skillName.appendNewline().append(description(skill));
                sPage = sPage.append(Component.translatable("mw78.brackets", C_SD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(skillDescription))
                        .appendNewline()
                        .appendNewline();
                sPage = sPage.append(C_HOME);
                sPages.add(sPage);
                skills.add(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, skillName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(skillDescription)
                        .clickEvent(ClickEvent.changePage(++i)));
            }
            info = info.append(Component.join(JoinConfiguration.spaces(), skills))
                    .appendNewline();

            info = info.append(C_P).appendNewline();
            List<Class<? extends Passive>> passiveClasses = identity.getPassiveClasses();
            List<Component> passives = Lists.newArrayList();
            List<Component> pPages = Lists.newArrayList();
            for (Class<? extends Passive> passive : passiveClasses) {
                Component passiveName = name(passive);
                Component passiveDescription = passiveName.appendNewline().append(description(passive));
                pPages.add(Component.empty().append(passiveName.decorate(TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.translatable("mw78.brackets", C_PD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(passiveDescription))
                        .appendNewline()
                        .appendNewline()
                        .append(C_HOME));
                passives.add(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, passiveName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(passiveDescription)
                        .clickEvent(ClickEvent.changePage(++i)));
            }
            info = info.append(Component.join(JoinConfiguration.spaces(), passives))
                    .appendNewline();

            info = info.append(C_G).appendNewline();
            Class<? extends Gathering> gathering = identity.getGatheringClass();
            Component gatheringName = name(gathering);
            Component gatheringDescription = gatheringName.appendNewline().append(description(gathering));
            Component gPage = Component.empty().append(gatheringName.decorate(TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.translatable("mw78.brackets", C_GD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(gatheringDescription))
                    .appendNewline()
                    .appendNewline()
                    .append(C_HOME);
            info = info.append(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, gatheringName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(gatheringDescription).clickEvent(ClickEvent.changePage(++i)))
                    .appendNewline();

            pages.add(info);
            pages.addAll(sPages);
            pages.addAll(pPages);
            pages.add(gPage);

            return Book.book(name, name, pages);
        });
    }
}
