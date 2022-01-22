package Assembly.Inst;

public class Call extends Inst{
    public String funcName;

    public Call(String funcName){
        this.funcName = funcName;
    }

    @Override
    public String toString() {
        return "call\t" + funcName;
    }
}
