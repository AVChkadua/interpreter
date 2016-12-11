package ru.mephi.interpreter;

import ru.mephi.interpreter.generated.LangBaseVisitor;
import ru.mephi.interpreter.generated.LangParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class TreeVisitor
        extends LangBaseVisitor<Integer> {

    Scope currentScope;
    boolean continueParsing = true;

    @Override
    public Integer visitMoveLeft(LangParser.MoveLeftContext ctx) {
        // TODO
        return null;
    }

    @Override
    public Integer visitMoveRight(LangParser.MoveRightContext ctx) {
        // TODO
        return null;
    }

    @Override
    public Integer visitMoveTop(LangParser.MoveTopContext ctx) {
        // TODO
        return null;
    }

    @Override
    public Integer visitMoveBottom(LangParser.MoveBottomContext ctx) {
        // TODO
        return null;
    }

    @Override
    public Integer visitAddOp(LangParser.AddOpContext ctx) {
        if (ctx.op.getText().equals("+")) {
            return visit(ctx.getChild(0)) + visit(ctx.getChild(1));
        } else if (ctx.op.getText().equals("-")) {
            return visit(ctx.getChild(0)) - visit(ctx.getChild(1));
        }
        return null;
    }

    @Override
    public Integer visitMultiOp(LangParser.MultiOpContext ctx) {
        if (ctx.op.getText().equals("*")) {
            return visit(ctx.getChild(0)) * visit(ctx.getChild(1));
        } else if (ctx.op.getText().equals("/")) {
            return visit(ctx.getChild(0)) / visit(ctx.getChild(1));
        } else if (ctx.op.getText().equals("%")) {
            return visit(ctx.getChild(0)) % visit(ctx.getChild(1));
        }
        return null;
    }

    @Override
    public Integer visitLength(LangParser.LengthContext ctx) {
        try {
            return currentScope.get(ctx.getChild(1).getText()).getLength();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return 1;
    }

    @Override
    public Integer visitComparing(LangParser.ComparingContext ctx) {
        if (ctx.op.getText().equals("==")) {
            return visit(ctx.getChild(0)) == visit(ctx.getChild(1)) ? 1 : 0;
        } else if (ctx.op.getText().equals("!=")) {
            return visit(ctx.getChild(0)) != visit(ctx.getChild(1)) ? 1 : 0;
        } else if (ctx.op.getText().equals("<=")) {
            return visit(ctx.getChild(0)) <= visit(ctx.getChild(1)) ? 1 : 0;
        } else if (ctx.op.getText().equals(">=")) {
            return visit(ctx.getChild(0)) >= visit(ctx.getChild(1)) ? 1 : 0;
        }
        return null;
    }

    @Override
    public Integer visitAssign(LangParser.AssignContext ctx) {
        visit(ctx.getChild(0));
        Variable variable;
        try {
            if (ctx.getChild(0).getText().contains("[")) {
                variable = getVariable(ctx.getChild(ctx.getChildCount() - 1).getText());
                if (variable instanceof SimpleVariable) {
                    ((SimpleVariable)variable).setValue(visit(ctx.getChild(2)));
                } else if (variable instanceof Pointer) {
                    if (ctx.getChild(0).getChild(0).getText().equals("*")) {
                        ((Pointer)variable).setValue(visit(ctx.getChild(2)));
                    } else {
                        ((Pointer)variable).setAddress(visit(ctx.getChild(2)));
                    }
                }
            } else {
                // TODO getting array element
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitConstValue(LangParser.ConstValueContext ctx) {
        return Integer.parseInt(ctx.getText());
    }

    @Override
    public Integer visitArrayElementValue(LangParser.ArrayElementValueContext ctx) {
        try {
            return getVariable(ctx.getChild(0).getText()).getElement(visit(ctx.getChild(1))).getValue();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return 0;
    }

    @Override
    public Integer visitPointerValueValue(LangParser.PointerValueValueContext ctx) {
        try {
            return getVariable(ctx.getChild(1).getText()).getValue();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return 0;
    }

    @Override
    public Integer visitPointerAddressValue(LangParser.PointerAddressValueContext ctx) {
        Variable variable;
        try {
            variable = getVariable(ctx.getChild(1).getText());
            if (!(variable instanceof Pointer)) {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            }
            return ((Pointer)variable).getAddress();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return 0;
    }

    @Override
    public Integer visitDeclareVariable(LangParser.DeclareVariableContext ctx) {
        Class type = getVariableClass(ctx.getChild(ctx.getChildCount() - 2).getText());
        try {
            currentScope.add(new SimpleVariable(ctx.getChild(ctx.getChildCount() - 1).getText(), type, 0,
                    ctx.getChild(0).getText().toLowerCase().equals("const")));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitDeclarePointer(LangParser.DeclarePointerContext ctx) {
        Class type = getVariableClass(ctx.getChild(ctx.getChildCount() - 2).getText());
        boolean constAddress = ctx.getChild(0).getText().toLowerCase().equals("const");
        boolean constValue = ctx.getChild(1 + (constAddress ? 1 : 0)).getText().toLowerCase().equals("const");
        int childCount = ctx.getChildCount();
        try {
            currentScope.add(new Pointer(ctx.getChild(childCount - 1).getText(), type, constValue, -1, constAddress));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitDeclareArray(LangParser.DeclareArrayContext ctx) {
        boolean constSize = ctx.getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(ctx.getChildCount() - 3).getText());
        try {
            currentScope.add(new Array(ctx.getChild(ctx.getChildCount() - 1).getText(), type, visitChildren(ctx),
                    constSize));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitIndex(LangParser.IndexContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public Integer visitForEach(LangParser.ForEachContext ctx) {
        Variable variable = null;
        try {
            variable = getVariable(ctx.getChild(0).getText());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        if (!(variable instanceof Array)) {
            System.out.println("Variable is not an array");
            continueParsing = false;
            return null;
        }
        Array array = (Array)variable;
        List<Class> types = new ArrayList<>();
        try {
            types.add(array.getElement(0).getType());
            Function function = getFunction(ctx.func.getText(), types);
            for (int i = 0; i < array.getLength() - 1; i++) {
                function.invoke(array.getElement(i));
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitFuncCall(LangParser.FuncCallContext ctx) {
        // TODO result returning
        currentScope = new Scope(currentScope);
        int result = 0;
        List<Class> types = new ArrayList<>();
        List<Variable> variables = new ArrayList<>();
        for (int i = 2; i < ctx.getChildCount() - 1; i += 2) {
            try {
                types.add(currentScope.get(ctx.getChild(i).getText()).getType());
                variables.add(currentScope.get(ctx.getChild(i).getText()));
            } catch (RuntimeLangException e) {
                System.out.println(e.getType());
                continueParsing = false;
                currentScope = currentScope.getParent();
                return result;
            }
        }
        try {
            getFunction(ctx.getChild(0).getText(), types).invoke(variables.toArray(new Variable[]{}));
            currentScope = currentScope.getParent();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return result;
    }

    @Override
    public Integer visitFunctionDeclaration(LangParser.FunctionDeclarationContext ctx) {
        List<Argument> args = new ArrayList<>();
        for (int i = 2; i < ctx.getChildCount() - 1; i += 2) {
            args.add(new Argument(ctx.getChild(i).getChild(1).getText(),
                    getVariableClass(ctx.getChild(i).getChild(0).getText())));
        }
        try {
            Function function = new Function(ctx.funcName.getText(), getVariableClass(ctx.getChild(0).getText()), args,
                    currentScope);
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public Integer visitBody(LangParser.BodyContext ctx) {
        currentScope = new Scope(currentScope);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().toLowerCase().equals("break")) {
                return 0;
            }
            visit(ctx.getChild(i));
        }
        return null;
    }

    @Override
    public Integer visitWhileCycling(LangParser.WhileCyclingContext ctx) {
        while (visit(ctx.getChild(0)) == 1) {
            visit(ctx.getChild(2));
        }
        visit(ctx.getChild(4));
        return null;
    }

    @Override
    public Integer visitZero(LangParser.ZeroContext ctx) {
        if (visit(ctx.zeroCond) == 1) {
            visit(ctx.getChild(2));
        }
        return null;
    }

    @Override
    public Integer visitNotZero(LangParser.NotZeroContext ctx) {
        if (visit(ctx.notZeroCond) == 1) {
            visit(ctx.getChild(2));
        }
        return null;
    }

    @Override
    public Integer visitBreaking(LangParser.BreakingContext ctx) {
        currentScope = currentScope.getParent();
        return 0;
    }

    private Variable getVariable(String name) throws RuntimeLangException {
        return currentScope.get(name);
    }

    private Class getVariableClass(String string) {
        switch (string) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
        }
        System.out.println("No such type");
        continueParsing = false;
        return Integer.class;
    }

    private Function getFunction(String name, List<Class> types) throws RuntimeLangException {
        return CallStack.getInstance().getFunction(name, types);
    }
}
