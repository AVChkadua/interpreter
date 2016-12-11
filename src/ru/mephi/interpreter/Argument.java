package ru.mephi.interpreter;

/**
 * @author Anton_Chkadua
 */
public class Argument {

    protected final String name;
    protected Class type;

    protected Argument(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Argument && name.equals(((Argument) obj).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
