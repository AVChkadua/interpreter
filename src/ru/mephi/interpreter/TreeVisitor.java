package ru.mephi.interpreter;

import ru.mephi.interpreter.generated.LangBaseVisitor;
import ru.mephi.interpreter.generated.LangParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class TreeVisitor
        extends LangBaseVisitor<String> {

    Scope currentScope = Scope.GLOBAL;
    boolean continueParsing = true;

    @Override
    public String visitMoveLeft(LangParser.MoveLeftContext ctx) {
        // TODO
        return null;
    }

    @Override
    public String visitMoveRight(LangParser.MoveRightContext ctx) {
        // TODO
        return null;
    }

    @Override
    public String visitMoveTop(LangParser.MoveTopContext ctx) {
        // TODO
        return null;
    }

    @Override
    public String visitMoveBottom(LangParser.MoveBottomContext ctx) {
        // TODO
        return null;
    }

    @Override
    public String visitAddOp(LangParser.AddOpContext ctx) {
        if (ctx.op.getText().equals("+")) {
            return toS(toI(visit(ctx.getChild(0))) + toI(visit(ctx.getChild(1))));
        } else if (ctx.op.getText().equals("-")) {
            return toS(toI(visit(ctx.getChild(0))) - toI(visit(ctx.getChild(1))));
        }
        return null;
    }

    @Override
    public String visitMultiOp(LangParser.MultiOpContext ctx) {
        if (ctx.op.getText().equals("*")) {
            return toS(toI(visit(ctx.getChild(0))) * toI(visit(ctx.getChild(1))));
        } else if (ctx.op.getText().equals("/")) {
            return toS(toI(visit(ctx.getChild(0))) / toI(visit(ctx.getChild(1))));
        } else if (ctx.op.getText().equals("%")) {
            return toS(toI(visit(ctx.getChild(0))) % toI(visit(ctx.getChild(1))));
        }
        return null;
    }

    @Override
    public String visitLength(LangParser.LengthContext ctx) {
        try {
            return toS(getVariable(ctx.getChild(1).getText()).getLength());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return "1";
    }

    @Override
    public String visitComparing(LangParser.ComparingContext ctx) {
        if (ctx.op.getText().equals("==")) {
            return toS(toI(visit(ctx.getChild(0))) == toI(visit(ctx.getChild(1))) ? 1 : 0);
        } else if (ctx.op.getText().equals("!=")) {
            return toS(toI(visit(ctx.getChild(0))) != toI(visit(ctx.getChild(1))) ? 1 : 0);
        } else if (ctx.op.getText().equals("<=")) {
            return toS(toI(visit(ctx.getChild(0))) <= toI(visit(ctx.getChild(1))) ? 1 : 0);
        } else if (ctx.op.getText().equals(">=")) {
            return toS(toI(visit(ctx.getChild(0))) >= toI(visit(ctx.getChild(1))) ? 1 : 0);
        }
        return null;
    }

    @Override
    public String visitExistingVariable(LangParser.ExistingVariableContext ctx) {
        try {
            Variable variable = getVariable(visit(ctx.getChild(0).getChild(0)));
            if (ctx.getChild(0).getChild(0).getText().contains("[")) {
                String[] buf = ctx.getChild(0).getChild(0).getText().split("\\[");
                List<Integer> indexes = new ArrayList<>();
                for (int i = 1; i < buf.length; i++) {
                    indexes.add(toI(buf[i].replace("]", "")));
                }
                for (Integer index : indexes) {
                    variable = variable.getElement(index);
                }
            }
            variable.setValue(toI(visit(ctx.getChild(0).getChild(2))));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitJustDeclaredPointer(LangParser.JustDeclaredPointerContext ctx) {
        Integer value = toI(visit(ctx.getChild(2)));
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        boolean constAddress = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        boolean constValue =
                ctx.getChild(0).getChild(1 + (constAddress ? 1 : 0)).getText().toLowerCase().equals("const");
        int childCount = ctx.getChild(0).getChildCount() - 1;
        try {
            currentScope.add(new Pointer(ctx.getChild(0).getChild(childCount).getText(), type, constValue, value,
                    constAddress));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitJustDeclaredVariable(LangParser.JustDeclaredVariableContext ctx) {
        boolean isConstant = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        Integer value = toI(visit(ctx.getChild(2)));
        try {
            currentScope.add(new SimpleVariable(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(),
                    type, value, isConstant));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitJustDeclaredArray(LangParser.JustDeclaredArrayContext ctx) {
        Integer size = toI(visit(ctx.getChild(2)));
        boolean constSize = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        try {
            currentScope.add(new Array(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(), type,
                    size, constSize));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitConstValue(LangParser.ConstValueContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitArrayElementValue(LangParser.ArrayElementValueContext ctx) {
        // TODO
        try {
            return toS(getVariable(ctx.getChild(0).getText()).getElement(toI(visit(ctx.getChild(1)))).getValue());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return "0";
    }

    @Override
    public String visitPointerValueValue(LangParser.PointerValueValueContext ctx) {
        // TODO
        try {
            return toS(getVariable(ctx.getChild(1).getText()).getValue());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return "0";
    }

    @Override
    public String visitPointerAddressValue(LangParser.PointerAddressValueContext ctx) {
        // TODO
        Variable variable;
        try {
            variable = getVariable(ctx.getChild(1).getText());
            if (!(variable instanceof Pointer)) {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            }
            return toS(((Pointer)variable).getAddress());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return "0";
    }

    @Override
    public String visitVariableDeclaration(LangParser.VariableDeclarationContext ctx) {
        boolean isConstant = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());

        try {
            currentScope.add(new SimpleVariable(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(),
                    type, null, isConstant));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitPointerDeclaration(LangParser.PointerDeclarationContext ctx) {
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        boolean constAddress = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        boolean constValue =
                ctx.getChild(0).getChild(1 + (constAddress ? 1 : 0)).getText().toLowerCase().equals("const");
        int childCount = ctx.getChild(0).getChildCount() - 1;
        try {
            currentScope.add(new Pointer(ctx.getChild(0).getChild(childCount).getText(), type, constValue, null,
                    constAddress));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitArrayDeclaration(LangParser.ArrayDeclarationContext ctx) {
        boolean constSize = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        try {
            currentScope
                    .add(new Array(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(), type, null,
                            constSize));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        System.out.println(currentScope);
        return null;
    }

    @Override
    public String visitIndex(LangParser.IndexContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public String visitForEach(LangParser.ForEachContext ctx) {
        // TODO
        Variable variable = null;
        try {
            variable = getVariable(ctx.getChild(0).getChild(0).getText());
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
    public String visitFuncCall(LangParser.FuncCallContext ctx) {
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
                return toS(result);
            }
        }
        try {
            getFunction(ctx.getChild(0).getText(), types).invoke(variables.toArray(new Variable[]{}));
            currentScope = currentScope.getParent();
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return toS(result);
    }

    @Override
    public String visitFunctionDeclaration(LangParser.FunctionDeclarationContext ctx) {
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
    public String visitBody(LangParser.BodyContext ctx) {
        currentScope = new Scope(currentScope);
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().toLowerCase().equals("break")) {
                return null;
            }
            visit(ctx.getChild(i));
        }
        return null;
    }

    @Override
    public String visitWhileCycle(LangParser.WhileCycleContext ctx) {
        currentScope = new Scope(currentScope);
        while (visit(ctx.getChild(0).getChild(0).getChild(2)).equals("1")) {
            visit(ctx.getChild(0).getChild(1));
        }
        currentScope = currentScope.getParent();
        visit(ctx.getChild(0).getChild(3));
        return null;
    }

    @Override
    public String visitIfZero(LangParser.IfZeroContext ctx) {
        currentScope = new Scope(currentScope);
        if (visit(ctx.getChild(0).getChild(0).getChild(2)).equals("0")) {
            visit(ctx.getChild(0).getChild(1));
        }
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitIfNotZero(LangParser.IfNotZeroContext ctx) {
        currentScope = new Scope(currentScope);
        if (visit(ctx.getChild(0).getChild(0).getChild(2)).equals("0")) {
            visit(ctx.getChild(0).getChild(1));
        }
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitBreaking(LangParser.BreakingContext ctx) {
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitReturning(LangParser.ReturningContext ctx) {
        // TODO
        return visit(ctx.getChild(0).getChild(1));
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
        // TODO throw exception here
        System.out.println("No such type: " + string);
        continueParsing = false;
        return Integer.class;
    }

    private Function getFunction(String name, List<Class> types) throws RuntimeLangException {
        return CallStack.getInstance().getFunction(name, types);
    }

    private int toI(String s) {
        return Integer.valueOf(s);
    }

    private String toS(int i) {
        return String.valueOf(i);
    }
}
