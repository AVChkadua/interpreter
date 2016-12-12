package ru.mephi.interpreter;

public class SimpleVariable
        extends Variable {

    private Integer value;

    SimpleVariable(String name, Class type, Integer value, boolean isConstant) throws RuntimeLangException {
        super(name, type, isConstant);
        if (isConstant && value == null) throw new RuntimeLangException(RuntimeLangException.Type.NO_VALUE_SPECIFIED);
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
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(int value) throws RuntimeLangException {
        if (constantValue) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        this.value = value;
    }
}
