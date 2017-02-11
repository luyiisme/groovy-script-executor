package io.github.luyiisme.script.groovy;

import io.github.luyiisme.script.ScriptEngine;
import io.github.luyiisme.script.management.DefaultScriptManager;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

/**
 * 客户端的脚本前提条件:
 * <p>
 * - 一个规则只有一个脚本;
 * - 每个规则的ID是唯一的;
 * <p>
 * scriptId 就必须使用 ruleId;
 *
 * @author luyi on 16/4/19.
 */
public class GroovyScriptEngine extends DefaultScriptManager implements ScriptEngine {

    private final DefaultInvokeContext invokeContext = new DefaultInvokeContext();
    public static final String         CONTEXT_KEY   = "context";
    private final Binding              binding;

    public GroovyScriptEngine() {
        binding = new Binding();
        binding.setVariable(CONTEXT_KEY, invokeContext);
    }

    @Override
    protected Script generateScriptInstance(String scriptName, String scriptText) {
        GroovyClassLoader scriptClassLoader = AccessController
            .doPrivileged(new PrivilegedAction<GroovyClassLoader>() {
                @Override
                public GroovyClassLoader run() {
                    return new GroovyClassLoader(getClass().getClassLoader());
                }
            });

        Class<?> clazz = scriptClassLoader.parseClass(scriptText);
        try {
            Script script = null;
            if (Script.class.isAssignableFrom(clazz)) {
                try {
                    Constructor constructor = clazz.getConstructor(Binding.class);
                    script = (Script) constructor.newInstance(binding);
                } catch (Throwable e) {
                    // Fallback for non-standard "Script" classes.
                    script = (Script) clazz.newInstance();
                    script.setBinding(binding);
                }
            } else {
                //不是脚本,return null;
                logger.warn("script:" + scriptText + " is not a script!!");
            }

            return script;
        } catch (Throwable e) {
            throw new RuntimeException(
                "Failed to Generate scriptInstance! scriptText" + scriptText, e);
        }
    }

    @Override
    public <T> T invoke(String scriptId, Map<String, Object> scriptParams) {
        Object scriptInstance = super.getScriptInstance(scriptId);
        try {
            invokeContext.setParams(scriptParams);
            return (T) ((Script) scriptInstance).run();
        } finally {
            invokeContext.clear();
        }
    }

    private static class NoClassCacheGroovyClassLoader extends GroovyClassLoader {
        public NoClassCacheGroovyClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        @Override
        protected void setClassCacheEntry(Class cls) {
        }

        @Override
        protected Class getClassCacheEntry(String name) {
            return null;
        }
    }
}
