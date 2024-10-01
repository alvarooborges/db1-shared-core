package net.hyze.core.shared.skins;

import com.google.common.base.Preconditions;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SkinRecord {

    @Setter
    private int id;
    private final String nick;
    private final Skin skin;
    private final SkinRecordType type;
    private final Date updatedAt;
    
    public SkinRecord(int id, String nick, Skin skin, SkinRecordType type, Date updatedAt) {
        
        Preconditions.checkNotNull(nick, "Nick cannot be null.");
        Preconditions.checkNotNull(skin, "Skin cannot be null.");
        Preconditions.checkNotNull(type, "Type cannot be null.");
        Preconditions.checkNotNull(updatedAt, "UpdatedAt cannot be null.");
        
        this.id = id;
        this.nick = nick;
        this.skin = skin;
        this.type = type;
        this.updatedAt = updatedAt;
        
    }
    
    public SkinRecord(String nick, Skin skin, SkinRecordType type) {
        this(0, nick, skin, type, new Date());
    }

}
