package ru.mephi.interpreter;

import java.text.ParseException;

/**
 * @author Anton_Chkadua
 */
public class Pointer {
    private int address;
    private MemoryEmulator memory = MemoryEmulator.getInstance();
    private boolean constantAddress;
    private boolean constantValue;

    public Pointer(int address, boolean constantAddress, boolean constantValue) {
        this.address = address;
        this.constantAddress = constantAddress;
        this.constantValue = constantValue;
    }

    public int getAddress() {
        return address;
    }

    public int getValue(Integer address) {
        return memory.getValue(address);
    }

    public void setAddress(int address) throws RuntimeLangException {
        if (constantAddress) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        this.address = address;
    }

    public void setValue(int value) throws ParseException, RuntimeLangException {
        if (constantValue) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        memory.put(value, address);
    }

    public int getSize() {
        return 1;
    }
}
