package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public class Pointer
        extends Variable {
    private int address;
    private MemoryEmulator memory = MemoryEmulator.getInstance();
    private boolean constantAddress;

    public Pointer(String name, Class type, boolean constantValue, int address, boolean constantAddress)
            throws RuntimeLangException {
        super(name, type, constantValue);
        if (constantAddress && address == -1) {
            throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        }
        this.address = address;
        this.constantAddress = constantAddress;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) throws RuntimeLangException {
        if (constantAddress) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        this.address = address;
    }

    @Override
    public int getValue() throws RuntimeLangException {
        return memory.getValue(address);
    }

    public void setValue(int value) throws RuntimeLangException {
        if (constantValue) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        memory.put(value, address);
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    Variable getElement(int i) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }
}
