package net.bird;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2021/1/4
 */

public class AgentTest {

    @Test
    public void fun() {

        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    System.out.println("hh");
                    return "result";
                }
            });
        } catch (Throwable e){
            e.printStackTrace();
        }

    }
}
