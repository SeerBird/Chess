package game;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MoveFuture implements Future<Integer> {
    public final Choice choice;

    public MoveFuture(Choice choice) {
        this.choice = choice;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Integer get() throws InterruptedException, ExecutionException {
        if (choice.value != -1) {
            return choice.value;
        }
        while (choice.value == -1) {
            synchronized (choice) {
                choice.wait();
            }
        }
        return choice.value;
    }

    @Override
    public Integer get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
