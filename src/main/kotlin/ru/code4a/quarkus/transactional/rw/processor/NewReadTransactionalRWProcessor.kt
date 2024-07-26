package ru.code4a.quarkus.transactional.rw.processor

interface NewReadTransactionalRWProcessor {
  fun <T> with(block: () -> T): T
}
