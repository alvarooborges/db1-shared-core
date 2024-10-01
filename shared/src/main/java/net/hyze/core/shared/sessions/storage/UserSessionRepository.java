package net.hyze.core.shared.sessions.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.sessions.UserSession;
import net.hyze.core.shared.sessions.UserSessionStatus;
import net.hyze.core.shared.sessions.storage.specs.EndActiveSessionsSpec;
import net.hyze.core.shared.sessions.storage.specs.InsertSessionSpec;
import net.hyze.core.shared.sessions.storage.specs.UpdateSessionLoggedSpec;
import net.hyze.core.shared.sessions.storage.specs.EndSessionSpec;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;

public class UserSessionRepository extends MysqlRepository {

    public UserSessionRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void startSession(User user, UserSession session, boolean logged) {
        query(new EndActiveSessionsSpec(user));
        query(new InsertSessionSpec(user, session, logged));
    }

    public void endSession(int sessionId) {
        query(new EndSessionSpec(sessionId, UserSessionStatus.FINISHED));
    }

    public void authenticateSession(int sessionId) {
        query(new UpdateSessionLoggedSpec(sessionId, true));
    }

}
