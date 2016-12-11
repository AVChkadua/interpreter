package ru.mephi.interpreter;

import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class Function {
    private String name;
    private int line = 0;
    private List<Argument> args;
    Scope scope;

    Function(String name, List<Argument> args, Scope parentScope) throws RuntimeLangException {
        this.name = name;
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
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof SimpleVariable) {
                scope.add(new SimpleVariable(this.args.get(i).name, this.args.get(i).type, args[i].getValue(),
                        args[i].isConstant));
            } else if (args[i] instanceof Array) {
                scope.add(new Array(this.args.get(i).name, this.args.get(i).type, args[i].getLength(),
                        false));
            }
        }
    }
}
