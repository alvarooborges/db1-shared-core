package net.hyze.core.spigot.misc.enchantments.triggers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public abstract class EnchantmentTrigger<T extends Event> {

    /**
     * Evento que chamou o gatilho.
     */
    private final T event;

    /**
     * Player que está usando o encantamento.
     */
    private final Player player;

    /**
     * Item que possui o encantamento.
     */
    private final ItemStack item;

    /**
     * Nível do encantamento.
     */
    private final int level;

    /**
     * Caso o trigger precise ser parada para um encantamento especifico.
     *
     * Caso esse valor seja true, o trigger não será mais disparado para os
     * itens com o mesmo encantamento.
     *
     * Isso não impede que os outros triggers de outros encantamentos sejam
     * disparados para qualquer outro item ou até mesmo para o item do trigger
     * atual.
     */
    @Setter
    private boolean stopPropagation = false;
}
