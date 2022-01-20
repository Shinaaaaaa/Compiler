package LLVMIR.entity;

import LLVMIR.IRType.IRType;
import LLVMIR.IRType.arrayType;
import LLVMIR.IRType.integerType;

public class stringConst extends entity{
    public String value;
    public int length;

    public stringConst(boolean isLeftValue , String value) {
        super(isLeftValue);
        StringBuilder tmp = new StringBuilder();
        boolean flag = false;
        for (int i = 0 ; i < value.length() ; ++i) {
            if (flag) {
                flag = false;
                length++;
                if (value.charAt(i) == 'n') {
                    tmp.append("\\0A");
                } else if (value.charAt(i) == '\\') {
                    tmp.append("\\5C");
                } else tmp.append("\\22");
            } else {
                if (value.charAt(i) != '\\') {
                    tmp.append(value.charAt(i));
                    length++;
                } else flag = true;
            }
        }
        this.value = tmp.toString();
    }

    @Override
    public IRType getIRType() {
        return new arrayType(length + 1 , new integerType(8));
    }

    @Override
    public String getType() {
        return "[" + (length + 1) + " x i8]";
    }

    @Override
    public String getEntityName() {
        return value;
    }
}
