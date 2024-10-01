package net.hyze.core.spigot.misc.alchemy.runnables;

import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.alchemy.AlchemyManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AlchemyBrewTask extends BukkitRunnable {

    private static double DEFAULT_BREW_SPEED = 1.0;
    private static int DEFAULT_BREW_TICKS = 400;

    private BlockState brewingStand;
    private Location location;
    private double brewSpeed;
    private double brewTimer;
    private Player player;

    public AlchemyBrewTask(BlockState brewingStand, Player player) {
        this.brewingStand = brewingStand;
        this.location = brewingStand.getLocation();
        this.player = player;

        brewSpeed = DEFAULT_BREW_SPEED;
        brewTimer = DEFAULT_BREW_TICKS;

//        if (player != null && !Misc.isNPCEntity(player) && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.CATALYSIS)) {
//            double catalysis = UserManager.getPlayer(player).getAlchemyManager().calculateBrewSpeed(Permissions.lucky(player, SkillType.ALCHEMY));

//            McMMOPlayerCatalysisEvent event = new McMMOPlayerCatalysisEvent(player, catalysis);
//            HyzeSkillsPlugin.p.getServer().getPluginManager().callEvent(event);

//            if (!event.isCancelled()) {
//                brewSpeed = catalysis;
//            }
//        }

        if (AlchemyManager.brewingStandMap.containsKey(location)) {
            AlchemyManager.brewingStandMap.get(location).cancel();
        }

        AlchemyManager.brewingStandMap.put(location, this);
        this.runTaskTimer(CoreSpigotPlugin.getInstance(), 1, 1);
    }

    @Override
    public void run() {
        if (player == null || !player.isValid() || brewingStand == null || brewingStand.getType() != Material.BREWING_STAND) {
            if (AlchemyManager.brewingStandMap.containsKey(location)) {
                AlchemyManager.brewingStandMap.remove(location);
            }

            this.cancel();

            return;
        }

        brewTimer -= brewSpeed;

        // Vanilla potion brewing completes when BrewingTime == 1
        if (brewTimer < Math.max(brewSpeed, 2)) {
            this.cancel();
            finish();
        } else {
            ((BrewingStand) brewingStand).setBrewingTime((int) brewTimer);
        }
    }

    private void finish() {
//        McMMOPlayerBrewEvent event = new McMMOPlayerBrewEvent(player, brewingStand);
//        HyzeSkillsPlugin.p.getServer().getPluginManager().callEvent(event);

//        if (!event.isCancelled()) {
//            AlchemyPotionBrewer.finishBrewing(brewingStand, player, false);
//        }

        AlchemyManager.brewingStandMap.remove(location);
    }

    public void finishImmediately() {
        this.cancel();

        AlchemyManager.finishBrewing(brewingStand, player, true);
        AlchemyManager.brewingStandMap.remove(location);
    }

    public void cancelBrew() {
        this.cancel();

        ((BrewingStand) brewingStand).setBrewingTime(-1);
        AlchemyManager.brewingStandMap.remove(location);
    }

    @Override
    public void cancel() {
        super.cancel();
    }
}
