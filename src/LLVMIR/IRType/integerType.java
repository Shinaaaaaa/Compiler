package LLVMIR.IRType;

public class integerType extends IRType {
    public int size; // int(i32) , bool(i8)

    public integerType(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "i" + size;
    }
}
