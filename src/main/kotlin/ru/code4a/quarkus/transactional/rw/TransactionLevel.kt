package ru.code4a.quarkus.transactional.rw

/**
 * Represents the transaction level for database operations.
 *
 * This enum defines the possible transaction levels that can be used
 * to control the behavior of database transactions in the application.
 */
enum class TransactionLevel {
  /**
   * Indicates a read-only transaction level.
   *
   * When this level is set, the transaction is optimized for read operations.
   * No data modifications are allowed in this mode. This is useful for
   * operations that only need to query data without making any changes,
   * potentially allowing for better performance and concurrency.
   */
  READ_ONLY,

  /**
   * Indicates a write transaction level.
   *
   * When this level is set, the transaction allows both read and write operations.
   * This mode should be used for operations that need to modify data in the database.
   * Write transactions typically have stronger isolation and locking mechanisms
   * compared to read-only transactions.
   */
  WRITE
}
