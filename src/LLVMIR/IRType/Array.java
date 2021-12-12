package LLVMIR.IRType;

public class Array extends IRType {
    public int dimension;
    public IRType indexType;

    public Array(int dimension , IRType indexType) {
        this.dimension = dimension;
        this.indexType = indexType;
    }

    @Override
    public String toString() {
        return "[" + dimension + " x " + indexType.toString() + "]";
    }
}
