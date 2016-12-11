package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public class RuntimeLangException extends Exception {

    Type type;

    public RuntimeLangException(Type type) {
        this.type = type;
    }

    enum Type {
        INVALID_LENGTH, NO_SUCH_VARIABLE, NO_SUCH_FUNCTION, DUPLICATE_IDENTIFIER, ILLEGAL_MODIFICATION
    }
}
