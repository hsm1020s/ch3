package com.fastcampus.ch3.aop;

import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class aopMain {
    public static void main(String[] args) throws Exception{
        MyAdvice myAdvice = new MyAdvice();
        Class myClass = Class.forName("com.fastcampus.ch3.aop.MyClass");
        Object obj = myClass.newInstance();

        for(Method m : myClass.getDeclaredMethods()){
            myAdvice.invoke(m,obj,null);
        }

    }
}

class MyAdvice{
    Pattern p = Pattern.compile("a.*"); //a로 시작하는 단어

    boolean matches(Method m){
        Matcher matcher = p.matcher(m.getName());
        return matcher.matches();
    }
    void invoke(Method m,Object obj, Object... args)throws Exception{
        if(matches(m))
        System.out.println("[before]{");

        m.invoke(obj,args);

        if(matches(m))
        System.out.println("][after]{");
    }
}
class MyClass{
    // @Transactional
    void aaa(){
        System.out.println("aaa() is called.");
    }
    void aaa2(){
        System.out.println("aaa2() is called.");
    }
    void bbb(){
        System.out.println("bbb() is called.");
    }
}