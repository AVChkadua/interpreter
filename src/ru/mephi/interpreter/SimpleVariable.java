package ru.mephi.interpreter;

import java.math.BigInteger;

public class SimpleVariable
        extends Variable {

    private BigInteger value;

    SimpleVariable(String name, Class type, BigInteger value, boolean isConstant) throws RuntimeLangException {
        super(name, type, isConstant);
        if (isConstant && value == null) throw new RuntimeLangException(RuntimeLangException.Type.NO_VALUE_SPECIFIED);
        this.value = value;
    }

    @Override
    public BigInteger getLength() {
        return BigInteger.ONE;
    }

    @Override
    public Variable getElement(int i) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }

    @Override
    public BigInteger getValue() {
        return value;
    }

    @Override
    public void setValue(BigInteger value) throws RuntimeLangException {
        if (constantValue || value == null) {
            throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        }
        this.value = value;
    }

    @Override
    BigInteger getAddress() throws RuntimeLangException {
        return null;
    }

    @Override
    void setAddress(BigInteger address) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
    }
}
