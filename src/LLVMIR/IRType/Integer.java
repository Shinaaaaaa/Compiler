package LLVMIR.IRType;

public class Integer extends IRType {
    public int size; // int(i32) , bool(i8)

    public Integer(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "i" + size;
    }
}
