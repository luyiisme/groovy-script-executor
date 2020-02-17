# 基于Groovy的脚步引擎
目标：简化 groovy 作为运行时动态脚本场景的工作
- 支持卸载老的脚本类（解决groovy类存在的无法卸载导致 FullGC 问题）;
- 提供沙箱功能的拦截器支持，并且默认提供一些黑名单的Interceptors（
比如，NoReflectionAllowedInterceptor，NoSystemExitInterceptor，实际使用建议白名单方式），
- 提供执行上下文，方便在执行时传递执行参数；
- 支持运行时登记和更新脚本内容；

# 用法

```
        //PHASE 1:脚本引擎初始化线程执行
        ScriptEngine scriptEngine = new GroovyScriptEngine();
        //沙箱功能的拦截器:
        scriptEngine.addGroovyInterceptor(new NoSystemExitInterceptor());

        //PHASE 2:脚本管理线程执行
        /** "123" 脚本Id;
         * 脚本内容里: context 表示脚本上下文对象; 可以在执行时传入参数map,比如后面,scriptEngine.invoke("123", params)
         * context.name 执行结果: jack
         **/
        ((ScriptManager)scriptEngine).registerScript("123", "println( context.name ); return 1;");

        //PHASE 3:脚本执行线程,实际业务执行调用
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("123", params);
        Assert.assertSame(1, v);
        System.out.println(cr.toString());
        Assert.assertTrue(cr.toString().contains("DefaultInvokeContext.name"));

        //PHASE 4:删除所有的脚本
        ((ScriptManager)scriptEngine).removeAllScript();
```
