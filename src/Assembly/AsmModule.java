package Assembly;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;

public class AsmModule {
    public ArrayList<Pair<String , AsmFunc>> text = new ArrayList<>();
    public ArrayList<Pair<String , String>> rodata = new ArrayList<>();
    public ArrayList<String> bss = new ArrayList<>();
}
