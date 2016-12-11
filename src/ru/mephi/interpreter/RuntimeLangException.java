package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public class RuntimeLangException
        extends Exception {

    Type type;

    public RuntimeLangException(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    enum Type {
        INVALID_LENGTH("Invalid length"), NO_SUCH_VARIABLE("No such variable found"),
        NO_SUCH_FUNCTION("No such function found"), DUPLICATE_IDENTIFIER("Duplicate identifier"),
        ILLEGAL_MODIFICATION("Illegal modification"), NO_SUCH_TYPE("No such type");

        String string;

        Type(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
