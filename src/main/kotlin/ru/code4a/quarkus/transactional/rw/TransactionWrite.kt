package ru.code4a.quarkus.transactional.rw

import io.quarkus.narayana.jta.QuarkusTransaction
import io.quarkus.narayana.jta.TransactionExceptionResult
import ru.code4a.quarkus.transactional.rw.processor.TransactionalRWProcessorManager

/**
 * Provides utility methods for performing transactional writes.
 */
object TransactionWrite {
  /**
   * Executes the given block of code within a transaction, joining an existing transaction if it exists.
   *
   * @param block the block of code to be executed within the transaction
   * @return the result of the block of code
   * @throws IllegalStateException if the current transaction level is READ_ONLY
   */
  fun <T> withJoiningExisting(block: () -> T): T {
    val currentTransactionLevel = CurrentTransactionLevel.get()

    if (currentTransactionLevel == TransactionLevel.READ_ONLY) {
      throw IllegalStateException("Write transaction level cannot be inside read transaction level")
    }

    return if (currentTransactionLevel == null) {
      CurrentTransactionLevel.with(TransactionLevel.WRITE) {
        TransactionalRWProcessorManager.withBeforeNewWriteTransactionProcessors {
          QuarkusTransaction
            .joiningExisting()
            .exceptionHandler {
              TransactionExceptionResult.ROLLBACK
            }.call {
              TransactionalRWProcessorManager.withNewWriteTransactionProcessors(block)
            }
        }
      }
    } else {
      QuarkusTransaction
        .joiningExisting()
        .exceptionHandler {
          TransactionExceptionResult.ROLLBACK
        }.call {
          TransactionalRWProcessorManager.withExistsWriteTransactionProcessors(block)
        }
    }
  }

  /**
   * Executes the given block of code within a new write transaction,
   * regardless of any existing transaction.
   *
   * @param block the block of code to be executed within the new transaction
   * @return the result of the block of code
   */
  fun <T> withRequiringNew(block: () -> T): T =
    CurrentTransactionLevel.with(
      TransactionLevel.WRITE
    ) {
      TransactionalRWProcessorManager.withBeforeNewWriteTransactionProcessors {
        QuarkusTransaction
          .requiringNew()
          .exceptionHandler {
            TransactionExceptionResult.ROLLBACK
          }.call {
            TransactionalRWProcessorManager.withNewWriteTransactionProcessors(block)
          }
      }
    }
}
