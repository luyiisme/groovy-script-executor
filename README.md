# 基于Groovy的脚步引擎

- 支持运行时登记新的脚本；
- 支持卸载老的脚本，防止OOM;
- 提供执行上下文，方便在执行时传递执行参数；


# 用法
```
ScriptEngine scriptEngine = new GroovyScriptEngine();
//登记脚本
/** "123" ruleId;
 *脚本内容里: context 表示脚本上下文对象; 可以在执行时传入参数map,比如后面,scriptEngine.invoke("123", params)
 *
 * context.name 执行结果: jack
 **/
((ScriptManager) scriptEngine).registerScript("123", "println( context.name ); return 1;");

//调用
Map<String, Object> params = new HashMap<String, Object>();
params.put("name", "jack");
Assert.assertEquals(1, scriptEngine.invoke("123", params));

//删除所有的脚本
((ScriptManager) scriptEngine).removeAllScript();
```
