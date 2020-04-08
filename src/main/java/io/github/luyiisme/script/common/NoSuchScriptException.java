package io.github.luyiisme.script.common;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-08 10:25
 **/
public class NoSuchScriptException extends RuntimeException {
    public NoSuchScriptException(String scriptName) {
        super("no such script:" + scriptName + " found!");
    }
}
