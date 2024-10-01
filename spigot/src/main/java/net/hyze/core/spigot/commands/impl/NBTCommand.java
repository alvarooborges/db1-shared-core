package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.hyze.core.shared.commands.GroupCommandRestrictable;

public class NBTCommand extends CustomCommand implements GroupCommandRestrictable {

    public NBTCommand() {
        super("nbt", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        ItemStack stack = player.getItemInHand();

        if (stack == null || stack.getType() == Material.AIR) {
            Message.ERROR.send(sender, "Segure um item na sua mão.");
            return;
        }

        ItemBuilder builder = ItemBuilder.of(stack);

        NBTTagCompound compound = builder.nbt();

        if (compound == null) {
            Message.ERROR.send(sender, "Este item não possui nenhum NBT.");
            return;
        }

        System.out.println(compound);
        
//        compound.getKeys().forEach(key -> {
//            Message.INFO.send(sender, "&lkey: &e" + key);           
//            Message.INFO.send(sender, "&lvalue: &e" + builder.nbt().getValue(key));
//            Message.INFO.send(sender, "");
//        });
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
