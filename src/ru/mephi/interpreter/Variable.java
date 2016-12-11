package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public abstract class Variable
        extends Argument {

    final boolean constantValue;

    protected Variable(String name, Class type, boolean constantValue) {
        super(name, type);
        this.constantValue = constantValue;
    }

    abstract int getLength();

    abstract int getValue() throws RuntimeLangException;

    abstract Variable getElement(int i) throws RuntimeLangException;
}
