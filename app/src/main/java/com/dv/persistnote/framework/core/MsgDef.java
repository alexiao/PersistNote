
package com.dv.persistnote.framework.core;

public class MsgDef {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int MSG_INIT_ROOTSCREEN = generateID();
}
