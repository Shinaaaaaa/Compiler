package LLVMIR.IRType;

public class Pointer extends IRType {
    public IRType pointerType;

    public Pointer(IRType pointerType) {
        this.pointerType = pointerType;
    }

    @Override
    public String toString() {
        return pointerType.toString() + "*";
    }
}
