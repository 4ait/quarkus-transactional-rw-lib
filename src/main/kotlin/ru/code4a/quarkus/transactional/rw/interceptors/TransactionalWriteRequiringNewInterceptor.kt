package ru.code4a.quarkus.transactional.rw.interceptors

import jakarta.annotation.Priority
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InvocationContext
import ru.code4a.quarkus.transactional.rw.TransactionWrite
import ru.code4a.quarkus.transactional.rw.annotations.TransactionalWrite

@TransactionalWrite(TransactionalWrite.Type.REQUIRES_NEW)
@Priority(0)
@Interceptor
class TransactionalWriteRequiringNewInterceptor {
  @AroundInvoke
  fun invocation(context: InvocationContext): Any? {
    return TransactionWrite.withRequiringNew {
      context.proceed()
    }
  }
}
