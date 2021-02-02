# skywalking-threadpool-agent



This project is designed to make it possible to propagate skywalking trace without changing user's code when using threadpool.

#### How to propagate

To propagate trace between thread, we may want to enhance Runnable first. But it's not good since Runnable is not only used in thread.So advicing ThreadPoolExecutor#execute to wrap the Runnable param is a good choice.

The java agent of skywalking usually add a field and implement interface EnhancedInstance when it enhance instances.It fails when the class of the enhanced instance has been loaded because most JVMs do not allow changes in the class file format for classes that have been loaded previously.ThreadPoolExecutor is a special class in the bootstrap class path and may be loaded in many scene. Fortunately we don't need to change the layout of ThreadPoolExecutor if we just want to wrap the Runnable param. This agent wrap the Runnable with the org.apache.skywalking.apm.toolkit.trace.RunnableWrapper. And there is a plugin in skywalking agent enhance RunnableWrapper. Now the trace is propagated!



#### How to use

run 

> mvn clean package,

you will get the agent.

In jvm options, you should add this agent AFTER the skywalking java agent.

