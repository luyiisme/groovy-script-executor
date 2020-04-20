package io.github.luyiisme.script.spring;

import java.util.Map;

import io.github.luyiisme.script.api.ScriptInvokeInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * make spring application context available from script invocation;
 *
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-20 17:24
 **/
public class ApplicationContextAwareScriptInvokeInterceptor implements ScriptInvokeInterceptor,
    ApplicationContextAware {
    public static final String APPLICATION_CONTEXT_KEY = "applicationContext";
    private ApplicationContext applicationContext;

    @Override
    public void preHandle(String scriptName, Object scriptInstance, Map<String, Object> scriptInvocationParams)
        throws Exception {
        if (applicationContext == null) { return; }
        scriptInvocationParams.put(APPLICATION_CONTEXT_KEY, applicationContext);
        return;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
