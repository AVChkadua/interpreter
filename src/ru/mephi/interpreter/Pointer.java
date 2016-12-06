package ru.mephi.interpreter;

import java.text.ParseException;

/**
 * @author Anton_Chkadua
 */
public class Pointer {
    private int address;
    private MemoryEmulator memory;
    private boolean constantAddress;
    private boolean constantValue;

    public Pointer(int address, boolean constantAddress, boolean constantValue) {
        this.address = address;
        this.memory = MemoryEmulator.getInstance();
        this.constantAddress = constantAddress;
        this.constantValue = constantValue;
    }

    public int getAddress() {
        return address;
    }

    public Byte getByte(Integer address)
    {
        return memory.getByte(address);
    }

    public Integer getInt(Integer address) {
        return memory.getInt(address);
    }

    public Long getLong(Integer address) {
        return memory.getLong(address);
    }

    public void setAddress(int address) {
        if (constantAddress) throw new IllegalStateException();
        this.address = address;
    }

    public void setValue(String value) throws ParseException {
        if (constantValue) throw new IllegalStateException();
        memory.put(value, address);
    }

    public int getSize() {
        return 1;
    }
}
