package Assembly.Inst;

public class Jump extends Inst{
    public String dest;

    public Jump(String dest){
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "j\t" + dest;
    }
}
