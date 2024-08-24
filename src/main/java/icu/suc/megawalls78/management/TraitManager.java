package icu.suc.megawalls78.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.EnergyWay;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.Trait;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.Formatters;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TraitManager {

    private static final Map<Class<? extends Trait>, String> ID_MAP = Maps.newHashMap();
    private static final Map<Class<? extends Trait>, Component> NAME_MAP = Maps.newHashMap();
    private static final Map<Class<? extends Trait>, Component> DESCRIBE_MAP = Maps.newHashMap();
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

    public static <T extends Trait> T trait(Class<? extends T> clazz, GamePlayer player) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return trait(clazz, player, null);
    }

    public static <T extends Trait> T trait(Class<? extends T> clazz, GamePlayer player, String id) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        id = id == null ? clazz.getAnnotation(icu.suc.megawalls78.identity.trait.annotation.Trait.class).value() : id;
        ID_MAP.put(clazz, id);
        NAME_MAP.put(clazz, Component.translatable("mw78.trait." + id));
        DESCRIBE_MAP.put(clazz, Component.translatable("mw78.trait." + id + ".description"));
        T trait = clazz.getConstructor().newInstance();
        trait.PLAYER(player);
        return trait;
    }

    public static String id(Class<? extends Trait> clazz) {
        return ID_MAP.get(clazz);
    }

    public static Component name(Class<? extends Trait> clazz) {
        return NAME_MAP.get(clazz);
    }

    public static Component describe(Class<? extends Trait> clazz) {
        return DESCRIBE_MAP.get(clazz);
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
            Map<Skill.Trigger, Class<? extends Skill>> skillClasses = identity.getSkillClasses();
            List<Component> skills = Lists.newArrayList();
            List<Component> sPages = Lists.newArrayList();
            for (Class<? extends Skill> skill : Sets.newLinkedHashSet(skillClasses.values())) {
                Component skillName = name(skill);
                skills.add(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, skillName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.changePage(++i)));
                Component sPage = Component.empty().append(skillName.decorate(TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline();
                List<Component> components = Lists.newArrayList();
                components.add(C_ST);
                for (Skill.Trigger trigger : Skill.Trigger.values()) {
                    if (Objects.equals(skillClasses.get(trigger), skill)) {
                        components.add(Component.translatable("mw78.gui.trait.skill.trigger", NamedTextColor.GRAY, trigger.getName(), trigger.getAction().getName().color(NamedTextColor.AQUA)));
                    }
                }
                sPage = sPage.append(Component.translatable("mw78.brackets", C_ST.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(Component.join(JoinConfiguration.newlines(), components)))
                        .appendNewline()
                        .appendNewline();
                sPage = sPage.append(Component.translatable("mw78.brackets", C_SD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(skillName.appendNewline().append(describe(skill))))
                        .appendNewline()
                        .appendNewline();
                sPage = sPage.append(C_HOME);
                sPages.add(sPage);
            }
            info = info.append(Component.join(JoinConfiguration.spaces(), skills))
                    .appendNewline();

            info = info.append(C_P).appendNewline();
            List<Class<? extends Passive>> passiveClasses = identity.getPassiveClasses();
            List<Component> passives = Lists.newArrayList();
            List<Component> pPages = Lists.newArrayList();
            for (Class<? extends Passive> passive : passiveClasses) {
                Component passiveName = name(passive);
                passives.add(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, passiveName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.changePage(++i)));
                pPages.add(Component.empty().append(passiveName.decorate(TextDecoration.BOLD))
                        .appendNewline()
                        .appendNewline()
                        .append(Component.translatable("mw78.brackets", C_PD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(passiveName.appendNewline().append(describe(passive))))
                        .appendNewline()
                        .appendNewline()
                        .append(C_HOME));
            }
            info = info.append(Component.join(JoinConfiguration.spaces(), passives))
                    .appendNewline();

            info = info.append(C_G).appendNewline();
            Class<? extends Gathering> gathering = identity.getGatheringClass();
            Component gatheringName = name(gathering);
            Component gPage = Component.empty().append(gatheringName.decorate(TextDecoration.BOLD))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.translatable("mw78.brackets", C_GD.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).hoverEvent(gatheringName.appendNewline().append(describe(gathering))))
                    .appendNewline()
                    .appendNewline()
                    .append(C_HOME);
            info = info.append(Component.translatable("mw78.brackets", NamedTextColor.DARK_GRAY, gatheringName.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)).decorate(TextDecoration.BOLD).clickEvent(ClickEvent.changePage(++i)))
                    .appendNewline();

            pages.add(info);
            pages.addAll(sPages);
            pages.addAll(pPages);
            pages.add(gPage);

            return Book.book(name, name, pages);
        });
    }
}
