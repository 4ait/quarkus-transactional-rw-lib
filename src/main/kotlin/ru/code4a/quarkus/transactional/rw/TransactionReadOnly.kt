package ru.code4a.quarkus.transactional.rw

import io.quarkus.narayana.jta.QuarkusTransaction
import io.quarkus.narayana.jta.TransactionExceptionResult
import ru.code4a.quarkus.transactional.rw.processor.TransactionalRWProcessorManager

/**
 * An object for executing code within transaction levels marked as READ_ONLY.
 * Provides methods for executing code within existing or new transactions.
 */
object TransactionReadOnly {
  /**
   * Executes the provided block of code within a read-only transaction,
   * joining an existing transaction if one exists.
   *
   * @param block the block of code to be executed within the transaction
   * @return the result of the block of code
   */
  fun <T> withJoiningExisting(block: () -> T): T {
    val currentTransactionLevel = CurrentTransactionLevel.get()

    return if (currentTransactionLevel == null) {
      CurrentTransactionLevel.with(CurrentTransactionLevel.Level.READ_ONLY) {
        QuarkusTransaction
          .joiningExisting()
          .exceptionHandler {
            // We don't need to handle Exceptions for READ_ONLY transactions
            TransactionExceptionResult.COMMIT
          }.call {
            TransactionalRWProcessorManager.withNewReadTransactionProcessors(block)
          }
      }
    } else {
      block()
    }
  }

  /**
   * Executes the given block of code in a new transaction, independent of the current transaction.
   *
   * @param block the block of code to be executed within the new transaction
   * @return the result of the block of code
   */
  fun <T> withRequiringNew(block: () -> T): T =
    CurrentTransactionLevel
      .with(
        CurrentTransactionLevel.Level.READ_ONLY
      ) {
        QuarkusTransaction
          .requiringNew()
          .exceptionHandler {
            // We don't need to handle Exceptions for READ_ONLY transactions
            TransactionExceptionResult.COMMIT
          }.call {
            TransactionalRWProcessorManager.withNewReadTransactionProcessors(block)
          }
      }
}
