package ru.mephi.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anton_Chkadua
 */
public class MemoryEmulator {

    private Map<Integer, Integer> map = new HashMap<>();
    private static MemoryEmulator INSTANCE = null;

    private MemoryEmulator() {}

    public static MemoryEmulator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryEmulator();
        }
        return INSTANCE;
    }

    public void put(Integer address, Integer value) {
        map.put(address, value);
    }

    public int getValue(int address)
    {
        return map.get(address);
    }
}
