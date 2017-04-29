import java.io.*;

/**
 * Created by qianzise on 2017/4/27 0027.
 */

public class LexicalAnalysis {

    private PrintWriter writer;
    private PrintWriter errWriter;
    public static String FILE = "F://A/a.dyd";
    public static String ERRFILE = "F://A/a.err";
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
                    if (c == ' ') {
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
                        printToFile("=", 12);
                        break;
                    }
                    if (c == '-') {
                        state = 6;
                        printToFile("-", 18);
                        state = 0;
                        break;
                    }
                    if (c == '*') {
                        state = 7;
                        printToFile("*",19);
                        state = 0;
                        break;
                    }
                    if (c == '(') {
                        state = 8;
                        printToFile("(",21);
                        state = 0;
                        break;
                    }
                    if (c == ')') {
                        state = 9;
                        printToFile(")",22);
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
                        printToFile(";",23);
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
                        printToFile(s.substring(first,last),11);
                        i--;
                        state = 0;
                        break;
                    }
                case 10:
                    if (c == '=') {
                        state = 11;
                        printToFile("<=",14);
                        state = 0;
                        break;
                    }
                    if (c == '>') {
                        state = 12;
                        printToFile("<>",13);
                        state = 0;
                        break;
                    }
                {
                    state = 13;
                    printToFile("<",15);
                    i--;
                    state = 0;
                    break;
                }

                case 14:
                    if (c == '=') {
                        state = 15;
                        printToFile(">=",16);
                        state = 0;
                        break;
                    }
                    state = 16;
                    printToFile(">",17);
                    i--;
                    state = 0;
                    break;
                case 17:
                    if (c == '=') {
                        state = 18;
                        printToFile(":=",20);
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
            printToFile(ss,1);
        }else if(ss.equals("end")){
            printToFile(ss,2);
        }else if(ss.equals("integer")){
            printToFile(ss,3);
        }else if(ss.equals("if")){
            printToFile(ss,4);
        }else if(ss.equals("then")){
            printToFile(ss,5);
        }else if(ss.equals("else")){
            printToFile(ss,6);
        }else if(ss.equals("function")){
            printToFile(ss,7);
        }else if(ss.equals("read")){
            printToFile(ss,8);
        }else if(ss.equals("write")){
            printToFile(ss,9);
        }else{
            printToFile(ss,10);
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

}
