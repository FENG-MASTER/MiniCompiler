import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * 语法分析器
 */
public class SyntaxAnalyzer {
    private BufferedReader reader = null;

    private List<TwoUnit> fileLines=new ArrayList<>();

    public static String FILE = "F://A/a.dys";
    public static String ERRFILE = "F://A/SyntaxAnalyzer.err";
    public static String VARFILE="F://A/a.var";
    public static String PROFILE="F://A/a.pro";

    private PrintWriter errWriter;
    private FileOutputStream out;
    private boolean errflag=false;

    private Stack<Func> funcStack=new Stack<>();//函数栈,用于函数嵌套的时候,栈顶就是当前所在函数名.栈大小-1即为层数

    private List<Var> varDefList=new ArrayList<>();
    private List<Func> funcDefList=new ArrayList<>();

    private PrintWriter varWriter;
    private PrintWriter prowriter;

    private int varadr=0;


    private TwoUnit token;
    private int len=1;//当前检测的行(dyd文件中的行数)
    private int line=1;//当前检测行数(报错用的准确行数)

    private void openLexFile() {
        File file = new File(LexicalAnalysis.FILE);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void start(){
        init();
        getNextTaken();
        program2SubProgram();

        if (!errflag){
            saveVar2File();
            savePro2File();
            saveToFile();
        }
    }


    public void init(){
        openLexFile();

        try {
            errWriter=new PrintWriter(new FileWriter(new File(ERRFILE)));
            out=new FileOutputStream(new File(FILE));
            varWriter=new PrintWriter(new FileWriter(new File(VARFILE)));
            prowriter=new PrintWriter(new FileWriter(new File(PROFILE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     * 获取下一个taken
     */
    private void getNextTaken() {

        if (len<=fileLines.size()){
            token=fileLines.get(len-1);
            len++;
        }else {
            readFromFile();
            getNextTaken();
        }

        if (checkFor("EOLN",false,false)){
            getNextTaken();
        }

    }

    /**
     * <程序>→<分程序>
     */
    private void program2SubProgram() {
        funcStack.push(new Func("main","void",0));
        subProgram();
    }

    /**
     * <分程序>→begin <说明语句表>；<执行语句表> end
     */
    private void subProgram() {
        if(checkFor("begin")){
            declarativeStatementTable();
            if(checkFor(";")){
                exeStatementTable();
                if(checkFor("end")){

                }
            }
        }

    }

    /**
     * <说明语句表>→<说明语句>│<说明语句表> ；<说明语句>
     */
    private void declarativeStatementTable() {
        //这里消除下左递归
        declarativeStatement();
        funcA();
    }

    /**
     * 用于前面消除左递归
     */
    private void funcA(){
        if(checkFor(";",false,true)){
            if (checkFor("integer",false,false)){
                declarativeStatement();
                funcA();
            }else {
                back();
            }
        }
    }


    /**
     * <说明语句>→<变量说明>│<函数说明>
     */
    private void declarativeStatement() {
        if (checkFor("integer")) {
            if (checkFor("function",false,false)){
                functionalDeclaration();
            }else {
                variableDeclaration();
            }
        }
    }

    /**
     * <变量说明>→integer <变量>
     */
    private void variableDeclaration() {

            var(true);

    }

    /**
     * <变量>→<标识符>
     */
    private void var() {
        var(false);
    }

    private void var(boolean dec) {
        if (dec){
            if (checkFor("symbol", true, true, s -> saveVar(s,funcStack.peek().name,0,"ints",funcStack.size()-1))){

            }
        }else {
            if (checkFor("symbol", true, true, s -> {
                if (!checkVarDef(s)){
                    err("变量"+s+"未定义");
                }
            })){

            }
        }

    }

    /**
     * <函数说明>→integer function <标识符>（<参数>）；<函数体>
     */
    private void functionalDeclaration() {

            if (checkFor("function")){
                if (checkFor("symbol", true, true, s -> funcStack.push(new Func(s,"ints",funcStack.size())))){
                    if (checkFor("(")){
                        funcStack.peek().fadr=varadr;
                        parm();
                        funcStack.peek().ladr=varadr;
                        if (checkFor(")")){
                            if (checkFor(";")){
                                savePro(funcStack.peek());
                                function();
                                funcStack.pop();
                            }
                        }
                    }
                }
            }

    }

    /**
     * <参数>→<变量>
     */
    private void parm() {
        if (checkFor("symbol", true, true, s -> saveVar(s,funcStack.peek().name,1,"ints",funcStack.size()-1))){

        }
    }

    /**
     * <函数体>→begin <说明语句表>；<执行语句表> end
     */
    private void function() {
        if (checkFor("begin")){
            declarativeStatementTable();
            if (checkFor(";")){
                exeStatementTable();
                if (checkFor("end")){

                }
            }
        }
    }

    /**
     * <执行语句表>→<执行语句>│<执行语句表>；<执行语句>
     */
    private void exeStatementTable() {
        exeStatement();
        funcD();
    }

    private void funcD(){
        if (checkFor(";",false,true)){
            exeStatement();
            funcD();
        }
    }

    /**
     * <执行语句>→<读语句>│<写语句>│<赋值语句>│<条件语句>
     */
    private void exeStatement() {
        if (checkFor("read",false,false)){
            read();
        }else if(checkFor("write",false,false)){
            write();
        }else if (checkFor("if",false,false)){
            conditionalStatement();
        }else {
            assignmentStatement();
        }
    }

    /**
     * <读语句>→read(<变量>)
     */
    private void read() {
        if (checkFor("read")){
            if (checkFor("(")){
                var();
                if (checkFor(")")){

                }
            }
        }

    }

    /**
     * <写语句>→write(<变量>)
     */
    private void write() {
        if (checkFor("write")){
            if (checkFor("(")){
                var();
                if (checkFor(")")){

                }
            }
        }
    }


    /**
     * <赋值语句>→<变量>:=<算术表达式>
     */
    private void assignmentStatement() {
        if (checkFor("symbol")){
            if (checkFor(":=")){
                arithmeticExpression();
            }
        }
    }

    /**
     * <算术表达式>→<算术表达式>-<项>│<项>
     */
    private void arithmeticExpression() {
        term();
        funcB();
    }

    private void funcB(){
        if (checkFor("-",false,true)){
            term();
            funcB();
        }
    }

    /**
     * <项>→<项>*<因子>│<因子>
     */
    private void term() {
        factor();
        funcC();
    }

    private void funcC(){
        if (checkFor("*",false,true)){
            term();
            funcC();
        }
    }

    /**
     * <因子>→<变量>│<常数>│<函数调用>
     */
    private void factor() {
        final boolean[] funcFlag = {false};
        final boolean[] varFlag = {false};

        if (checkFor("const",false,true)){

        }else if (checkFor("symbol", false, true, new Consumer<String>() {
            @Override
            public void accept(String s) {
                varFlag[0] =checkVarDef(s);
                funcFlag[0] =checkProDef(s);
                if (!varFlag[0]&&!funcFlag[0]){
                    err("符号"+s+"未定义");
                }
            }
        })){

            if (checkFor("(",false,false)){
                if (!funcFlag[0]&&varFlag[0]){
                    err("变量无法当作函数调用");
                }else {
                    functionCall();
                }
            }


        }else {
            err("缺少因子");
        }


    }

    private void functionCall(){
        if(checkFor("(")){
            arithmeticExpression();
            if (checkFor(")")){

            }
        }
    }

    /**
     * <常数>→<无符号整数>
     */
    private void constant() {
        unsignedInteger();
    }

    /**
     * <无符号整数>→<数字>│<无符号整数><数字>
     */
    private void unsignedInteger() {
        if (checkFor("const")){

        }
    }

    /**
     * <条件语句>→if<条件表达式>then<执行语句>else <执行语句>
     */
    private void conditionalStatement() {
        if (checkFor("if")){
            conditionExpression();
            if (checkFor("then")){
                exeStatement();
                if (checkFor("else")){
                    exeStatement();
                }
            }
        }
    }

    /**
     * <条件表达式>→<算术表达式><关系运算符><算术表达式>
     */
    private void conditionExpression() {
        arithmeticExpression();
        relationalOperator();
        arithmeticExpression();
    }

    /**
     * <关系运算符> →<│<=│>│>=│=│<>
     */
    private void relationalOperator(){
        if (checkFor("<",false,false)||checkFor("<=")||checkFor(">")||checkFor(">=")||
                checkFor("=")||checkFor("<>")){

        }else {
            err("缺少关系运算符");
        }
    }

    private void err(String s){
        try {
            s=new String(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        printErrToFile(getLine(),s);
        errflag=true;
        System.out.printf(s+"\n");
    }

    private boolean checkFor(String s, boolean errFlag, boolean nextFlag,@Nullable Consumer<String> c){
        if(token.num==Compiler.symbolMap.get(s)){
            if (c!=null){
                c.accept(token.name);
            }
            if(nextFlag){
                getNextTaken();
            }
            return true;
        }else {
            if(errFlag){
                err("行数:"+getLine()+"缺少"+s);
            }
            return false;
        }

    }
    private boolean checkFor(String s, boolean errFlag, boolean nextFlag){
        return checkFor(s,errFlag,nextFlag,null);
    }

    private boolean checkFor(String s){
       return checkFor(s,true,true);
    }

    private void back(){
        len--;
        len--;
        len--;
        getNextTaken();
        if(token.num==Compiler.symbolMap.get("EOLN")){
            back();
        }
    }

    /**
     * 从文件里读取token
     */
    private void readFromFile(){
        try {
            String s = reader.readLine();
            fileLines.add(new TwoUnit(s.substring(0,17),Integer.parseInt(s.substring(17))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getLine(){
        int l=0;
        for (int i=0;i<len-1;i++){
            if (fileLines.get(i).num==Compiler.symbolMap.get("EOLN")){
                l++;
            }
        }
        return l;
    }

    private void printErrToFile(int line, String s) {
        errWriter.printf("***LINE:%d  %s\n", line, s);
        errWriter.flush();
    }


    private void saveToFile(){
        byte[] buff=new byte[1024];
        try {
            FileInputStream inputStream=new FileInputStream(new File(LexicalAnalysis.FILE));
            int len=0;
            while ((len=inputStream.read(buff))>0){
                out.write(buff,0,len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveVar(String name,String pro,int kind,String type,int lev){
        varDefList.add(new Var(name,pro,kind,type,lev));
    }

    private boolean checkVarDef(String name){
        boolean flag=false;
        for (Var v:varDefList){
            if (v.name.equals(name)){
                flag=true;
                break;
            }
        }

        return flag;
    }

    private boolean checkProDef(String name){

        boolean flag=false;
        for (Func f:funcDefList){
            if (f.name.equals(name)){
                flag=true;
                break;
            }
        }

        return flag;
    }


    private void savePro(Func func){
        funcDefList.add(func);

    }


    private void saveVar2File(){
        for (Var v: varDefList) {
            varWriter.printf("%16s%16s%16d%16s%16d%16d\n",v.name,v.pro,v.kind,v.type,v.lev,varadr++);
            varWriter.flush();
        }
    }

    private void savePro2File(){
        for(Func f:funcDefList){
            prowriter.printf("%16s%16s%16d%16d%16d\n",f.name,f.type,f.lev,f.fadr,f.ladr);
            prowriter.flush();
        }
    }


}
