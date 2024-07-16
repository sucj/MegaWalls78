package icu.suc.megawalls78.util;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Random;

public class RandomUtil {

    public static final Random RANDOM = new Random();

    public static Location getRandomSpawn(Location[][] regions) {
        Location[] region = regions[RANDOM.nextInt(regions.length)];
        Location locA = region[0];
        Location locB = region[1];
        double minX = Math.min(locA.getX(), locB.getX()) + 0.5D;
        double maxX = Math.max(locA.getX(), locB.getX()) - 0.5D;
        double minZ = Math.min(locA.getZ(), locB.getZ()) + 0.5D;
        double maxZ = Math.max(locA.getZ(), locB.getZ()) - 0.5D;
        double y = Math.min(locA.getY(), locB.getY());
        double randomX = minX + (maxX - minX) * RANDOM.nextDouble();
        double randomZ = minZ + (maxZ - minZ) * RANDOM.nextDouble();
        Location location = new Location(locA.getWorld(), randomX, y, randomZ, locA.getYaw(), locA.getPitch());
        while (!location.getBlock().getType().equals(Material.AIR)) {
            location.setY(++y);
        }
        return location;
    }

    public static <E> E getRandomEntry(Collection<E> collection) {
        int randomIndex = RANDOM.nextInt(collection.size());
        int currentIndex = 0;
        for (E e : collection) {
            if (currentIndex == randomIndex) {
                return e;
            }
            currentIndex++;
        }
        return null;
    }
}
