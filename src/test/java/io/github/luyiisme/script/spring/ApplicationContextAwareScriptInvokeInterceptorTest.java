package io.github.luyiisme.script.spring;

import java.util.HashMap;

import io.github.luyiisme.script.groovy.GroovyScriptEngine;
import io.github.luyiisme.script.management.ScriptManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.when;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-20 17:36
 **/
public class ApplicationContextAwareScriptInvokeInterceptorTest {

    @Test
    public void testApplicationContextAware() {
        ApplicationContext appctxt = Mockito.mock(ApplicationContext.class);
        when(appctxt.getId()).thenReturn("mock");

        ApplicationContextAwareScriptInvokeInterceptor invokeInterceptor
            = new ApplicationContextAwareScriptInvokeInterceptor();
        invokeInterceptor.setApplicationContext(appctxt);

        GroovyScriptEngine scriptEngine = new GroovyScriptEngine();
        scriptEngine.addScriptInvokeInterceptor(invokeInterceptor);

        ((ScriptManager)scriptEngine).registerScript("xx", "return context.applicationContext.getId()");
        String xx = scriptEngine.invoke("xx", new HashMap<String, Object>());

        Assert.assertEquals("mock", xx);

    }
}
