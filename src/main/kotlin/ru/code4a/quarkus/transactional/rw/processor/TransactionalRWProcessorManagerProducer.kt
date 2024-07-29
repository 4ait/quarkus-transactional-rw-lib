package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.All
import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject

class TransactionalRWProcessorManagerProducer {

  @Inject
  @All
  lateinit var existsWriteTransactionalRWProcessors: MutableList<ExistsWriteTransactionalRWProcessor>

  @Inject
  @All
  lateinit var newReadTransactionalRWProcessors: MutableList<NewReadTransactionalRWProcessor>

  @Inject
  @All
  lateinit var newWriteTransactionalRWProcessors: MutableList<NewWriteTransactionalRWProcessor>

  @Produces
  @ApplicationScoped
  @Unremovable
  fun produceTransactionalRWProcessorManager(): TransactionalRWProcessorManager {
    return TransactionalRWProcessorManager(
      existsWriteTransactionalRWProcessors,
      newReadTransactionalRWProcessors,
      newWriteTransactionalRWProcessors
    )
  }
}
