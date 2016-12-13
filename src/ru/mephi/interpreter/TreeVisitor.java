package ru.mephi.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;
import ru.mephi.interpreter.generated.LangBaseVisitor;
import ru.mephi.interpreter.generated.LangParser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anton_Chkadua
 */
public class TreeVisitor
        extends LangBaseVisitor<String> {

    private Scope currentScope = Scope.GLOBAL;
    private boolean continueParsing = true;

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
        try {
            if (ctx.op.getText().equals("+")) {
                return toS(toI(visit(ctx.getChild(0))).add(toI(visit(ctx.getChild(2)))));
            } else if (ctx.op.getText().equals("-")) {
                return toS(toI(visit(ctx.getChild(0))).add(toI(visit(ctx.getChild(2)))));
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitMultiOp(LangParser.MultiOpContext ctx) {
        try {
            if (ctx.op.getText().equals("*")) {
                return toS(toI(visit(ctx.getChild(0))).multiply(toI(visit(ctx.getChild(1)))));
            } else if (ctx.op.getText().equals("/")) {
                return toS(toI(visit(ctx.getChild(0))).divide(toI(visit(ctx.getChild(1)))));
            } else if (ctx.op.getText().equals("%")) {
                return toS(toI(visit(ctx.getChild(0))).remainder(toI(visit(ctx.getChild(1)))));
            }
        } catch (RuntimeLangException e) {
            System.out.println();
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
            continueParsing = false;
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
                variable.setValue(toI(visit(ctx.getChild(2))));
            } else if (variable instanceof Pointer) {
                if (ctx.getChild(0).getChild(0).getText().contains("*")) {
                    currentScope.setValueByAddress((Pointer)variable, toI(visit(ctx.getChild(2))));
                } else if (ctx.getChild(0).getChild(0).getText().contains("&")) {
                    variable.setAddress(toI(visit(ctx.getChild(2))));
                }
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
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
            BigInteger value = toI(visit(ctx.getChild(2)));
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
        try {
            BigInteger value = toI(visit(ctx.getChild(2)));
            currentScope.add(new SimpleVariable(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(),
                    type, value, isConstant));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        System.out.println(currentScope);
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
            continueParsing = false;
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
            return toS(getVariable(ctx.getChild(0).getChild(0).getText())
                    .getElement(toI(visit(ctx.getChild(0).getChild(1))).intValue()).getValue());
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return "0";
    }

    @Override
    public String visitNamedVariableValue(LangParser.NamedVariableValueContext ctx) {
        try {
            return toS(getVariable(ctx.getChild(0).getText()).getValue());
        } catch (RuntimeLangException e) {
            e.printStackTrace();
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
                return toS(currentScope.getByAddress((Pointer)variable).getValue());
            }
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
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
            continueParsing = false;
        }
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        try {
            BigInteger length = BigInteger.ONE;
            if (variable instanceof Array) {
                types.add(variable.getElement(0).getType());
                length = variable.getLength();
            } else if (variable != null) {
                types.add(variable.type);
                length = variable.getLength();
            }
            for (int i = 0; i < length.intValue() - 1; i++) {
                currentScope = new Scope(currentScope);
                ParseTree functionTree = getFunction(ctx.getChild(0).getChild(2).getText(), types);
                Function func = currentScope.getFunction(ctx.getChild(0).getChild(2).getText(), types);
                for (int j = 2; j < ctx.getChild(0).getChild(2).getChildCount(); j += 2) {
                    args.add(getVariable(ctx.getChild(0).getChild(2).getChild(j).getText()));
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
            continueParsing = false;
            currentScope = currentScope.getParent();
        }
        return null;
    }

    @Override
    public String visitFunctionCall(LangParser.FunctionCallContext ctx) {
        // TODO result returning
        String name = ctx.getChild(0).getChild(0).getText();
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        String result = "0";
        ParseTree functionTree;
        Function func;
        try {
            functionTree = getFunction(name, types);
            func = currentScope.getFunction(name, types);
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
            return result;
        }
        currentScope = new Scope(currentScope);
        try {
            for (int i = 2; i < ctx.getChild(0).getChildCount(); i += 2) {
                args.add(getVariable(ctx.getChild(0).getChild(i).getText()));
            }
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
            continueParsing = false;
        }
        currentScope = currentScope.getParent();
        return result;
    }

    @Override
    public String visitCall(LangParser.CallContext ctx) {
        // TODO result returning
        String name = ctx.getChild(0).getChild(0).getText();
        List<Variable> args = new ArrayList<>();
        List<Class> types = new ArrayList<>();
        String result = "0";
        ParseTree functionTree;
        Function func;
        try {
            functionTree = getFunction(name, types);
            func = currentScope.getFunction(name, types);
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
            return result;
        }
        currentScope = new Scope(currentScope);
        try {
            for (int i = 2; i < ctx.getChild(0).getChildCount(); i += 2) {
                if (!ctx.getChild(0).getChild(i).getText().equals(")")) {
                    args.add(getVariable(ctx.getChild(0).getChild(i).getText()));
                }
            }
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
            continueParsing = false;
        }
        System.out.println(result);
        currentScope = currentScope.getParent();
        return result;
    }

    @Override
    public String visitFunctionImplementation(LangParser.FunctionImplementationContext ctx) {
        List<Argument> args = new ArrayList<>();
        for (int i = 2; i < ctx.getChild(0).getChildCount() - 1; i += 2) {
            args.add(new Argument(ctx.getChild(0).getChild(i).getChild(1).getText(),
                    getVariableClass(ctx.getChild(0).getChild(i).getChild(0).getText())));
        }
        try {
            currentScope.addFunction(
                    new Function(ctx.getChild(0).getChild(0).getChild(1).getText(),
                            getVariableClass(ctx.getChild(0).getChild(0).getChild(0).getText()), args),
                    ctx.getChild(0).getChild(1));
        } catch (RuntimeLangException e) {
            System.out.println(e.getType());
            continueParsing = false;
        }
        return null;
    }

    @Override
    public String visitBody(LangParser.BodyContext ctx) {
        String result = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().toLowerCase().equals("break")) {
                return null;
            }
            result = visit(ctx.getChild(i));
        }
        return result;
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
        System.out.println("No such type: " + string);
        continueParsing = false;
        return Integer.class;
    }

    private ParseTree getFunction(String name, List<Class> types) throws RuntimeLangException {
        return currentScope.getFunctionTree(name, types);
    }

    private void saveFunction(Function function, ParseTree tree) throws RuntimeLangException {
        currentScope.addFunction(function, tree);
    }

    private BigInteger toI(String s) throws RuntimeLangException {
        return new BigInteger(s);
    }

    private String toS(BigInteger i) {
        return i.toString();
    }
}
