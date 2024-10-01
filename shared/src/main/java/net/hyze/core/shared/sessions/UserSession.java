package net.hyze.core.shared.sessions;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserSession {

    @Setter
    private int id;
    
    private final String ip;
    private final int version;
    private final Date startedAt;
        
    public UserSession(String ip, int version, Date startedAt) {
        this.ip = ip;
        this.version = version;
        this.startedAt = startedAt;
    }
    
}
