package ru.code4a.quarkus.transactional.rw.processor

interface ExistsWriteTransactionalRWProcessor {
  fun <T> with(block: () -> T): T
}
