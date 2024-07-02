package codesquad;

import java.util.concurrent.ExecutorService;

public class HttpThreadPool {
    private final ExecutorService executor;

    public HttpThreadPool(ExecutorService executor) {
        this.executor = executor;
    }

    public void execute(HttpTask task) {
        executor.execute(task);
    }
}
