package net.hyze.core.shared.user.storage;

import net.hyze.core.shared.misc.utils.BCrypt;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.storage.specs.*;
import net.hyze.core.shared.user.storage.specs.cash.DecrementCashByUserSpec;
import net.hyze.core.shared.user.storage.specs.cash.IncrementCashByUserSpec;
import net.hyze.core.shared.user.storage.specs.cash.SelectCashByUserSpec;
import net.hyze.core.shared.user.storage.specs.cash.UpdateCashByUserSpec;

import java.util.List;
import java.util.UUID;

public class UserRepository extends MysqlRepository {

    public UserRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public User create(String nick, UUID uuid, String plainPassword) {
        return query(new InsertOrUpdateUserSpec(nick, uuid, BCrypt.hashpw(plainPassword, BCrypt.gensalt())));
    }

    public User update(User user) {
        return query(new InsertOrUpdateUserSpec(user));
    }

    public void update_(User user) {
        query(new UpdateUserSpec(user));
    }

    public User fetchById(Integer id) {
        return query(new SelectUserByIdSpec(id));
    }

    public User fetchByNick(String nick) {
        return query(new SelectUserByNickSpec(nick));
    }

    public User fetchByEmail(String email) {
        return query(new SelectUserByEmailSpec(email));
    }

    public int fetchCash(User user) {
        return query(new SelectCashByUserSpec(user));
    }

    public List<User> fetchAssociateUsers(User user, int days) {
        return query(new SelectAssociateUsersByUserSpec(user, days));
    }

    public boolean decrementCash(User user, int amount) {
        if (amount <= 0) {
            return false;
        }

        return query(new DecrementCashByUserSpec(user, amount));
    }

    public boolean incrementCash(User user, int amount) {
        if (amount <= 0) {
            return false;
        }

        return query(new IncrementCashByUserSpec(user, amount));
    }

    public boolean defineCash(User user, int amount) {
        if (amount <= 0) {
            return false;
        }

        return query(new UpdateCashByUserSpec(user, amount));
    }
}
