package com.lbl.advice;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2021/2/4
 */
public class MyRunnableWrapper implements Runnable {

    final Runnable runnable;

    private MyRunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    public static MyRunnableWrapper of(Runnable r) {
        return new MyRunnableWrapper(r);
    }

    public void run() {
        this.runnable.run();
    }
}
