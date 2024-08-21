package icu.suc.megawalls78.management;

import com.google.common.collect.Maps;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.trait.skill.Skill;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TriggerManager {

    private final Map<UUID, Map<Skill.Trigger, Boolean>> triggers;

    public TriggerManager() {
        this.triggers = Maps.newHashMap();
    }

    public boolean sneak(UUID uuid, Skill.Trigger trigger) {
        return triggers.computeIfAbsent(uuid, k -> Maps.newHashMap()).computeIfAbsent(trigger, k -> {
            try {
                return MegaWalls78.getInstance().getDatabaseManager().getTrigger(uuid, trigger).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void sneak(UUID uuid, Skill.Trigger trigger, boolean sneak) {
        triggers.computeIfAbsent(uuid, k -> Maps.newHashMap()).put(trigger, sneak);
        MegaWalls78.getInstance().getDatabaseManager().setTrigger(uuid, trigger, sneak);
    }

    public void toggle(UUID uuid, Skill.Trigger trigger) {
        sneak(uuid, trigger, !sneak(uuid, trigger));
    }
}
