package ru.mephi.interpreter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Anton_Chkadua
 */
public class Scope {

    static Scope GLOBAL = new Scope(null);
    private Scope parent;
    private Set<Variable> variables = new HashSet<>();

    Scope(Scope parent) {
        this.parent = parent;
    }

    void add(Variable variable) throws RuntimeLangException {
        if (variables.contains(variable)) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        variables.add(variable);
    }

    public Scope getParent() {
        return parent;
    }

    Variable get(String name) throws RuntimeLangException {
        Variable candidate =
                variables.stream().filter(variable -> variable.getName().equals(name)).findAny()
                         .orElseThrow(() -> new RuntimeLangException(
                                 RuntimeLangException.Type.NO_SUCH_VARIABLE));
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

    @Override
    public String toString() {
        String parent = this.parent != null ? this.parent.toString() : "";
        StringBuilder builder = new StringBuilder();

        for (Variable variable : variables) {
            builder.append(variable.getName()).append("-").append(variable.getType()).append("-length-")
                   .append(variable.getLength());
            try {
                builder.append("-value-").append(variable.getValue()).append('-').append(variable.constantValue)
                       .append("\r\n");
            } catch (RuntimeLangException e) {
                e.printStackTrace();
            }
        }
        return builder.insert(0, parent).toString();
    }
}
