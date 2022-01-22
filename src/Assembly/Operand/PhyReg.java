package Assembly.Operand;

public class PhyReg extends Reg{
    public String name;
    public boolean status;

    public PhyReg(String name){
        this.name = name;
        status = false;
    }

    public boolean isBusy(){
        return status;
    }

    public void busy() {
        status = true;
    }

    public void free() {
        status = false;
    }

    @Override
    public String toString() {
        return name;
    }
}
