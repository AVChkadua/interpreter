package ru.mephi.interpreter;

public class SimpleVariable
        extends Variable {

    private int value;

    SimpleVariable(String name, Class type, int value, boolean isConstant) {
        super(name, type, isConstant);
        this.value = value;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public Variable getElement(int i) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) throws RuntimeLangException {
        if (constantValue) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        this.value = value;
    }
}
