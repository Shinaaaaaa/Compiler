package LLVMIR.entity;

import LLVMIR.IRType.IRType;

public abstract class entity {
    public boolean isLeftValue;

    entity(boolean isLeftValue) {
        this.isLeftValue = isLeftValue;
    }

    abstract public IRType getIRType();
    abstract public String getType();
    abstract public String getEntityName();
}
