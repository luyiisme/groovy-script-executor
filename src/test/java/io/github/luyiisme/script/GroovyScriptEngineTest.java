package io.github.luyiisme.script;

import io.github.luyiisme.script.groovy.GroovyScriptEngine;
import io.github.luyiisme.script.management.ScriptManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luyi on 16/4/19.
 */
public class GroovyScriptEngineTest {

    @Test
    public void testUsage() {
        ScriptEngine scriptEngine = new GroovyScriptEngine();

        //推送时,登记脚本.
        /** "123" ruleId;
         *脚本内容里: context 表示脚本上下文对象; 可以在执行时传入参数map,比如后面,scriptEngine.invoke("123", params)
         *
         * context.name 执行结果: jack
         **/
        ((ScriptManager) scriptEngine).registerScript("123", "println( context.name ); return 1;");

        //调用
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Assert.assertSame(1, scriptEngine.invoke("123", params));

        //删除所有的脚本
        ((ScriptManager) scriptEngine).removeAllScript();
    }

    /**
     * Groovy 的 context 是 ThreadLocal, 所以是线程安全的。
     * @throws InterruptedException
     */
    @Test
    public void testConcurrentSetContext() throws InterruptedException {
        final GroovyScriptEngine scriptEngine = new GroovyScriptEngine();

        final int counter = 100;

        int threadNum = 10;
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            final int integer = i;
            scriptEngine.registerScript(String.valueOf(i), "return (context.num) ");
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < counter; i++) {
                        // do real work here
                        Map map = new HashMap();
                        map.put("num", integer);

                        Object result = scriptEngine.invoke(String.valueOf(integer), map);
                        assert result.equals(integer);
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    //    @Test
    //    public void testLoad() {
    //        ScriptEngine scriptEngine = new GroovyScriptEngine();
    //
    //      //   for (int i=0;i<9;i++) {
    //
    //         for (; ; ) {
    //            ((ScriptManager) scriptEngine).registerScript("123", "println( context.name ); return 1;");
    //            //调用
    //            Map<String, Object> params = new HashMap<String, Object>();
    //            params.put("name", "jack");
    //            scriptEngine.invoke("123", params);
    //        }
    //    }

}
