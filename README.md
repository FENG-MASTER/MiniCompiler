# 迷你编译器(词法+语法)

求n！的极小语言

## 语言文法如下:
1. <程序>→<分程序>
2. <分程序>→begin <说明语句表>；<执行语句表> end
3. <说明语句表>→<说明语句>│<说明语句表> ；<说明语句>
4. <说明语句>→<变量说明>│<函数说明>
5. <变量说明>→integer <变量>
6. <变量>→<标识符>
7. <标识符>→<字母>│<标识符><字母>│ <标识符><数字>
8. <字母>→a│b│c│d│e│f│g│h│i│j│k│l│m│n│o │p│q │r│s│t│u│v│w│x│y│z
9. <数字>→0│1│2│3│4│5│6│7│8│9
10. <函数说明>→integer function <标识符>（<参数>）；<函数体>
11. <参数>→<变量>
12. <函数体>→begin <说明语句表>；<执行语句表> end
13. <执行语句表>→<执行语句>│<执行语句表>；<执行语句>
14. <执行语句>→<读语句>│<写语句>│<赋值语句>│<条件语句>
15. <读语句>→read(<变量>)
16. <写语句>→write(<变量>)
17. <赋值语句>→<变量>:=<算术表达式>
18. <算术表达式>→<算术表达式>-<项>│<项>
19. <项>→<项>*<因子>│<因子>
20. <因子>→<变量>│<常数>│<函数调用>
21. <常数>→<无符号整数>
22. <无符号整数>→<数字>│<无符号整数><数字>
23. <条件语句>→if<条件表达式>then<执行语句>else <执行语句>
24. <条件表达式>→<算术表达式><关系运算符><算术表达式>
25. <关系运算符> →<│<=│>│>=│=│<>


## 实现内容

完成了词法分析,语法分析的全部内容
具体包括了:
1.	词法分析部分:

    a)	关键字的识别
    b)	标识符的识别
    c)	运算符号的识别
    d)	界符的识别
    e)	文件结束符识别
    f)	行结束符识别
    
2.	语法分析部分:

    a)	缺少相应符号错误
    b)	符号不匹配错误
    c)	变量未定义错误
    d)	变量重复定义错误
    e)	函数未定义错误
    f)	函数重复定义错误
    g)	函数和变量混淆错误
    
err文件输出语法和词法分析的错误信息

pro文件输出函数表

var文件输出变量表
    
# 求n！的极小语言源程序

    begin
      integer k;
      integer function F(n);
        begin
          integer m;
          if n<= 0 then F:=1
          else F:=n*F(n-1)
        end;
      read(k);
      k:=F(k);
      write(k)
    end


