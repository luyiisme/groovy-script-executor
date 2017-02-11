
package io.github.luyiisme.script;

import java.util.Map;

/**
 * @author luyi on 16/4/19.
 */
public interface ScriptEngine {

    /**
     * 执行入口
     *
     * @param scriptId     脚本Id
     * @param scriptParams 脚本执行的参数[参数名--参数实例]
     * @param <T>          可以为null
     * @return
     */
    <T> T invoke(String scriptId, Map<String, Object> scriptParams);

}
