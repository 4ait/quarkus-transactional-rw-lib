package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.Arc

/**
 * Manages the execution of transactional processors for different transaction types.
 */
class TransactionalRWProcessorManager(
  existsWriteTransactionalRWProcessors: MutableList<ExistsWriteTransactionalRWProcessor>,
  newReadTransactionalRWProcessors: MutableList<NewReadTransactionalRWProcessor>,
  newWriteTransactionalRWProcessors: MutableList<NewWriteTransactionalRWProcessor>
) {

  private val existsWriteTransactionalRWProcessorsCallback: (() -> Any?) -> Any?
  private val newReadTransactionalRWProcessorsCallback: (() -> Any?) -> Any?
  private val newWriteTransactionalRWProcessorsCallback: (() -> Any?) -> Any?

  init {
    newReadTransactionalRWProcessorsCallback =
      createCallbackFromProcessors(newReadTransactionalRWProcessors)

    existsWriteTransactionalRWProcessorsCallback =
      createCallbackFromProcessors(existsWriteTransactionalRWProcessors)

    newWriteTransactionalRWProcessorsCallback =
      createCallbackFromProcessors(newWriteTransactionalRWProcessors)
  }

  companion object {
    private val instance
      get() = Arc
        .container()
        .instance(TransactionalRWProcessorManager::class.java)
        .get()

    internal fun <T> withNewReadTransactionProcessors(block: () -> T): T {
      return instance.newReadTransactionalRWProcessorsCallback(block) as T
    }

    internal fun <T> withNewWriteTransactionProcessors(block: () -> T): T {
      return instance.newWriteTransactionalRWProcessorsCallback(block) as T
    }

    internal fun <T> withExistsWriteTransactionProcessors(block: () -> T): T {
      return instance.existsWriteTransactionalRWProcessorsCallback(block) as T
    }
  }
}

internal fun createCallbackFromProcessors(processors: List<TransactionalRWProcessor>): (() -> Any?) -> Any? {
  var currentBlock: (() -> Any?) -> Any? = { block -> block() }

  for (processor in processors.sortedBy { it.priority }) {
    currentBlock = { block ->
      processor.with(block)
    }
  }

  return currentBlock
}
