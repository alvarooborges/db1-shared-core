package net.hyze.core.shared.misc.mojang.exceptions;

import javax.xml.ws.http.HTTPException;

public class TooManyRequestsException extends HTTPException {

    public TooManyRequestsException(int statusCode) {
        super(statusCode);
    }

}
