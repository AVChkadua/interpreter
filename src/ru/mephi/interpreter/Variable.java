package ru.mephi.interpreter;

import java.math.BigInteger;

/**
 * @author Anton_Chkadua
 */
abstract class Variable
        extends Argument {

    final boolean constantValue;

    Variable(String name, Class type, boolean constantValue) {
        super(name, type);
        this.constantValue = constantValue;
    }

    abstract BigInteger getLength();

    abstract BigInteger getValue() throws RuntimeLangException;

    abstract void setValue(BigInteger value) throws RuntimeLangException;

    abstract BigInteger getAddress() throws RuntimeLangException;

    abstract void setAddress(BigInteger address) throws RuntimeLangException;

    abstract Variable getElement(int i) throws RuntimeLangException;
}
