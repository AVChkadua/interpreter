package ru.mephi.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anton_Chkadua
 */
public class Scope {

    static Scope GLOBAL = new Scope(null);
    private Scope parent;
    private BigInteger memoryCounter = BigInteger.ZERO;
    private Map<BigInteger, Variable> variables = new HashMap<>();
    private Map<Function, ParseTree> functions = new HashMap<>();

    Scope(Scope parent) {
        this.parent = parent;
    }

    void add(Variable variable) throws RuntimeLangException {
        if (variables.values().contains(variable)) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        variables.put(memoryCounter, variable);
        memoryCounter = memoryCounter.add(BigInteger.ONE);
    }

    Scope getParent() {
        return parent;
    }

    Variable get(String name) throws RuntimeLangException {
        Variable candidate =
                variables.values().stream().filter(variable -> variable.getName().equals(name)).findAny()
                        .orElse(null);
        if (candidate == null) {
            if (parent != null) {
                candidate = parent.get(name);
            } else {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            }
        }
        return candidate;
    }

    Variable getByAddress(Pointer pointer) throws RuntimeLangException {
        return variables.get(pointer.getValue());
    }

    void setValueByAddress(Pointer pointer, BigInteger value) throws RuntimeLangException {
        if (pointer.constantValue) throw new RuntimeLangException(RuntimeLangException.Type.ILLEGAL_MODIFICATION);
        variables.get(pointer.getValue()).setValue(value);
    }

    BigInteger getVariableAddress(Variable variable) throws RuntimeLangException {
        if (!variables.values().contains(variable)) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
        }
        for (Map.Entry<BigInteger, Variable> entry : variables.entrySet()) {
            if (entry.getValue().name.equals(variable.name)) return entry.getKey();
        }
        return null;
    }

    void addFunction(Function function, ParseTree functionTree) throws RuntimeLangException {
        if (functions.containsKey(function)) {
            throw new RuntimeLangException(RuntimeLangException.Type.DUPLICATE_IDENTIFIER);
        }
        functions.put(function, functionTree);
    }

    ParseTree getFunctionTree(String name, List<Class> types) throws RuntimeLangException {
        ParseTree tree = functions.get(getFunction(name, types));
        if (tree == null) {
            if (parent != null) {
                tree = parent.getFunctionTree(name, types);
            } else {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
            }
        }
        return tree;
    }

    Function getFunction(String name, List<Class> types) throws RuntimeLangException {
        Map.Entry<Function, ParseTree> entryCandidate =
                functions.entrySet().stream().filter(entry -> entry.getKey().name.equals(name)).findAny()
                        .orElse(null);
        Function candidate = null;
        if (entryCandidate == null) {
            if (parent != null) {
                candidate = parent.getFunction(name, types);
            }
        } else {
            candidate = entryCandidate.getKey();
        }
        if (candidate == null) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
        }
        if (candidate.args.size() != types.size()) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
        }
        for (int i = 0; i < candidate.args.size(); i++) {
            if (!candidate.args.get(i).getType().equals(types.get(i))) {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
            }
        }
        return candidate;
    }

    @Override
    public String toString() {
        String parent = this.parent != null ? this.parent.toString() : "";
        StringBuilder builder = new StringBuilder();

        for (Variable variable : variables.values()) {
            builder.append(variable.getName()).append("-").append(variable.getType()).append("-length-")
                    .append(variable.getLength());
            try {
                builder.append("-value-").append(variable.getValue()).append('-').append(variable.constantValue)
                        .append("\r\n");
            } catch (RuntimeLangException e) {
                System.out.println(e.getType());
            }
        }
        return builder.insert(0, parent).toString();
    }
}
