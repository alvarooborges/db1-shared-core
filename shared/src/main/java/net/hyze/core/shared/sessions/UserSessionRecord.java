package net.hyze.core.shared.sessions;

import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSessionRecord {

    private final int userId;
    private final String ip;
    private final int version;

    private final Date startedAt;
    private final Date endedAt;

    private final boolean logged;
    private final UserSessionStatus sessionStatus;

}
