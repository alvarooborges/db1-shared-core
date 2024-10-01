package net.hyze.core.shared.skins;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Skin {

    private final String value;
    private final String signature;

    public Skin(String value, String signature) {

        Preconditions.checkNotNull(value, "Value cannot be null.");
        Preconditions.checkNotNull(signature, "Signature cannot be null.");
        
        this.value = value;
        this.signature = signature;
        
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean hasSignature() {
        return signature != null;
    }

}
