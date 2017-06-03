import java.io.*;

/**
 * 词法分析器
 */

public class LexicalAnalysis {

    private PrintWriter writer;
    private PrintWriter errWriter;
    public static String FILE = "F://A/a.dyd";
    public static String ERRFILE = "F://A/a.LexicalAnalysis.err";
    public void analysis(String s, int line) {
        int state = 0;
        int first = 0;
        int last = 0;
        s = s + " ";
        for (int i = 0; i < s.length(); i++) {
            //循环
            char c = s.charAt(i);

            switch (state) {
                case 0: {
                    if (c == ' '||c=='\t') {
                        state = 0;
                        break;
                    }
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        state = 1;
                        first = i;
                        break;
                    }
                    if (c >= '0' && c <= '9') {
                        state = 3;
                        first = i;
                        break;
                    }
                    if (c == '=') {
                        state = 5;
                        printToFile("=", Compiler.symbolMap.get("="));
                        break;
                    }
                    if (c == '-') {
                        state = 6;
                        printToFile("-", Compiler.symbolMap.get("-"));
                        state = 0;
                        break;
                    }
                    if (c == '*') {
                        state = 7;
                        printToFile("*",Compiler.symbolMap.get("*"));
                        state = 0;
                        break;
                    }
                    if (c == '(') {
                        state = 8;
                        printToFile("(",Compiler.symbolMap.get("("));
                        state = 0;
                        break;
                    }
                    if (c == ')') {
                        state = 9;
                        printToFile(")",Compiler.symbolMap.get(")"));
                        state = 0;
                        break;
                    }
                    if (c == '<') {
                        state = 10;
                        break;
                    }
                    if (c == '>') {
                        state = 14;
                        break;
                    }
                    if (c == ':') {
                        state = 17;
                        break;
                    }
                    if (c == ';') {
                        state = 20;
                        printToFile(";",Compiler.symbolMap.get(";"));
                        state = 0;
                        break;
                    }

                    state = 21;
                    printErrToFile(line,i);
                    state=0;
                    return;
                }

                case 1:
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                        state = 1;
                        break;
                    } else {
                        state = 2;
                        last = i;
                        analise(s, first, last);
                        i--;
                        state = 0;
                        break;
                    }
                case 3:
                    if (c >= '0' && c <= '9') {
                        state = 3;
                        break;
                    } else {
                        state = 4;
                        last = i;
                        printToFile(s.substring(first,last),Compiler.symbolMap.get("const"));
                        i--;
                        state = 0;
                        break;
                    }
                case 10:
                    if (c == '=') {
                        state = 11;
                        printToFile("<=",Compiler.symbolMap.get("<="));
                        state = 0;
                        break;
                    }
                    if (c == '>') {
                        state = 12;
                        printToFile("<>",Compiler.symbolMap.get("<>"));
                        state = 0;
                        break;
                    }
                {
                    state = 13;
                    printToFile("<",Compiler.symbolMap.get("<"));
                    i--;
                    state = 0;
                    break;
                }

                case 14:
                    if (c == '=') {
                        state = 15;
                        printToFile(">=",Compiler.symbolMap.get(">="));
                        state = 0;
                        break;
                    }
                    state = 16;
                    printToFile(">",Compiler.symbolMap.get(">"));
                    i--;
                    state = 0;
                    break;
                case 17:
                    if (c == '=') {
                        state = 18;
                        printToFile(":=",Compiler.symbolMap.get(":="));
                        state = 0;
                        break;
                    }
                    state = 19;
                    printErrToFile(line, i);
                    i--;
                    state = 0;
                    return;
                default:
                    break;
            }


        }
        printToFile("EOLN",24);

    }

   private void analise(String s, int first, int last){
        String ss=s.substring(first,last);
        if(ss.equals("begin")){
            printToFile(ss,Compiler.symbolMap.get("begin"));
        }else if(ss.equals("end")){
            printToFile(ss,Compiler.symbolMap.get("end"));
        }else if(ss.equals("integer")){
            printToFile(ss,Compiler.symbolMap.get("integer"));
        }else if(ss.equals("if")){
            printToFile(ss,Compiler.symbolMap.get("if"));
        }else if(ss.equals("then")){
            printToFile(ss,Compiler.symbolMap.get("then"));
        }else if(ss.equals("else")){
            printToFile(ss,Compiler.symbolMap.get("else"));
        }else if(ss.equals("function")){
            printToFile(ss,Compiler.symbolMap.get("function"));
        }else if(ss.equals("read")){
            printToFile(ss,Compiler.symbolMap.get("read"));
        }else if(ss.equals("write")){
            printToFile(ss,Compiler.symbolMap.get("write"));
        }else{
            printToFile(ss,Compiler.symbolMap.get("symbol"));
        }

    }


    public LexicalAnalysis() {
        File file = new File(FILE);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer = new PrintWriter(new FileWriter(new File(FILE), true));
            errWriter=new PrintWriter(new FileWriter(new File(ERRFILE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printToFile(String s, int i) {
        writer.printf("%16s %d\n", s, i);
        writer.flush();
    }
    private void printErrToFile(int line, int i) {
        errWriter.printf("***LINE:%d  %d\n", line, i);
        errWriter.flush();
    }

    public void printfEND(){
        printToFile("EOF",25);
    }

}
