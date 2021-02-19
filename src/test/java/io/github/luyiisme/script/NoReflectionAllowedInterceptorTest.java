package io.github.luyiisme.script;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import io.github.luyiisme.script.groovy.GroovyScriptEngine;
import io.github.luyiisme.script.management.ScriptManager;
import io.github.luyiisme.script.sandbox.interceptor.NoReflectionAllowedInterceptor;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-02-17 16:16
 **/
public class NoReflectionAllowedInterceptorTest {

    static ScriptEngine scriptEngine = new GroovyScriptEngine();

    @BeforeClass
    public static void init() {
        scriptEngine.addGroovyInterceptor(new NoReflectionAllowedInterceptor());

    }

    @Test(expected = SecurityException.class)
    public void testClassLoaderCase() {
        ((ScriptManager)scriptEngine).registerScript("123", "Class.forName(\"java.lang.String\");");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("123", params);
    }

    @Test(expected = SecurityException.class)
    public void testReflectionCase() {
        Array.newInstance(String.class, 2);
        ((ScriptManager)scriptEngine).registerScript("456",
            "java.lang.reflect.Array.newInstance(java.lang.String.class,2);");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("456", params);
    }

    @Test(expected = MultipleCompilationErrorsException.class)
    public void testRejectAST_Transform_Case() throws Throwable {
        try {


        ((ScriptManager)scriptEngine).registerScript("456",
            "import groovy.transform.* \n @ASTTest(value = { \n def owner = node.declaringClass \n assert owner"
                + ".fields.any { it.name == 'x' } \n }) \n @Field int x "
                + " ");
        }catch (Throwable e){
            Assert.assertTrue(e.getMessage().contains("Insecure annotation"));
            throw e;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("456", params);
    }
}
