package icu.suc.mw78.identity.mythic.assassin.skill;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.task.DurationTask;
import icu.suc.megawalls78.util.Effect;
import icu.suc.megawalls78.util.EntityUtil;
import icu.suc.megawalls78.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

// 因为即将重构Passive的计时方式顺便加入Skill的监听实现，暂时推迟隐身时攻击的编写
public final class ShadowCloak extends DurationSkill {

    public static final String ID = "shadow_cloak";

    private static final long DURATION = 10000L; // 持续10秒
    private static final int TICK = (int) (DURATION / 50);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, TICK, 0); // 关于隐藏盔甲，将以后以Util形式提供发级别的隐藏
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, TICK, 0);
    private static final PotionEffect RESISTANCE = new PotionEffect(PotionEffectType.RESISTANCE, TICK, 0);
    private static final int REMAIN = 20; // 每剩余1秒隐身时间
    private static final int RETURN = 4; // 返还4点能量
    private static final double SCALE = 0.1D; //损失的生命值10%
    private static final double MIN = 1.0D; // 至少造成1点真实伤害

    private static final Effect<Player> EFFECT_START = Effect.create(player -> {
        ParticleUtil.spawnParticleRandomBody(player, Particle.SMOKE, 8, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, SoundCategory.PLAYERS, 1.0F, 2.0F);
    });
    private static final Effect<Player> EFFECT_END = Effect.create(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.0F));

    private Task task;

    public ShadowCloak() {
        super(ID, 100, 1000L, DURATION, Internal.class);
    }

    @Override
    protected boolean use0(Player player) {
        // 关于无线续杯问题，蜘蛛提供了一个很好的模板，请自行查阅
        boolean run = false;
        if (task == null || task.isCancelled()) {
            task = new Task(player);
            run = true;
        }

        EFFECT_START.play(player);
        player.addPotionEffect(INVISIBILITY); // force参数已弃用，默认覆盖原效果时间
        player.addPotionEffect(SPEED);
        player.addPotionEffect(RESISTANCE);
        summaryEffectSelf(player, INVISIBILITY, SPEED, RESISTANCE);
        task.resetTimer();

        if (run) {
            task.runTaskTimer(MegaWalls78.getInstance(), 0L, 1L);
        }

        //TODO:不是asn隐身的粒子效果是什么来着老子忘了草拟吗反正肯定不是dust但我也不知道是什么所以等会再补
        return true;
    }

    private final class Task extends DurationTask {

        private Task(Player player) {
            super(player, TICK);

            EntityUtil.setMetadata(player, ID, true);

            updateArmor();
        }

        @Override
        public void run() {
            if (shouldCancel()) {
                cancel();
                return;
            }

            super.run();

            if (!EntityUtil.getMetadata(player, ID)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                if (EntityUtil.hasPotionEffect(player, RESISTANCE)) {
                    player.removePotionEffect(PotionEffectType.RESISTANCE);
                }
                ShadowCloak.this.summaryRefund(player, (int) (((double) remain() / REMAIN) * RETURN));
                cancel();
            }
        }

        public void updateArmor() {
            if (player.isDead()) {
                return;
            }
            EntityEquipment equipment = player.getEquipment();
            ItemStack helmet = equipment.getHelmet();
            ItemStack chestplate = equipment.getChestplate();
            ItemStack leggings = equipment.getLeggings();
            ItemStack boots = equipment.getBoots();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                if (helmet != null) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HEAD, helmet);
                }
                if (chestplate != null) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.CHEST, chestplate);
                }
                if (leggings != null) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.LEGS, leggings);
                }
                if (boots != null) {
                    onlinePlayer.sendEquipmentChange(player, EquipmentSlot.FEET, boots);
                }
                onlinePlayer.sendEquipmentChange(player, EquipmentSlot.HAND, equipment.getItemInMainHand());
                onlinePlayer.sendEquipmentChange(player, EquipmentSlot.OFF_HAND, equipment.getItemInOffHand());
            }
            player.setArrowsInBody(0);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            EFFECT_END.play(player);
            EntityUtil.removeMetadata(player, ShadowCloak.this.getId());
            updateArmor();
            super.cancel();
            stop();
        }
    }

    public static final class Internal extends Passive {

        private final PacketAdapter adapter = new PacketAdapter(MegaWalls78.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                GamePlayer gamePlayer = PLAYER();
                if (gamePlayer == null) {
                    return;
                }
                if (event.getPlayer().getUniqueId().equals(gamePlayer.getUuid())) {
                    return;
                }
                Player player = gamePlayer.getBukkitPlayer();
                PacketContainer packet = event.getPacket();
                if (player.getEntityId() != packet.getIntegers().read(0)) {
                    return;
                }
                if (!EntityUtil.getMetadata(player, ID)) {
                    return;
                }
                StructureModifier<List<Pair<EnumWrappers.ItemSlot, ItemStack>>> modifier = packet.getSlotStackPairLists();
                for (int i = 0; i < modifier.getValues().size(); i++) {
                    List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = modifier.read(i);
                    for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : pairs) {
                        if (pair.getSecond().isEmpty()) {
                            continue;
                        }
                        pair.setSecond(ItemStack.empty());
                    }
                    modifier.write(i, pairs);
                }
            }
        };

        public Internal() {
            super("shadow_cloak");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player player && PASSIVE(player) && EntityUtil.getMetadata(player, ID) && condition(event)) {
                LivingEntity entity = (LivingEntity) event.getEntity();
                double damage = Math.max((entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - entity.getHealth()) * SCALE, MIN);
                event.setDamage(event.getDamage() + damage);
                EntityUtil.removeMetadata(player, ID);
            }
        }

        private static boolean condition(EntityDamageByEntityEvent event) {
            Entity entity = event.getEntity();
            return entity instanceof Player || entity instanceof Wither;
        }

        @Override
        public void register() {
            ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        }

        @Override
        public void unregister() {
            ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
        }
    }
}
