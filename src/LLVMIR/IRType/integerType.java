package LLVMIR.IRType;

public class integerType extends IRType {
    public int size; // int(i32) , bool(i1)

    public boolean isBool(){
        return size == 1;
    }

    public integerType(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "i" + size;
    }

    @Override
    public int getBytes() {
        return size / 8;
    }
}
