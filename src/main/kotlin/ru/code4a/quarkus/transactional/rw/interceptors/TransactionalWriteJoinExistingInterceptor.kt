package ru.code4a.quarkus.transactional.rw.interceptors

import jakarta.annotation.Priority
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InvocationContext
import ru.code4a.quarkus.transactional.rw.TransactionWrite
import ru.code4a.quarkus.transactional.rw.annotations.TransactionalWrite

@TransactionalWrite(TransactionalWrite.Type.JOIN_EXISTING)
@Priority(0)
@Interceptor
class TransactionalWriteJoinExistingInterceptor {
  @AroundInvoke
  fun invocation(context: InvocationContext): Any? =
    TransactionWrite.withJoiningExisting {
      context.proceed()
    }
}
