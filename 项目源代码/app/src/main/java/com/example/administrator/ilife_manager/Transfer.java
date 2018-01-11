package com.example.administrator.ilife_manager;

/**
 * Created by Administrator on 2017/12/20/020.
 */

import java.util.Stack;

/**
 * Created by Administrator on 2017/10/12/012.
 */

public class Transfer {

    static String[] toArray(String s) {
        String[] midExpression = new String[20];
        int index = -1;
        char lastChar = ' ';
        for (int i = 0; i < s.length(); i++) {
            if (isDigit(s.charAt(i)) && isDigit(lastChar))// 扫描的字符和上个字符构成一个数字
                midExpression[index] += s.charAt(i);
            else// 扫描的字符是操作符或是新的数字
            {
                index++;
                midExpression[index] = "" + s.charAt(i);
            }
            lastChar=s.charAt(i);
        }
        index++;
        midExpression[index]="end";
        return midExpression;
    }

    static boolean isDigit(char d)
    {
        if((d>=48&&d<=57)||d==46)
            return true;
        else return false;
    }

    static String[] transfer(String[] s) {
        Stack<String> stack = new Stack<String>();
        String[] afterExpression = new String[20];
        int afterIndex = 0;
        for (int i = 0; !s[i].equals("end"); i++) {
            if (s[i].equals("+") || s[i].equals("-") || s[i].equals("*") || s[i].equals("/") || s[i].equals("("))// 扫描项为除")"以外的操作符
            {
                while (!stack.isEmpty() && !priorTo(s[i], stack.peek()))// 栈内不空且栈外操作符优先级不高于栈顶操作符
                    afterExpression[afterIndex++] = stack.pop();
                stack.push(s[i]);
            } else if (s[i].equals(")")) {
                while (!stack.peek().equals("("))
                    afterExpression[afterIndex++] =stack.pop();
                stack.pop();
            } else
                afterExpression[afterIndex++] = s[i];
        }
        while (!stack.isEmpty())
            afterExpression[afterIndex++] = stack.pop();
        afterExpression[afterIndex] = "end";
        return afterExpression;
    }

    static boolean priorTo(String a,String b)//a为栈外操作符，b为栈内操作符
    {
        if(a.equals("(")||b.equals("("))
            return true;
        if(a.equals("*")||a.equals("/"))
        {
            if (b.equals("+")||b.equals("-"))
                return true;
        }
        return false;
    }
}