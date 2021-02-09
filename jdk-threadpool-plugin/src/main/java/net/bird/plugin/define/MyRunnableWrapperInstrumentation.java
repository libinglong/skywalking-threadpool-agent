/*
 *    Copyright 2021 Binglong Li
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.bird.plugin.define;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.description.method.MethodDescription;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatcher;

import static org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatchers.*;


public class MyRunnableWrapperInstrumentation extends ClassEnhancePluginDefine {
    private static final String CLASS_NAME = "net.bird.agent.advice.MyRunnableWrapper";
    private static final String RUNNABLE_CLASS_INTERCEPTOR = "org.apache.skywalking.apm.plugin.jdk.threading.MyRunnableWrapperConstructorInterceptor";
    private static final String RUNNABLE_RUN_METHOD = "run";
    private static final String RUNNABLE_RUN_METHOD_INTERCEPTOR = "org.apache.skywalking.apm.plugin.jdk.threading.MyRunnableWrapperMethodInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(CLASS_NAME);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return any();
                }

                @Override
                public String getConstructorInterceptor() {
                    return RUNNABLE_CLASS_INTERCEPTOR;
                }
            }
        };
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(RUNNABLE_RUN_METHOD).and(takesArguments(0));
                }

                @Override
                public String getMethodsInterceptor() {
                    return RUNNABLE_RUN_METHOD_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return new StaticMethodsInterceptPoint[0];
    }

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }
}