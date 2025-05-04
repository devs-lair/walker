package devs.lair.walkers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;

public class MultiThreadWalker<T> {
    private final Predicate<T> filter;
    private final ChildSupplier<T> supplier;
    private final T root;
    private final Predicate<T> forkPredicate;

    public MultiThreadWalker(T root, Predicate<T> filter, Predicate<T> forkPredicate, ChildSupplier<T> supplier) {
        this.root = root;
        this.filter = filter;
        this.supplier = supplier;
        this.forkPredicate = forkPredicate;
    }

    public List<T> collect() {
        WalkTask<T> task = new WalkTask<>(root, filter, forkPredicate, supplier);
        return task.invoke();
    }

    public static class WalkTask<T> extends RecursiveTask<List<T>> {
        private final T root;
        private final Predicate<T> filter;
        private final ChildSupplier<T> supplier;
        private final Predicate<T> forkPredicate;

        public WalkTask(T root, Predicate<T> filter, Predicate<T> forkPredicate, ChildSupplier<T> supplier) {
            this.root = root;
            this.filter = filter;
            this.supplier = supplier;
            this.forkPredicate = forkPredicate;
        }

        @Override
        protected List<T> compute() {
            List<WalkTask<T>> tasks = new ArrayList<>();
            List<T> result = new ArrayList<>();

            for (T t : supplier.getChildren(root)) {
                if (forkPredicate.test(t)) {
                    tasks.add(new WalkTask<>(t, filter, forkPredicate, supplier));
                }

                if (filter.test(t)) {
                    result.add(t);
                }
            }

            result.addAll(
                    ForkJoinTask.invokeAll(tasks)
                    .stream()
                    .map(ForkJoinTask::join)
                    .flatMap(Collection::stream)
                    .toList());

            return result;
        }
    }

    @FunctionalInterface
    public interface ChildSupplier<T> {
        T[] getChildren(T t);
    }
}