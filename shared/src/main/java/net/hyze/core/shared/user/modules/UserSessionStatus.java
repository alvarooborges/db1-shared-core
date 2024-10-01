package net.hyze.core.shared.user.modules;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionStatus {

    @Setter
    private boolean logged;

    @Setter
    private Date loggedAt;
}
