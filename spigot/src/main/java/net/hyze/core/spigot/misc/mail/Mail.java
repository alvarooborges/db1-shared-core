package net.hyze.core.spigot.misc.mail;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Mail {

    /**
     * Jogador que receberá os itens.
     */
    private final int receiverId;

    /**
     * Sender.
     */
    private final String sender;

    /**
     * Message.
     */
    private final String message;

    /**
     * Utilizado para categorizar e organizar os "mails".
     */
    private final String type;

    /**
     * Horário que o item foi enviado.
     */
    private final Long createdAt;

    /**
     * Itens que o jogador receberá.
     */
    private final ItemStack item;

    @Setter
    private int id;

    public Mail(int receiverId, String type, Long createdAt, ItemStack item, String sender, String message) {
        this.receiverId = receiverId;
        this.type = type;
        this.createdAt = createdAt;
        this.sender = sender;
        this.message = message;
        this.item = item;
    }


}
