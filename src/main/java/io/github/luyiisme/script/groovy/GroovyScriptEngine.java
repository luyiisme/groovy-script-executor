package io.github.luyiisme.script.groovy;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import io.github.luyiisme.script.ScriptEngine;
import io.github.luyiisme.script.api.ScriptInvokeInterceptor;
import io.github.luyiisme.script.common.NoSuchScriptException;
import io.github.luyiisme.script.management.DefaultScriptManager;
import io.github.luyiisme.script.sandbox.RejectASTTransformsCustomizer;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

/**
 * 默认脚本引擎，需要初始化时先注册登记脚本，
 *
 * @author luyi on 16/4/19.
 */
public class GroovyScriptEngine extends DefaultScriptManager implements ScriptEngine {

    private final DefaultInvokeContext invokeContext = new DefaultInvokeContext();
    public static final String CONTEXT_KEY = "context";
    private final Binding binding;
    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    private List<GroovyInterceptor> groovyInterceptors = new ArrayList<GroovyInterceptor>();
    private List<ScriptInvokeInterceptor> scriptInvokeInterceptors = new ArrayList<ScriptInvokeInterceptor>();

    public GroovyScriptEngine() {
        binding = new Binding();
        binding.setVariable(CONTEXT_KEY, invokeContext);
        compilerConfiguration.addCompilationCustomizers(new RejectASTTransformsCustomizer(), new SandboxTransformer());
    }

    @Override
    public void setCompilerConfiguration(CompilerConfiguration cc) {
        this.compilerConfiguration = cc;
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return compilerConfiguration;
    }

    @Override
    public void addGroovyInterceptor(GroovyInterceptor groovyInterceptor) {
        groovyInterceptors.add(groovyInterceptor);
    }

    @Override
    public void addScriptInvokeInterceptor(ScriptInvokeInterceptor scriptInvokeInterceptor) {
        scriptInvokeInterceptors.add(scriptInvokeInterceptor);
    }

    @Override
    protected Script generateScriptInstance(String scriptName, String scriptText) {
        GroovyClassLoader scriptClassLoader = AccessController
            .doPrivileged(new PrivilegedAction<GroovyClassLoader>() {
                @Override
                public GroovyClassLoader run() {
                    return new GroovyClassLoader(getClass().getClassLoader(), compilerConfiguration);
                }
            });

        Class<?> clazz = scriptClassLoader.parseClass(scriptText);
        try {
            Script script = null;
            if (Script.class.isAssignableFrom(clazz)) {
                try {
                    Constructor constructor = clazz.getConstructor(Binding.class);
                    script = (Script)constructor.newInstance(binding);
                } catch (Throwable e) {
                    // Fallback for non-standard "Script" classes.
                    script = (Script)clazz.newInstance();
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
    public <T> T invoke(String scriptName, Map<String, Object> scriptParams) {
        Object scriptInstance = super.getScriptInstance(scriptName);
        if (scriptInstance == null) {
            throw new NoSuchScriptException(scriptName);
        }
        postProcessScriptInvocation(scriptName, scriptInstance, scriptParams);

        try {
            invokeContext.setParams(scriptParams);
            for (GroovyInterceptor interceptor : groovyInterceptors) {
                interceptor.register();
            }
            return (T)((Script)scriptInstance).run();
        } finally {
            for (GroovyInterceptor interceptor : groovyInterceptors) {
                interceptor.unregister();
            }
            invokeContext.clear();
        }
    }

    private void postProcessScriptInvocation(String scriptName, Object scriptInstance,
        Map<String, Object> scriptParams) {
        try {
            for (ScriptInvokeInterceptor invokeInterceptor : scriptInvokeInterceptors) {
                invokeInterceptor.preHandle(scriptName, scriptInstance, scriptParams);
            }
        } catch (Exception e) {
            throw new RuntimeException("script invoke interceptors err!", e);
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
