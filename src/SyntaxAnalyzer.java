import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器
 */
public class SyntaxAnalyzer {
    private BufferedReader reader = null;

    private List<Integer> fileLines=new ArrayList<>();

    private int token;
    private int len=1;//当前检测的行(dyd文件中的行数)
    private int line=1;//当前检测行数(报错用的准确行数)

    public void openLexFile() {
        File file = new File(LexicalAnalysis.FILE);
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        getNextTaken();
        program2SubProgram();
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

            var();

    }

    /**
     * <变量>→<标识符>
     */
    private void var() {
        if (checkFor("symbol")){

        }
    }

    /**
     * <函数说明>→integer function <标识符>（<参数>）；<函数体>
     */
    private void functionalDeclaration() {

            if (checkFor("function")){
                if (checkFor("symbol")){
                    if (checkFor("(")){
                        var();
                        if (checkFor(")")){
                            if (checkFor(";")){
                                function();
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
        var();
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
        if (checkFor("symbol",false,true)||checkFor("const",false,true)){
            if (checkFor("(",false,false)){
                functionCall();
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
        System.out.printf(s+"\n");
    }

    private boolean checkFor(String s,boolean errFlag,boolean nextFlag){
        if(token==Compiler.symbolMap.get(s)){
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

    private boolean checkFor(String s){
       return checkFor(s,true,true);
    }

    private void back(){
        len--;
        len--;
        len--;
        getNextTaken();
        if(token==Compiler.symbolMap.get("EOLN")){
            back();
        }
    }

    /**
     * 从文件里读取token
     */
    private void readFromFile(){
        try {
            String s = reader.readLine();
            fileLines.add(Integer.parseInt(s.substring(17)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getLine(){
        int l=0;
        for (int i=0;i<len-1;i++){
            if (fileLines.get(i)==Compiler.symbolMap.get("EOLN")){
                l++;
            }
        }
        return l;
    }

}
