package LLVMIR.IRType;

public class pointerType extends IRType {
    public IRType pointerType;

    public pointerType(IRType pointerType) {
        this.pointerType = pointerType;
    }

    @Override
    public String toString() {
        return pointerType.toString() + "*";
    }
}
