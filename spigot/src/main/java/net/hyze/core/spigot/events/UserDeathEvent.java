//package net.hyze.core.spigot.events;
//
//import net.hyze.core.shared.user.User;
//import java.util.List;
//import lombok.Getter;
//import lombok.Setter;
//import org.bukkit.entity.Player;
//import org.bukkit.event.HandlerList;
//import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.inventory.ItemStack;
//
//public class UserDeathEvent extends PlayerDeathEvent {
//
//    @Getter
//    private static final HandlerList handlerList = new HandlerList();
//
//    @Getter
//    private final User user;
//
//    @Setter
//    private boolean respawn = true;
//
//    public UserDeathEvent(User user, Player player, List<ItemStack> drops, int droppedExp) {
//        this(user, player, drops, droppedExp, 0);
//    }
//
//    public UserDeathEvent(User user, Player player, List<ItemStack> drops, int droppedExp, int newExp) {
//        this(user, player, drops, droppedExp, newExp, 0, 0);
//    }
//
//    public UserDeathEvent(User user, Player player, List<ItemStack> drops, int droppedExp, int newExp, int newTotalExp, int newLevel) {
//        super(player, drops, droppedExp, newExp, newTotalExp, newLevel, null);
//        this.user = user;
//    }
//
//    @Override
//    public HandlerList getHandlers() {
//        return handlerList;
//    }
//
//    public boolean canRespawn() {
//        return respawn;
//    }
//}
