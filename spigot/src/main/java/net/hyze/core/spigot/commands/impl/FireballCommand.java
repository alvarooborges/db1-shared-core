package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.commands.CustomCommand;
import net.minecraft.server.v1_8_R3.EntitySmallFireball;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class FireballCommand extends CustomCommand implements GroupCommandRestrictable {

    public FireballCommand() {
        super("fireball", CommandRestriction.IN_GAME, "fb");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        World world = player.getWorld();
        Location location = player.getLocation();
        Location eyeLocation = player.getEyeLocation();

        //SmallFireball fireball = player.launchProjectile(SmallFireball.class);
        //fireball.setIsIncendiary(false);
        //fireball.setYield(0F);

        int batchSize = 16;
        double angleDelta = (2 * Math.PI) / (batchSize);

        for(int i = 0; i < batchSize; i++) {
            double angle = angleDelta * i;

            Vector vector = new Vector(Math.cos(angle), 0, Math.sin(angle)).multiply(10);

            EntitySmallFireball fireball = new EntitySmallFireball(((CraftWorld) world).getHandle(), ((CraftPlayer) player).getHandle(), vector.getX(), vector.getY(), vector.getZ());
            fireball.projectileSource = ((CraftPlayer) player);
            fireball.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            fireball.world.addEntity(fireball);

            SmallFireball projectile = (SmallFireball) fireball.getBukkitEntity();
            projectile.setIsIncendiary(false);
            projectile.setYield(0F);
            projectile.setMetadata("damage", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), 5));
        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}

