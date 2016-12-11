package ru.mephi.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Anton_Chkadua
 */
public class Function {
    Scope scope;
    private Class returnType;
    private String name;
    private int line = 0;
    private List<Argument> args = new ArrayList<>();

    Function(String name, Class returnType, List<Argument> args, Scope parentScope) throws RuntimeLangException {
        this.name = name;
        this.returnType = returnType;
        if (args.stream().distinct().count() != args.size()) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        this.args.addAll(args);
        this.scope = new Scope(parentScope);
    }

    String getName() {
        return name;
    }

    List<Argument> getArgs() {
        return args;
    }

    Scope getScope() {
        return scope;
    }

    public void invoke(Variable... args) throws RuntimeLangException {
        if (args.length != this.args.size()) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
        }
        if (!Stream.of(args).allMatch(variable -> variable instanceof SimpleVariable)) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
        }
        for (int i = 0; i < args.length; i++) {
            scope.add(new SimpleVariable(this.args.get(i).name, this.args.get(i).type, args[i].getValue(),
                    args[i].constantValue));
        }
    }
}
