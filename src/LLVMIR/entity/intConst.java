package LLVMIR.entity;

public class intConst extends entity{
    public int value;

    intConst(int value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return "i32";
    }

    @Override
    public String getEntityName() {
        return Integer.toString(value);
    }
}
