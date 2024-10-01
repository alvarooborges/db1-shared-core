package net.hyze.core.spigot.commands.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MeltCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group;

    private final HashMap<MaterialData, Result> map = Maps.newHashMap();

    private final BiFunction<User, List<Result>, Boolean> preMelt;

    public MeltCommand(Group group, BiFunction<User, List<Result>, Boolean> preMelt) {
        super("derreter", CommandRestriction.IN_GAME);
        this.group = group;
        this.preMelt = preMelt;

        map.put(new MaterialData(Material.IRON_ORE, (byte) 0), new Result(new MaterialData(Material.IRON_INGOT, (byte) 0), 1));
        map.put(new MaterialData(Material.GOLD_ORE, (byte) 0), new Result(new MaterialData(Material.GOLD_INGOT, (byte) 0), 1));
        map.put(new MaterialData(Material.REDSTONE_ORE, (byte) 0), new Result(new MaterialData(Material.REDSTONE, (byte) 0), 1));
        map.put(new MaterialData(Material.COAL_ORE, (byte) 0), new Result(new MaterialData(Material.COAL, (byte) 0), 1));
        map.put(new MaterialData(Material.DIAMOND_ORE, (byte) 0), new Result(new MaterialData(Material.DIAMOND, (byte) 0), 1));
        map.put(new MaterialData(Material.EMERALD_ORE, (byte) 0), new Result(new MaterialData(Material.EMERALD, (byte) 0), 1));
        map.put(new MaterialData(Material.LAPIS_ORE, (byte) 0), new Result(new MaterialData(Material.INK_SACK, (byte) 4), 1));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        Inventory inventory = InventoryUtils.copy(player.getInventory());

        Set<MaterialData> checked = Sets.newHashSet();

        List<Result> results = Lists.newArrayList();

        for (ItemStack target : inventory.getContents()) {
            if (target == null || checked.contains(target.getData())) {
                continue;
            }

            checked.add(target.getData());

            Result targetResult = map.get(target.getData());
            if (targetResult == null) {
                continue;
            }

            int amountOfType = Arrays.stream(inventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> item.getData().equals(target.getData()))
                    .collect(Collectors.summingInt(item -> item.getAmount()));

            if (amountOfType < targetResult.getAmountNeeded()) {
                continue;
            }

            int leftOvers = Math.floorMod(amountOfType, targetResult.getAmountNeeded());
            int used = amountOfType - leftOvers;
            int resultProductAmount = used / targetResult.getAmountNeeded();

            int removed = 0;

            Inventory checkerInventory = InventoryUtils.copy(inventory);
            ItemStack[] checkerContents = checkerInventory.getContents();

            for (int i = 0; i < checkerContents.length; i++) {
                if (checkerContents[i] == null || !checkerContents[i].getData().equals(target.getData())) {
                    continue;
                }

                ItemStack content = checkerContents[i];

                if (content.getAmount() <= (used - removed)) {
                    checkerContents[i] = null;

                    removed += content.getAmount();

                    if (removed == used) {
                        break;
                    }

                    continue;
                }

                checkerContents[i].setAmount(content.getAmount() - (used - removed));
                break;
            }

            checkerInventory.setContents(checkerContents);

            ItemStack result = new ItemStack(
                    targetResult.getMaterialData().getItemType(),
                    resultProductAmount,
                    targetResult.getMaterialData().getData()
            );

            if (!InventoryUtils.fits(checkerInventory, result)) {
                continue;
            }

            checkerInventory.addItem(result);

            results.add(new Result(new MaterialData(result.getType(), (byte) result.getDurability()), result.getAmount()));

            inventory.setContents(checkerInventory.getContents());
        }

        if (results.isEmpty()) {
            Message.ERROR.send(sender, "Nenhum item para derreter.");
            return;
        }

        if (this.preMelt == null) {
            player.getInventory().setContents(inventory.getContents());
            return;
        }

        if (this.preMelt.apply(user, results)) {
            player.getInventory().setContents(inventory.getContents());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class Result {

        private final MaterialData materialData;
        private final int amountNeeded;
    }
}
