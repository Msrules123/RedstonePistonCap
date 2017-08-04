package me.msrules123.redstonepistoncap.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import javax.annotation.Nonnull;

/**
 * Contains chunk coordinates while holding no references to Bukkit objects,
 * lowering the Object complexity and discarding unneeded values
 */
class LiteChunk {

    private final String world;
    private final int x;
    private final int z;

    LiteChunk(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    Chunk getBukkitChunk() {
        return Bukkit.getWorld(world).getChunkAt(x, z);
    }

    public String getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object other) {
        Preconditions.checkNotNull(other, "Value cannot be null!");
        if (!(other instanceof LiteChunk)) {
            return false;
        }
        LiteChunk otherChunk = (LiteChunk) other;
        return (otherChunk.world.equals(world) && otherChunk.x == this.x && otherChunk.z == this.z);
    }

    @Override
    public int hashCode() {
        int hashCode = 11;
        hashCode = 31 * hashCode + world.hashCode();
        hashCode = 31 * hashCode + x;
        hashCode = 31 * hashCode + z;
        return hashCode;
    }

    @Nonnull
    public static LiteChunk fromBukkitChunk(@Nonnull Chunk chunk) {
        return new LiteChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

}
