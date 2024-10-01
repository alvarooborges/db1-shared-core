package net.hyze.core.shared.misc.purchases;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.servers.Server;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class Purchase {

    private final Integer id;
    private final Integer userId;
    private final Server server;
    private final PurchaseType type;
    private final String value;
    private final int cycle;
    private final String transaction;
    private final int quantity;
    private final String currency;
    private final double paidPrice;
    private final double originalPrice;
    private final String ip;
    private final String email;
    private final PurchaseState activationState;
    private final PurchaseState announcementState;
    private final Date createdAt;
}
