package de.espirit.sfcc.publication

import de.espirit.sfcc.publication.ConfigurationParameter.MAX_CONCURRENT_REQUESTS
import java.util.concurrent.*
import java.util.function.Function

// TODO When will this executor be shut down?
internal val executor: Executor by lazy {
    // Basically the same as executing ForkJoinPool() but setting async mode to true and using an configurable parralelism.
    // Using ForkJoinPool.commonPool is no option, as FirstSpirit sets a SecurityManager.
    // In Java 11 the implementation of the default thread factory changed.
    // Using it in the FirstSpirit context causes a java.security.AccessControlException.
    // Therefore we mimic the Java 8 behaviour.
    val parallelism = configuration.getParamValue(MAX_CONCURRENT_REQUESTS)?.toIntOrNull() ?: 10
    ForkJoinPool(parallelism, { pools -> object : ForkJoinWorkerThread(pools) {} }, null, true)
}

fun <T, U> CompletableFuture<T>.thenComposeWithSharedExecutor(function: Function<in T?, out CompletionStage<U>>): CompletableFuture<U> =
        this.thenComposeAsync(function, executor)

fun runAsyncWithSharedExecutor(runnable: Runnable): CompletableFuture<Void> = CompletableFuture.runAsync(runnable, executor)
