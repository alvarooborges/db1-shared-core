package net.hyze.core.shared.user.preferences;

public enum PreferenceStatus {

    ON, OFF, UNSET;

    public boolean is(PreferenceStatus status) {
        return this == status;
    }

    public PreferenceStatus opposite() {
        switch (this) {
            case ON:
                return OFF;
            case OFF:
                return ON;
            default:
                return UNSET;
        }
    }
}
