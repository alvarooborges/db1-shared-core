package net.hyze.core.shared.punishments.storage;

import com.google.common.collect.Sets;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.punishments.Punishment;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.punishments.PunishmentRevokeCategory;
import net.hyze.core.shared.punishments.PunishmentState;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.punishments.storage.specs.categories.SelectAllCategoriesSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.InsertPunishmentSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.SelectActivePunishmentByUserSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.SelectLatestPunishmentByUserSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.SelectPunishmentByIdSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.SelectPunishmentsByHardwareSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.SelectPunishmentsByUserAndCategorySpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.UpdatePunishmentHardwareIdSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.UpdatePunishmentRevokeSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.UpdatePunishmentStartedAtSpec;
import net.hyze.core.shared.punishments.storage.specs.punishments.UpdatePunishmentVisibilitySpec;
import net.hyze.core.shared.punishments.storage.specs.revoke_categories.SelectAllRevokeCategoriesSpec;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

public class PunishmentRepository extends MysqlRepository {

    public PunishmentRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    // Categories
    public Punishment fetchPunishment(int id) {
        return query(new SelectPunishmentByIdSpec(id)).stream().findFirst().orElse(null);
    }

    public Set<Punishment> fetchPunishments(User user) {
        return query(new SelectPunishmentsByUserAndCategorySpec(user, null));
    }

    public Set<Punishment> fetchPunishments(String ip) {
        return Sets.newHashSet(); // TODO
    }

    public Set<Punishment> fetchPunishments(User user, PunishmentCategory category) {
        return query(new SelectPunishmentsByUserAndCategorySpec(user, category));
    }

    public Punishment fetchActivePunishment(User user, PunishmentType type) {
        return query(new SelectActivePunishmentByUserSpec(user, type)).stream().findFirst().orElse(null);
    }

    public Punishment fetchActivePunishment(String hardwareId, PunishmentType type) {
        return query(new SelectPunishmentsByHardwareSpec(hardwareId, type)).stream().findFirst().orElse(null);
    }

    public Punishment fetchLastestPunishment(User user) {
        return query(new SelectLatestPunishmentByUserSpec(user)).stream().findFirst().orElse(null);
    }

    public Punishment insert(Punishment punishment) {
        return query(new InsertPunishmentSpec(punishment));
    }

    public void updateStartTime(Punishment punishment) {
        query(new UpdatePunishmentStartedAtSpec(punishment));
    }

    public void updateHardwareId(Punishment punishment) {
        query(new UpdatePunishmentHardwareIdSpec(punishment));
    }

    public void updateRevoke(Punishment punishment) {
        query(new UpdatePunishmentRevokeSpec(punishment));
    }

    public void updateVisibility(Punishment punishment) {
        query(new UpdatePunishmentVisibilitySpec(punishment));
    }

    public Punishment updatePunishments(User user, PunishmentType outputType) {

        AtomicReference<Punishment> output = new AtomicReference<>();
        Set<Punishment> punishments = this.fetchPunishments(user);

        if (!punishments.isEmpty()) {

            Set<PunishmentType> activePunishments = Sets.newHashSet();

            punishments.stream()
                    .filter(punishment -> !punishment.isRevoked() && !punishment.getState().equals(PunishmentState.ENDED) && !activePunishments.contains(punishment.getLevel().getType()))
                    .forEach(punishment -> {
                        activePunishments.add(punishment.getLevel().getType());

                        if (punishment.getState().equals(PunishmentState.PENDING)) {
                            punishment.setStartedAt(new Date());

                            String hardwareId = CoreProvider.Cache.Redis.USERS_STATUS.provide().getHardwareId(user.getNick());
                            punishment.setHardwareId(hardwareId);

                            this.updateHardwareId(punishment);
                            this.updateStartTime(punishment);

                            if (punishment.getLevel().getType().equals(outputType)) {
                                output.set(punishment);
                            }

                        }
                    });

        }

        return output.get();
    }

    // Categories
    public Set<PunishmentCategory> fetchCategories() {
        return query(new SelectAllCategoriesSpec());
    }

    // Revoke Categories
    public Set<PunishmentRevokeCategory> fetchRevokeCategories() {
        return query(new SelectAllRevokeCategoriesSpec());
    }

}
