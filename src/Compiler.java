import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by qianzise on 2017/4/27 0027.
 */
public class Compiler {
    public static String fileName="F://A/a.txt";
    private LexicalAnalysis analysis=null;

    public static Map<String,Integer> symbolMap=new HashMap<>();

    static {
        symbolMap.put("begin",1);
        symbolMap.put("end",2);
        symbolMap.put("integer",3);
        symbolMap.put("if",4);
        symbolMap.put("then",5);
        symbolMap.put("else",6);
        symbolMap.put("function",7);
        symbolMap.put("read",8);
        symbolMap.put("write",9);
        symbolMap.put("symbol",10);
        symbolMap.put("const",11);
        symbolMap.put("=",12);
        symbolMap.put("<>",13);
        symbolMap.put("<=",14);
        symbolMap.put("<",15);
        symbolMap.put(">=",16);
        symbolMap.put(">",17);
        symbolMap.put("-",18);
        symbolMap.put("*",19);
        symbolMap.put(":=",20);
        symbolMap.put("(",21);
        symbolMap.put(")",22);
        symbolMap.put(";",23);
        symbolMap.put("EOLN",24);
        symbolMap.put("EOF",25);

    }

    public void openFile(String fileName){
        File file=new File(fileName);
        if(file.exists()){
            BufferedReader reader=null;
            try {
                reader=new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line=null;
            int i=0;
            try {
                line=reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (line!=null){
                analysis.analysis(line,i);
                try {
                    line=reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }




        }

    }

    public Compiler(LexicalAnalysis analysis){
        this.analysis=analysis;
    }

    public  static  void main(String args[]){
        LexicalAnalysis analysis=new LexicalAnalysis();
        SyntaxAnalyzer syntaxAnalyzer=new SyntaxAnalyzer();
        Compiler compiler=new Compiler(analysis);
        compiler.openFile(fileName);

        syntaxAnalyzer.openLexFile();


    }
}
