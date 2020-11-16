package de.espirit.sfcc.publication

import java.util.concurrent.*
import java.util.function.Function

// TODO When will this executor be shut down?
internal val executor: Executor by lazy {
    val parallelism = Runtime.getRuntime().availableProcessors()
    ForkJoinPool(parallelism, { pools -> object : ForkJoinWorkerThread(pools) {} }, null, true)
}

fun <T, U> CompletableFuture<T>.thenComposeWithSharedExecutor(function: Function<in T?, out CompletionStage<U>>): CompletableFuture<U> =
        this.thenComposeAsync(function, executor)

fun <T, U> CompletableFuture<T>.thenApplyWithSharedExecutor(function: Function<in T, out U>): CompletableFuture<U> =
        this.thenApplyAsync(function, executor)

fun runAsyncWithSharedExecutor(runnable: Runnable): CompletableFuture<Void> = CompletableFuture.runAsync(runnable, executor)
