package ru.code4a.quarkus.transactional.rw

/**
 * Manages the current transaction level for the executing thread.
 */
internal object CurrentTransactionLevel {
  enum class Level {
    READ_ONLY,
    WRITE
  }

  private val topLevel = ThreadLocal<Level>()

  /**
   * Changes the current transaction level and executes a given block of code.
   *
   * @param newLevel the new transaction level to set
   * @param block the block of code to be executed within the transaction level
   * @return the result of the block of code
   */
  fun <T> with(
    newLevel: Level,
    block: () -> T
  ): T {
    val prevTopLevel: Level? = topLevel.get()

    topLevel.set(newLevel)

    try {
      return block()
    } finally {
      topLevel.set(prevTopLevel)
    }
  }

  /**
   * Retrieves the current transaction level.
   *
   * @return the current transaction level, or null if no transaction level is set
   */
  fun get(): Level? {
    return topLevel.get()
  }
}
