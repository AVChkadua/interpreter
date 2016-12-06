package ru.mephi.interpreter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Anton_Chkadua
 */
public class MemoryEmulator {

    private Map<Integer, Number> map = new HashMap<>();
    private DecimalFormat format = new DecimalFormat();
    private static MemoryEmulator INSTANCE = null;

    private MemoryEmulator() {
        format.setParseBigDecimal(true);
    }

    public static MemoryEmulator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryEmulator();
        }
        return INSTANCE;
    }

    public void put(String string, Integer address) throws ParseException {
        BigDecimal value = (BigDecimal) format.parse(string);
        Stack<Byte> stack = new Stack<>();
        while (!value.equals(BigDecimal.ZERO)) {
            BigDecimal remainder = value.remainder(new BigDecimal(128));
            stack.add(remainder.byteValue());
            value = value.subtract(remainder);
        }

        int size = stack.size();
        for (int i = 0; i < 4 - size % 4; i++) {
            stack.add((byte) 0);
        }

        stack.forEach(element -> map.put(address, element));
    }

    public Byte getByte(Integer address)
    {
        return (Byte)map.get(address);
    }

    public Integer getInt(Integer address) {
        return (Integer)map.get(address);
    }

    public Long getLong(Integer address) {
        return (Long)map.get(address);
    }

    public void createArray(Integer address, int size) {
        for (int i = 0; i < size; i++) {
            map.put(address, null);
            address++;
        }
    }
}
