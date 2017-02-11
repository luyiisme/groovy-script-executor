package io.github.luyiisme.script;

import io.github.luyiisme.script.management.DefaultScriptManager;
import groovy.lang.Script;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author luyi on 16/4/19.
 */
public class DefaultScriptManagerTest {

    DefaultScriptManager scriptManager = new DefaultScriptManagerImpl();

    @Test
    public void testCacheRightCommon() {
        String scriptKey = "1111";
        scriptManager.registerScript(scriptKey, "xxxx");
        System.out.println(scriptManager.getScriptState());
        scriptManager.deRegisterScript(scriptKey);
        Assert.assertTrue(scriptManager.isEmpty());
    }

    @Test
    public void testRemoveAllOper() {
        String scriptKey = "1111";
        scriptManager.registerScript(scriptKey, "xxxx");
        scriptManager.registerScript("2222", "yyyy");
        scriptManager.removeAllScript();
        Assert.assertTrue(scriptManager.isEmpty());
    }

    class DefaultScriptManagerImpl extends DefaultScriptManager {
        @Override
        protected Script generateScriptInstance(String scriptName, String scriptText) {
            return new Script() {
                @Override
                public Object run() {
                    return null;
                }
            };
        }
    }

}
