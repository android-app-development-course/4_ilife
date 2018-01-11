package com.example.administrator.ilife_manager;

/**
 * Created by Administrator on 2017/12/20/020.
 */

import java.util.Stack;

/**
 * Created by Administrator on 2017/10/12/012.
 */

public class Calculate {
    public static String calculate(String[] s)
    {
        Stack<String> stack=new Stack<String>();
        for(int i=0;!s[i].equals("end");i++)
        {
            if(!s[i].equals("+")&&!s[i].equals("-")&&!s[i].equals("*")&&!s[i].equals("/"))
            {
                String str=s[i];
                stack.push(str);
            }
            else
            {
                double a=Double.parseDouble(stack.pop());
                double b=Double.parseDouble(stack.pop());
                if(s[i].equals("+"))
                    b=b+a;
                else if(s[i].equals("-"))
                    b=b-a;
                else if(s[i].equals("*"))
                    b=b*a;
                else if(s[i].equals("/"))
                    b=b/a;
                stack.push(""+b);
            }
        }
        String result=stack.pop();
        return result;
    }
}