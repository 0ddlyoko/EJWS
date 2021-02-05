package me.oddlyoko.ejws;

public final class EJWS {
    private static final EJWS ejws = new EJWS();

    private EJWS() {
    }

    public static EJWS get() {
        return ejws;
    }
}
