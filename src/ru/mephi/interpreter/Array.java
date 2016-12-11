package ru.mephi.interpreter;

import java.util.ArrayList;

/**
 * @author Anton_Chkadua
 */
public class Array extends Variable {

    ArrayList<Variable> content;
    private int quant = 4;

    Array(String name, Class type, Integer size, boolean isConstant) throws RuntimeLangException {
        super(name, type, isConstant);
        if (size != null) {
            content = new ArrayList<>(size);
            quant = size;
        } else if (isConstant) {
            throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
        } else {
            content = new ArrayList<>(4);
        }
    }

    @Override
    int getLength() {
        return content.size();
    }

    @Override
    int getValue() throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }

    @Override
    Variable getElement(int i) throws RuntimeLangException {
        if (i > content.size()) throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
        Variable result = content.get(i);
        if (result == null) {
            throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
        }
        return result;
    }

    void setElement(int i, Variable value) throws RuntimeLangException {
        if (!value.getClass().equals(type)) {
            throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        } else if (i < content.size()) {
            content.remove(i);
            content.add(i, value);
        } else if (i == content.size() + 1) {
            ArrayList<Variable> buf = content;
            content = new ArrayList<>(buf.size() + 4);
            content.addAll(buf);
            content.add(value);
        } else {
            throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        }
    }

    void addElement(Variable value) throws RuntimeLangException {
        setElement(content.size(), value);
    }
}
