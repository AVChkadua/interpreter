package ru.mephi.interpreter;

import java.math.BigInteger;

/**
 * @author Anton_Chkadua
 */
public class Pointer
        extends Variable {
    private BigInteger address;
    private boolean constantAddress;

    Pointer(String name, Class type, boolean constantValue, BigInteger address, boolean constantAddress)
            throws RuntimeLangException {
        super(name, type, constantValue);
        if (constantAddress && address == null) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_VALUE_SPECIFIED);
        }
        this.address = address;
        this.constantAddress = constantAddress;
    }

    @Override
    public BigInteger getAddress() throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_ACCESS);
    }

    @Override
    public void setAddress(BigInteger address) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
    }

    @Override
    public BigInteger getValue() throws RuntimeLangException {
        return address;
    }

    @Override
    public void setValue(BigInteger value) throws RuntimeLangException {
        if (constantAddress) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
    }

    @Override
    public BigInteger getLength() {
        return BigInteger.ONE;
    }

    @Override
    Variable getElement(int i) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }
}
