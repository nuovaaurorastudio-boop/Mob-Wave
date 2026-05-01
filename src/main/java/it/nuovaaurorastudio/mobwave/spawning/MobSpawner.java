package it.nuovaaurorastudio.mobwave.spawning;

import io.lumine.mythic.bukkit.MythicBukkit;
import it.nuovaaurorastudio.mobwave.MobWave;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.concurrent.ThreadLocalRandom;

public class MobSpawner {

    private final MobWave plugin;

    public MobSpawner(MobWave plugin) {
        this.plugin = plugin;
    }

    public Entity spawnMob(Location baselocation, String mobType, int range) {
        Location spawnLoc = findSafeSpawnLocation(baselocation, range);
        if (spawnLoc == null) {
            return null;
        }

        if (plugin.isMythicMobsEnabled() && MythicBukkit.inst().getMobManager().getMythicMob(mobType).isPresent()) {
            try {
                return MythicBukkit.inst().getMobManager().spawnMob(mobType, spawnLoc).getEntity().getBukkitEntity();
            } catch (Exception e) {
                 return null;
            }
        } else {
            try {
                EntityType type = EntityType.valueOf(mobType.toUpperCase());
                return spawnLoc.getWorld().spawnEntity(spawnLoc, type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private Location findSafeSpawnLocation(Location center, int range) {
        World world = center.getWorld();
        if (world == null) return null;

        for (int i = 0; i < 15; i++) {
            int x = center.getBlockX() + ThreadLocalRandom.current().nextInt(-range, range + 1);
            int z = center.getBlockZ() + ThreadLocalRandom.current().nextInt(-range, range + 1);
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Block feet = loc.getBlock();
            Block head = loc.clone().add(0, 1, 0).getBlock();
            Block ground = loc.clone().subtract(0, 1, 0).getBlock();

            if (isPassable(feet) && isPassable(head) && ground.getType().isSolid()) {
                return loc;
            }
        }
        return null;
    }

    private boolean isPassable(Block block) {
        Material type = block.getType();
        return type == Material.AIR || type == Material.SHORT_GRASS || type == Material.TALL_GRASS || 
               type == Material.SNOW || type == Material.FERN || type == Material.LARGE_FERN;
    }
}
