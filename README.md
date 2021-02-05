# skywalking-threadpool-agent

This project is designed to make it possible to propagate skywalking trace without changing user's code when using threadpool.

If the project helps you, please star it!

#### How to propagate

To propagate trace between thread, we may want to enhance Runnable first. But it's not good since Runnable is not only 
used in thread.So advicing ThreadPoolExecutor#execute to wrap the Runnable param is a good choice.But it's hard to do 
it.The java agent of skywalking usually add a field and implement interface EnhancedInstance when it enhance instances.
It fails when the class of the enhanced instance has been loaded because most JVMs do not allow changes in the class 
file format for classes that have been loaded previously.ThreadPoolExecutor is a special class in the bootstrap class 
path and may be loaded at any code. Fortunately we don't need to change the layout of ThreadPoolExecutor if we just want 
to wrap the Runnable param. So let's write another agent to do this.

> You can not enhance the intance if it's class has already been loaded.



#### How to wrap the Runnable param?

Note the ThreadPoolExecutor is a bootstap class. We must inject the wrap class, such as com.lbl.RunnableWrapper into 
bootstrap classloader.Then you should write a skywalking plugin to enhance com.lbl.RunnableWrapper. There is already a 
RunnableWrapper, org.apache.skywalking.apm.toolkit.trace.RunnableWrapper, but we can not use. why?

This RunnableWrapper has a plugin. But the condition is checking if there is @TraceCrossThread. Then you should also 
inject TraceCrossThread into bootstrap classloader. Even if you do like this, you still fail. Byte buddy in skywalking 
will use net.bytebuddy.pool.TypePool.Default.WithLazyResolution.LazyTypeDescription to find the annotations of a class. 
The LazyTypeDescription finds annotations by using a URLClassLoader with no urls if the classloader is 
null(bootstrap classloader). So you should write your own wrapper class, and simplely use a name match condition.

#### How to use

run 

> mvn clean package,

you will get the agent.

In jvm options, you should add this agent after the skywalking java agent since the wrapper class should be enhanced 
by skywalking java agent



