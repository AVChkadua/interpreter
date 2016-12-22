package ru.mephi.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;
import ru.mephi.interpreter.generated.LangBaseVisitor;
import ru.mephi.interpreter.generated.LangParser;
import ru.mephi.interpreter.robot.Robot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class TreeVisitor
        extends LangBaseVisitor<String> {

    private Scope currentScope = Scope.GLOBAL;
    private Robot robot = Robot.getInstance();

    @Override
    public String visitMain(LangParser.MainContext ctx) {
        visitChildren(ctx);
        try {
            visit(getFunction("main", new ArrayList<>()));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitMoveLeft(LangParser.MoveLeftContext ctx) {
        return String.valueOf(robot.left());
    }

    @Override
    public String visitMoveRight(LangParser.MoveRightContext ctx) {
        return String.valueOf(robot.right());
    }

    @Override
    public String visitMoveTop(LangParser.MoveTopContext ctx) {
        return String.valueOf(robot.top());
    }

    @Override
    public String visitMoveBottom(LangParser.MoveBottomContext ctx) {
        return String.valueOf(robot.bottom());
    }

    @Override
    public String visitCreatePortal(LangParser.CreatePortalContext ctx) {
        robot.createTeleport();
        return null;
    }

    @Override
    public String visitTeleport(LangParser.TeleportContext ctx) {
        robot.teleport();
        return null;
    }

    @Override
    public String visitCanMoveBottom(LangParser.CanMoveBottomContext ctx) {
        if (robot.canMoveBottom()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitCanMoveLeft(LangParser.CanMoveLeftContext ctx) {
        if (robot.canMoveLeft()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitCanMoveRight(LangParser.CanMoveRightContext ctx) {
        if (robot.canMoveRight()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitCanMoveTop(LangParser.CanMoveTopContext ctx) {
        if (robot.canMoveTop()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitVisitedBottom(LangParser.VisitedBottomContext ctx) {
        if (robot.visitedBottom()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitVisitedLeft(LangParser.VisitedLeftContext ctx) {
        if (robot.visitedLeft()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitVisitedRight(LangParser.VisitedRightContext ctx) {
        if (robot.visitedRight()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitVisitedTop(LangParser.VisitedTopContext ctx) {
        if (robot.visitedTop()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitIsAtExit(LangParser.IsAtExitContext ctx) {
        if (robot.checkIfAtExit()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String visitNotAtExit(LangParser.NotAtExitContext ctx) {
        if (robot.checkIfAtExit()) {
            return "0";
        }
        return "1";
    }

    @Override
    public String visitAddOp(LangParser.AddOpContext ctx) {
        try {
            if (ctx.op.getText().equals("+")) {
                return toS(toI(visit(ctx.getChild(0))).add(toI(visit(ctx.getChild(2)))));
            } else if (ctx.op.getText().equals("-")) {
                return toS(toI(visit(ctx.getChild(0))).subtract(toI(visit(ctx.getChild(2)))));
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitMultiOp(LangParser.MultiOpContext ctx) {
        try {
            if (ctx.op.getText().equals("*")) {
                return toS(toI(visit(ctx.getChild(0))).multiply(toI(visit(ctx.getChild(2)))));
            } else if (ctx.op.getText().equals("/")) {
                return toS(toI(visit(ctx.getChild(0))).divide(toI(visit(ctx.getChild(2)))));
            } else if (ctx.op.getText().equals("%")) {
                return toS(toI(visit(ctx.getChild(0))).remainder(toI(visit(ctx.getChild(2)))));
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitLength(LangParser.LengthContext ctx) {
        try {
            return toS(getVariable(ctx.getChild(1).getText()).getLength());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return "1";
    }

    @Override
    public String visitComparing(LangParser.ComparingContext ctx) {
        try {
            if (ctx.op.getText().equals("==")) {
                return toS(toI(visit(ctx.getChild(0))).compareTo(toI(visit(ctx.getChild(2)))) == 0 ? BigInteger.ONE :
                        BigInteger.ZERO);
            } else if (ctx.op.getText().equals("!=")) {
                return toS(toI(visit(ctx.getChild(0))).compareTo(toI(visit(ctx.getChild(2)))) != 0 ? BigInteger.ONE :
                        BigInteger.ZERO);
            } else if (ctx.op.getText().equals("<=")) {
                return toS(toI(visit(ctx.getChild(0))).compareTo(toI(visit(ctx.getChild(2)))) <= 0 ? BigInteger.ONE :
                        BigInteger.ZERO);
            } else if (ctx.op.getText().equals(">=")) {
                return toS(toI(visit(ctx.getChild(0))).compareTo(toI(visit(ctx.getChild(2)))) >= 0 ? BigInteger.ONE :
                        BigInteger.ZERO);
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitExistingVariable(LangParser.ExistingVariableContext ctx) {
        try {
            Variable variable = getVariable(visit(ctx.getChild(0)));
            if (ctx.getChild(0).getChild(0).getText().contains("[")) {
                String[] buf = ctx.getChild(0).getChild(0).getText().split("\\[");
                List<BigInteger> indexes = new ArrayList<>();
                for (int i = 1; i < buf.length; i++) {
                    indexes.add(toI(buf[i].replace("]", "")));
                }
                for (BigInteger index : indexes) {
                    variable = variable.getElement(index.intValue());
                }
                variable.setValue(toI(visit(ctx.getChild(2))));
            } else if (variable instanceof SimpleVariable) {
                variable.setValue(extractResult(variable.getType(), visit(ctx.getChild(2))));
            } else if (variable instanceof Pointer) {
                if (ctx.getChild(0).getChild(0).getText().contains("*")) {
                    currentScope.setValueByAddress((Pointer) variable, extractResult(variable.getType(), visit(ctx.getChild(2))));
                } else if (ctx.getChild(0).getChild(0).getText().contains("&")) {
                    variable.setAddress(toI(visit(ctx.getChild(2))));
                }
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitJustDeclaredPointer(LangParser.JustDeclaredPointerContext ctx) {
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        boolean constAddress = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        boolean constValue =
                ctx.getChild(0).getChild(1 + (constAddress ? 1 : 0)).getText().toLowerCase().equals("const");
        int childCount = ctx.getChild(0).getChildCount() - 1;
        try {
            String result = visit(ctx.getChild(2));
            BigInteger value = extractResult(type, result);
            currentScope.add(new Pointer(ctx.getChild(0).getChild(childCount).getText(), type, constValue, value,
                    constAddress));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitJustDeclaredVariable(LangParser.JustDeclaredVariableContext ctx) {
        boolean isConstant = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        try {
            String result = visit(ctx.getChild(2));
            BigInteger value = extractResult(type, result);
            currentScope.add(new SimpleVariable(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(),
                    type, value, isConstant));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitJustDeclaredArray(LangParser.JustDeclaredArrayContext ctx) {
        boolean constSize = ctx.getChild(0).getChild(0).getText().toLowerCase().equals("const");
        Class type = getVariableClass(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 2).getText());
        try {
            int size = toI(visit(ctx.getChild(2))).intValue();
            currentScope.add(new Array(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(), type,
                    size, constSize));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitNamedVariable(LangParser.NamedVariableContext ctx) {
        return ctx.getChild(0).getText();
    }

    @Override
    public String visitArrayElementVariable(LangParser.ArrayElementVariableContext ctx) {
        return ctx.getChild(0).getChild(0).getText();
    }

    @Override
    public String visitPointerValueVariable(LangParser.PointerValueVariableContext ctx) {
        return ctx.getChild(0).getChild(1).getText();
    }

    @Override
    public String visitPointerAddressVariable(LangParser.PointerAddressVariableContext ctx) {
        return ctx.getChild(0).getChild(1).getText();
    }

    @Override
    public String visitConstValue(LangParser.ConstValueContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitArrayElementValue(LangParser.ArrayElementValueContext ctx) {
        try {
            Variable variable = getVariable(ctx.getChild(0).getChild(0).getText())
                    .getElement(toI(visit(ctx.getChild(0).getChild(1))).intValue());
            if (variable.getValue() != null) {
                return toS(variable.getValue());
            } else {
                System.out.println(RuntimeLangException.Type.INVALID_LENGTH);
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return "0";
    }

    @Override
    public String visitNamedVariableValue(LangParser.NamedVariableValueContext ctx) {
        try {
            return toS(getVariable(ctx.getChild(0).getText()).getValue());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return "0";
    }

    @Override
    public String visitPointerValueValue(LangParser.PointerValueValueContext ctx) {
        try {
            Variable variable = getVariable(ctx.getChild(0).getChild(1).getText());
            if (!(variable instanceof Pointer)) {
                System.out.println(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            } else {
                return toS(currentScope.getByAddress((Pointer) variable).getValue());
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return "0";
    }

    @Override
    public String visitVariableAddressValue(LangParser.VariableAddressValueContext ctx) {
        try {
            Variable variable = getVariable(ctx.getChild(0).getChild(1).getText());
            return toS(currentScope.getVariableAddress(variable));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
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
        }
        return null;
    }

    @Override
    public String visitIndex(LangParser.IndexContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public String visitForEachCycle(LangParser.ForEachCycleContext ctx) {
        Variable variable = null;
        try {
            variable = getVariable(ctx.getChild(0).getChild(1).getText());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        try {
            BigInteger length;
            if (variable instanceof Array) {
                types.add(variable.getElement(0).type);
                length = variable.getLength();
            } else if (variable != null) {
                types.add(variable.type);
                length = variable.getLength();
            } else {
                throw new RuntimeLangException(RuntimeLangException.Type.NO_SUCH_VARIABLE);
            }
            for (int j = 2; j < ctx.getChild(0).getChild(2).getChildCount(); j += 2) {
                if (!ctx.getChild(0).getChild(2).getChild(j).getText().equals(")")) {
                    types.add(getVariable(ctx.getChild(0).getChild(2).getChild(j).getText()).type);
                }
            }

            ParseTree functionTree = getFunction(ctx.getChild(0).getChild(2).getChild(0).getText(), types);
            Function func = currentScope.getFunction(ctx.getChild(0).getChild(2).getChild(0).getText(), types);

            for (int i = 0; i < length.intValue(); i++) {
                currentScope = new Scope(currentScope);
                args.clear();
                if (variable instanceof Array) {
                    args.add(variable.getElement(i));
                } else {
                    args.add(variable);
                }
                for (int j = 2; j < ctx.getChild(0).getChild(2).getChildCount(); j += 2) {
                    if (!ctx.getChild(0).getChild(2).getChild(j).getText().equals(")")) {
                        args.add(getVariable(ctx.getChild(0).getChild(2).getChild(j).getText()));
                    }
                }
                for (int j = 0; j < args.size(); j++) {
                    if (args.get(j) instanceof SimpleVariable) {
                        currentScope.add(new SimpleVariable(func.args.get(j).name, func.args.get(j).type,
                                args.get(j).getValue(), false));
                    } else {
                        currentScope.add(new Pointer(func.args.get(j).name, func.args.get(j).type, false,
                                args.get(j).getValue(), false));
                    }
                }
                visit(functionTree);
                currentScope = currentScope.getParent();
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            currentScope = currentScope.getParent();
        }
        return null;
    }

    @Override
    public String visitFunctionCall(LangParser.FunctionCallContext ctx) {
        String name = ctx.getChild(0).getChild(0).getText();
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        String result = "0";
        ParseTree functionTree;
        Function func;
        try {
            for (int i = 2; i < ctx.getChild(0).getChildCount(); i += 2) {
                if (!ctx.getChild(0).getChild(i).getText().equals(")")) {
                    Variable variable = getVariable(ctx.getChild(0).getChild(i).getText());
                    args.add(variable);
                    types.add(variable.getType());
                }
            }
            functionTree = getFunction(name, types);
            func = currentScope.getFunction(name, types);
            currentScope = new Scope(currentScope);
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i) instanceof SimpleVariable) {
                    currentScope.add(new SimpleVariable(func.args.get(i).name, func.args.get(i).type,
                            args.get(i).getValue(), false));
                } else {
                    currentScope.add(new Pointer(func.args.get(i).name, func.args.get(i).type, false,
                            args.get(i).getValue(), false));
                }
            }
            result = visit(functionTree);
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        currentScope = currentScope.getParent();
        return result;
    }

    @Override
    public String visitCall(LangParser.CallContext ctx) {
        String name = ctx.getChild(0).getChild(0).getText();
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        String result = "0";
        ParseTree functionTree;
        Function func;
        try {
            for (int i = 2; i < ctx.getChild(0).getChildCount(); i += 2) {
                if (!ctx.getChild(0).getChild(i).getText().equals(")")) {
                    Variable variable = getVariable(ctx.getChild(0).getChild(i).getText());
                    args.add(variable);
                    types.add(variable.getType());
                }
            }
            functionTree = getFunction(name, types);
            func = currentScope.getFunction(name, types);
            currentScope = new Scope(currentScope);
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i) instanceof SimpleVariable) {
                    currentScope.add(new SimpleVariable(func.args.get(i).name, func.args.get(i).type,
                            args.get(i).getValue(), false));
                } else {
                    currentScope.add(new Pointer(func.args.get(i).name, func.args.get(i).type, false,
                            args.get(i).getValue(), false));
                }
            }
            result = func.returnType + ":" + visit(functionTree);
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        currentScope = currentScope.getParent();
        return result;
    }

    @Override
    public String visitFuncImpl(LangParser.FuncImplContext ctx) {
        List<Argument> args = new ArrayList<>();
        for (int i = 3; i < ctx.getChild(0).getChildCount() - 1; i += 2) {
            args.add(new Argument(ctx.getChild(0).getChild(i).getChild(1).getText(),
                    getVariableClass(ctx.getChild(0).getChild(i).getChild(0).getText())));
        }
        try {
            currentScope.addFunction(
                    new Function(ctx.getChild(0).getChild(1).getText(),
                            getVariableClass(ctx.getChild(0).getChild(0).getText()), args),
                    ctx.getChild(1));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
        }
        return null;
    }

    @Override
    public String visitBody(LangParser.BodyContext ctx) {
        String result = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getChild(0) != null && ctx.getChild(i).getChild(0).getText().toLowerCase().equals(
                    "break")) {
                return "break";
            } else if (ctx.getChild(i).getText().toLowerCase().startsWith("return")) {
                result = visit(ctx.getChild(i));
            } else {
                visit(ctx.getChild(i));
            }
        }
        return result;
    }

    @Override
    public String visitWhileCycle(LangParser.WhileCycleContext ctx) {
        while (visit(ctx.getChild(0).getChild(0).getChild(2)).equals("1")) {
            currentScope = new Scope(currentScope);
            String visitResult = visit(ctx.getChild(0).getChild(1));
            if (visitResult != null && visitResult.equals("break")) {
                currentScope = currentScope.getParent();
                return null;
            }
            currentScope = currentScope.getParent();
        }
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
        if (!visit(ctx.getChild(0).getChild(0).getChild(2)).equals("0")) {
            visit(ctx.getChild(0).getChild(1));
        }
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitBreaking(LangParser.BreakingContext ctx) {
        return "break";
    }

    @Override
    public String visitReturning(LangParser.ReturningContext ctx) {
        return visit(ctx.getChild(0).getChild(1));
    }

    @Override
    public String visitPrint(LangParser.PrintContext ctx) {
        System.out.println(visit(ctx.getChild(1)));
        return null;
    }

    @Override
    public String visitBodyPart(LangParser.BodyPartContext ctx) {
        currentScope = new Scope(currentScope);
        for (int i = 1; i < ctx.getChild(0).getChildCount() - 1; i++) {
            visit(ctx.getChild(0).getChild(i));
        }
        currentScope = currentScope.getParent();
        return null;
    }

    private Variable getVariable(String name) throws RuntimeLangException {
        return currentScope.get(name);
    }

    private Class getVariableClass(String string) {
        switch (string) {
            case "int":
            case "Integer":
                return Integer.class;
            case "long":
            case "Long":
                return Long.class;
            case "byte":
            case "Byte":
                return Byte.class;
        }
        System.out.println("No such type: " + string);
        return Integer.class;
    }

    private ParseTree getFunction(String name, List<Class> types) throws RuntimeLangException {
        return currentScope.getFunctionTree(name, types);
    }

    private BigInteger toI(String s) throws RuntimeLangException {
        return new BigInteger(s);
    }

    private String toS(BigInteger i) {
        return i.toString();
    }

    private BigInteger extractResult(Class type, String result) throws RuntimeLangException {
        if (result.contains(":")) {
            Class resultType = getVariableClass(result.substring(result.lastIndexOf('.') + 1, result.indexOf(':')));
            if (!resultType.equals(type)) throw new RuntimeLangException(
                    RuntimeLangException.Type.ILLEGAL_MODIFICATION);
            return toI(result.substring(result.indexOf(":") + 1));
        } else {
            return  toI(result);
        }
    }
}
