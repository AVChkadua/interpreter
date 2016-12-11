package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public abstract class Variable
        extends Argument {

    final boolean isConstant;

    protected Variable(String name, Class type, boolean isConstant) {
        super(name, type);
        this.isConstant = isConstant;
    }

    abstract int getLength();

    abstract int getValue() throws RuntimeLangException;

    abstract Variable getElement(int i) throws RuntimeLangException;
}
