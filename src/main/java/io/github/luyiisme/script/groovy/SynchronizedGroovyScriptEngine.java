package io.github.luyiisme.script.groovy;

/**
 * 支持(1.注册，2.解除注册)操作的同步化的脚本引擎
 *
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-08 13:51
 **/
public class SynchronizedGroovyScriptEngine extends GroovyScriptEngine {
    @Override
    public synchronized void registerScript(String scriptId, String scriptText) {
        super.registerScript(scriptId, scriptText);
    }

    @Override
    public synchronized void deRegisterScript(String scriptId) {
        super.deRegisterScript(scriptId);
    }

    @Override
    public synchronized void removeAllScript() {
        super.removeAllScript();
    }
}
