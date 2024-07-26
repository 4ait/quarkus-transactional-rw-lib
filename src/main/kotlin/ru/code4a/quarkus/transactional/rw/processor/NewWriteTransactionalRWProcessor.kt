package ru.code4a.quarkus.transactional.rw.processor

interface NewWriteTransactionalRWProcessor {
  fun <T> with(block: () -> T): T
}
