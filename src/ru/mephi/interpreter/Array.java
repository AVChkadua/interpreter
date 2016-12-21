package ru.mephi.interpreter;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author Anton_Chkadua
 */
class Array
        extends Variable {

    private ArrayList<Variable> content;
    private int quant = 4;

    Array(String name, Class type, Integer size, boolean isConstant) throws RuntimeLangException {
        super(name, type, isConstant);
        if (size != null) {
            content = new ArrayList<>(size);
            quant = size;
        } else if (isConstant) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_VALUE_SPECIFIED);
        } else {
            content = new ArrayList<>(4);
        }
    }

    @Override
    BigInteger getLength() {
        return BigInteger.valueOf(content.size());
    }

    @Override
    BigInteger getValue() throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
    }

    @Override
    void setValue(BigInteger value) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
    }

    @Override
    Variable getElement(int i) throws RuntimeLangException {
        if (i > content.size()) throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
        if (i == content.size()) {
            ArrayList<Variable> oldContent = content;
            content = new ArrayList<>(oldContent.size() + quant);
            content.addAll(oldContent);
            Variable newElement = new SimpleVariable("element", type, null, false);
            content.add(newElement);
        }
        Variable result = content.get(i);
        if (result == null) {
            throw new RuntimeLangException(RuntimeLangException.Type.INVALID_LENGTH);
        }
        return result;
    }

    @Override
    BigInteger getAddress() throws RuntimeLangException {
        throw new RuntimeException();
    }

    @Override
    void setAddress(BigInteger address) throws RuntimeLangException {
        throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
    }
}
