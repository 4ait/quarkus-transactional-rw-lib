package ru.code4a.quarkus.transactional.rw.processor

import io.quarkus.arc.Arc

interface NewReadTransactionalRWProcessor {
  fun <T> with(block: () -> T): T
}
