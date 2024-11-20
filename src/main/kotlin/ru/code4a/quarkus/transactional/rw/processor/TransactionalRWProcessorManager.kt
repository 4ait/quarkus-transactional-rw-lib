package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.Arc

/**
 * Manages the execution of transactional processors for different transaction types.
 */
class TransactionalRWProcessorManager(
  existsWriteTransactionalRWProcessors: List<ExistsWriteTransactionalRWProcessor>,
  newReadTransactionalRWProcessors: List<NewReadTransactionalRWProcessor>,
  newWriteTransactionalRWProcessors: List<NewWriteTransactionalRWProcessor>,
  beforeNewWriteTransactionalRWProcessors: List<BeforeNewWriteTransactionalRWProcessor>
) {
  val existsWriteTransactionalRWProcessorsCallback: (
    () -> Any?
  ) -> Any? = createCallbackFromProcessors(existsWriteTransactionalRWProcessors)
  val newReadTransactionalRWProcessorsCallback: (
    () -> Any?
  ) -> Any? = createCallbackFromProcessors(newReadTransactionalRWProcessors)
  val newWriteTransactionalRWProcessorsCallback: (
    () -> Any?
  ) -> Any? = createCallbackFromProcessors(newWriteTransactionalRWProcessors)
  val beforeNewWriteTransactionalRWProcessorsCallback: (
      () -> Any?
  ) -> Any? = createCallbackFromProcessors(beforeNewWriteTransactionalRWProcessors)

  companion object {
    private val instance
      get() =
        Arc
          .container()
          .instance(TransactionalRWProcessorManager::class.java)
          .get()

    internal fun <T> withNewReadTransactionProcessors(block: () -> T): T = instance.newReadTransactionalRWProcessorsCallback(block) as T

    internal fun <T> withNewWriteTransactionProcessors(block: () -> T): T = instance.newWriteTransactionalRWProcessorsCallback(block) as T

    internal fun <T> withBeforeNewWriteTransactionProcessors(block: () -> T): T = instance.beforeNewWriteTransactionalRWProcessorsCallback(block) as T

    internal fun <T> withExistsWriteTransactionProcessors(block: () -> T): T =
      instance.existsWriteTransactionalRWProcessorsCallback(block) as T
  }
}

internal fun createCallbackFromProcessors(processors: List<TransactionalRWProcessor>): (() -> Any?) -> Any? {
  var currentBlock: (() -> Any?) -> Any? = { block -> block() }

  for (processor in processors.sortedBy { it.priority }) {
    val iterBlock = currentBlock

    currentBlock = { block ->
      processor.with {
        iterBlock(block)
      }
    }
  }

  return currentBlock
}
