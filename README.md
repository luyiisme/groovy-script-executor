# 基于Groovy的脚步引擎
目标：简化 Groovy 作为动态脚本场景的工作

相比直接使用 GroovyShell，我们为脚本场景做了些方便使用的封装。已应用于内部很多项目，这是抽出的一个简化版本。

详细 features:
- 支持运行时基于 ScriptName 的在内存中登记和更新脚本内容，方便 ScriptName 标识不变情况下做脚本内容修改；
- 解决常见的容易 FullGC 或 OOM 问题（Groovy 在动态脚本场景，容易因脚本过多对应 class无法正常卸载导致 FullGC的问题，网上该类问题很普遍）;
- 提供沙箱功能，沙箱的限制控制能力通过拦截器支持，并默认提供一些黑名单的 GroovyInterceptors（
比如，NoReflectionAllowedInterceptor，NoSystemExitInterceptor），特别限定的场景建议直接使用白名单方式；
- 提供执行上下文，方便在执行时传递执行参数；
- TODO,提供脚本的 Repository，方便作为个更开箱即用的方案；

# 用法

```
        //PHASE 1:脚本引擎初始化线程执行
        ScriptEngine scriptEngine = new GroovyScriptEngine();
        //沙箱功能的拦截器:（可选，但建议有）
        scriptEngine.addGroovyInterceptor(new NoSystemExitInterceptor());
```
```
        //PHASE 2:脚本管理线程执行
        /** "123" 作为 scriptName;
         * 脚本内容里: context 表示脚本上下文对象,它对应于您在执行时传入参数 map,比如后面,scriptEngine.invoke("123", params)
         * context.name 执行结果: jack
         **/
        ((ScriptManager)scriptEngine).registerScript("123", "println( context.name ); return 1;");
```
```
        //PHASE 3:脚本执行线程,实际业务执行调用
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "jack");
        Integer v = scriptEngine.invoke("123", params);
        Assert.assertSame(1, v);
```

```
        //PHASE 4:删除所有的脚本
        ((ScriptManager)scriptEngine).removeAllScript();
```
# 最佳实践
- 使用方可以自己维护个脚本的管理DB表；
 业务应用启动时或运行时发生脚本更新时 GroovyScriptEngine 同步刷新加载；
- 引入"context" 这个变量是 built-in，方便脚本里使用参数的；
- GroovyScriptEngine是否需要多个实例，根据每个使用场景的差异性决定；