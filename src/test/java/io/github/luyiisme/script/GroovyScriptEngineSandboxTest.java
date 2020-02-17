package io.github.luyiisme.script;

import java.util.HashMap;
import java.util.Map;

import io.github.luyiisme.script.groovy.GroovyScriptEngine;
import io.github.luyiisme.script.management.ScriptManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-02-17 14:31
 **/
public class GroovyScriptEngineSandboxTest {

    private ClassRecorder cr = new ClassRecorder();

    @Test
    public void testSandboxUsage() {
        //PHASE 1:脚本引擎初始化线程执行
        ScriptEngine scriptEngine = new GroovyScriptEngine();
        scriptEngine.addGroovyInterceptor(cr);

        //PHASE 2:脚本管理线程执行
        /** "123" 脚本Id;
         * 脚本内容里: context 表示脚本上下文对象; 可以在执行时传入参数map,比如后面,scriptEngine.invoke("123", params)
         * context.name 执行结果: jack
         **/
        ((ScriptManager)scriptEngine).registerScript("123", "println( context.name ); return 1;");

        //PHASE 3:脚本执行线程,实际业务执行调用
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("123", params);
        Assert.assertSame(1, v);
        System.out.println(cr.toString());
        Assert.assertTrue(cr.toString().contains("DefaultInvokeContext.name"));

        //PHASE 4:删除所有的脚本
        ((ScriptManager)scriptEngine).removeAllScript();
    }


}
