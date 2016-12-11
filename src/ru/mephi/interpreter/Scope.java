package ru.mephi.interpreter;

import java.util.Set;

/**
 * @author Anton_Chkadua
 */
public class Scope {

    static Scope GLOBAL = new Scope(null);
    private Scope parent;
    private Set<Variable> variables;

    Scope(Scope parent) {
        this.parent = parent;
    }

    void add(Variable variable) throws RuntimeLangException {
        if (variables.contains(variable)) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        variables.add(variable);
    }

    Variable get(String name) throws RuntimeLangException {
        Variable candidate = variables.stream().filter(variable -> variable.getName().equals(name)).findAny().orElseGet(null);
        if (candidate == null) {
            if (parent != null) {
                parent.get(name);
            } else {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            }
        }
        return candidate;
    }

    void remove(Variable variable) {
        variables.removeIf(var -> var.getName().equals(variable.getName()));
    }
}
