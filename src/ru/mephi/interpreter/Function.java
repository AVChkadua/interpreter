package ru.mephi.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class Function {
    Class returnType;
    String name;
    List<Argument> args = new ArrayList<>();

    Function(String name, Class returnType, List<Argument> args) throws RuntimeLangException {
        this.name = name;
        this.returnType = returnType;
        if (args.stream().distinct().count() != args.size()) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        this.args.addAll(args);
    }
}
