package io.github.luyiisme.script.groovy;

import java.util.Map;

import io.github.luyiisme.script.ScriptEngine;
import io.github.luyiisme.script.api.ScriptInvokeInterceptor;
import io.github.luyiisme.script.management.ScriptManager;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

/**
 * 支持(1.注册，2.解除注册)操作的同步化的脚本引擎.
 * 适用于脚本具备自动更新能力的场景(防止调用的时候，另外个线程先移除，再注册新脚本过程出错);
 *
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-08 13:51
 **/
public class SynchronizedGroovyScriptEngine implements ScriptEngine, ScriptManager {
    private GroovyScriptEngine groovyScriptEngine;

    public SynchronizedGroovyScriptEngine(GroovyScriptEngine groovyScriptEngine) {
        this.groovyScriptEngine = groovyScriptEngine;
    }

    @Override
    public synchronized <T> T invoke(String scriptName, Map<String, Object> scriptParams) {
        return groovyScriptEngine.invoke(scriptName, scriptParams);
    }

    /**
     * 更新操作原子化支持
     *
     * @param scriptId
     * @param scriptText
     */
    public synchronized void replaceScript(String scriptId, String scriptText) {
        groovyScriptEngine.deRegisterScript(scriptId);
        groovyScriptEngine.registerScript(scriptId, scriptText);
    }

    @Override
    public synchronized void registerScript(String scriptId, String scriptText) {
        groovyScriptEngine.registerScript(scriptId, scriptText);
    }

    @Override
    public synchronized void deRegisterScript(String scriptId) {
        groovyScriptEngine.deRegisterScript(scriptId);
    }

    @Override
    public synchronized void removeAllScript() {
        groovyScriptEngine.removeAllScript();
    }

    //===>
    @Override
    public void setCompilerConfiguration(CompilerConfiguration cc) {
        groovyScriptEngine.setCompilerConfiguration(cc);
    }

    @Override
    public void addGroovyInterceptor(GroovyInterceptor groovyInterceptor) {
        groovyScriptEngine.addGroovyInterceptor(groovyInterceptor);
    }

    @Override
    public void addScriptInvokeInterceptor(ScriptInvokeInterceptor scriptInvokeInterceptor) {
        groovyScriptEngine.addScriptInvokeInterceptor(scriptInvokeInterceptor);
    }

    @Override
    public String getScriptState() {
        return groovyScriptEngine.getScriptState();
    }

    @Override
    public boolean isEmpty() {
        return groovyScriptEngine.isEmpty();
    }
}
