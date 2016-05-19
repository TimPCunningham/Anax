package io.github.timpcunningham.anax.exceptions;

import io.github.timpcunningham.anax.utils.Lang;

public class LocalizedException extends Exception {
    private Lang reason;
    private Object[] args;

    public LocalizedException(Lang reason, Object... args) {
        this.reason = reason;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return reason.get("en_US", args);
    }

    public Lang getReason() {
        return reason;
    }

    public Object[] getArgs() {
        return args;
    }
}
