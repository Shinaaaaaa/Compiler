package LLVMIR.IRType;

public class pointerType extends IRType {
    public IRType pointerType;

    public pointerType(IRType pointerType) {
        this.pointerType = pointerType;
    }

    public boolean isString() {
        return pointerType instanceof integerType && ((integerType) pointerType).size == 8;
    }

    @Override
    public String toString() {
        return pointerType.toString() + "*";
    }

    @Override
    public int getBytes() {
        return 8;
    }
}
