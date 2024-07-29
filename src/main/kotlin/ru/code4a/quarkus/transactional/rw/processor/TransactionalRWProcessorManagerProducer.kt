package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.All
import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class TransactionalRWProcessorManagerProducer(
  @All
  val existsWriteTransactionalRWProcessors: MutableList<ExistsWriteTransactionalRWProcessor>,
  @All
  val newReadTransactionalRWProcessors: MutableList<NewReadTransactionalRWProcessor>,
  @All
  val newWriteTransactionalRWProcessors: MutableList<NewWriteTransactionalRWProcessor>
) {
  @Produces
  @ApplicationScoped
  @Unremovable
  fun produceTransactionalRWProcessorManager(): TransactionalRWProcessorManager =
    TransactionalRWProcessorManager(
      existsWriteTransactionalRWProcessors,
      newReadTransactionalRWProcessors,
      newWriteTransactionalRWProcessors
    )
}
