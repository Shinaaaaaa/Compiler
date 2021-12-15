package LLVMIR.IRType;

public class arrayType extends IRType {
    public int length;
    public IRType indexType;

    public arrayType(int length , IRType indexType) {
        this.length = length;
        this.indexType = indexType;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + indexType.toString() + "]";
    }
}
