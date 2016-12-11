package ru.mephi.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

/**
 * @author Anton_Chkadua
 */
public class CallStack {
    private static CallStack INSTANCE;
    private final Map<Function, ParseTree> definitions = new HashMap<>();
    private final Deque<Function> current = new ArrayDeque<>();

    private CallStack() {
    }

    public static CallStack getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CallStack();
        }
        return INSTANCE;
    }

    Function getFunction(String name, List<Class> types) throws RuntimeLangException {
        Function candidate =
                definitions.entrySet().stream().filter(entry -> entry.getKey().getName().equals(name)).findAny()
                           .orElse(null).getKey();
        if (candidate == null || candidate.getArgs().size() != types.size()) {
            throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
        }
        for (int i = 0; i < candidate.getArgs().size(); i++) {
            if (!candidate.getArgs().get(i).getType().equals(types.get(i))) {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_FUNCTION);
            }
        }
        return candidate;
    }

    Function getLast() {
        return current.getLast();
    }

    void removeLast() {
        current.removeLast();
    }

    void addFunction(Function function) {
        current.addLast(function);
    }

    void addDefinition(Function function, ParseTree tree) {
        definitions.put(function, tree);
    }
}
