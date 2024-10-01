package net.hyze.core.shared.group.due;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.servers.Server;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupDue {

    private final Integer userId;
    private final Group group;
    private final Date dueAt;
}
