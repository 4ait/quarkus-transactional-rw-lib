package ru.code4a.quarkus.transactional.rw.processor

interface TransactionalRWProcessor {
  val priority: Long
    get() = Long.MIN_VALUE

  fun <T> with(block: () -> T): T
}
