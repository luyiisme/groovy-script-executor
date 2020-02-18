
package io.github.luyiisme.script;

import java.util.Map;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

/**
 * @author luyi on 16/4/19.
 */
public interface ScriptEngine {

    /**
     * 执行入口
     *
     * @param scriptName     脚本Id,
     * @param scriptParams 脚本执行的参数[参数名--参数实例]
     * @param <T>          可以为null
     * @return
     */
    <T> T invoke(String scriptName, Map<String, Object> scriptParams);

    void setCompilerConfiguration(CompilerConfiguration cc);

    void addGroovyInterceptor(GroovyInterceptor groovyInterceptor);
}
