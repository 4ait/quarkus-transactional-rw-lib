package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.All
import io.quarkus.arc.Arc
import io.quarkus.arc.Unremovable
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages the execution of transactional processors for different transaction types.
 */
@ApplicationScoped
@Unremovable
class TransactionalRWProcessorManager(
  @All
  private val existsWriteTransactionalRWProcessors: MutableList<ExistsWriteTransactionalRWProcessor>,
  @All
  private val newReadTransactionalRWProcessors: MutableList<NewReadTransactionalRWProcessor>,
  @All
  private val newWriteTransactionalRWProcessors: MutableList<NewWriteTransactionalRWProcessor>
) {

  private lateinit var existsWriteTransactionalRWProcessorsCallback: (() -> Any?) -> Any?
  private lateinit var newReadTransactionalRWProcessorsCallback: (() -> Any?) -> Any?
  private lateinit var newWriteTransactionalRWProcessorsCallback: (() -> Any?) -> Any?

  @PostConstruct
  protected fun onPostConstruct() {
    var currentBlock: (() -> Any?) -> Any? = { block -> block() }
    for (processor in newReadTransactionalRWProcessors) {
      currentBlock = { block ->
        processor.with(block)
      }
    }
    newReadTransactionalRWProcessorsCallback = currentBlock

    currentBlock = { block -> block() }
    for (processor in existsWriteTransactionalRWProcessors) {
      currentBlock = { block ->
        processor.with(block)
      }
    }
    existsWriteTransactionalRWProcessorsCallback = currentBlock

    currentBlock = { block -> block() }
    for (processor in newWriteTransactionalRWProcessors) {
      currentBlock = { block ->
        processor.with(block)
      }
    }
    newWriteTransactionalRWProcessorsCallback = currentBlock
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
