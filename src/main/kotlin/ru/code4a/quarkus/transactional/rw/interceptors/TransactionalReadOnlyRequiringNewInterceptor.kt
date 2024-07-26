package ru.code4a.quarkus.transactional.rw.interceptors

import jakarta.annotation.Priority
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InvocationContext
import ru.code4a.quarkus.transactional.rw.TransactionReadOnly
import ru.code4a.quarkus.transactional.rw.annotations.TransactionalReadOnly

@TransactionalReadOnly(TransactionalReadOnly.Type.REQUIRES_NEW)
@Priority(0)
@Interceptor
class TransactionalReadOnlyRequiringNewInterceptor {
  @AroundInvoke
  fun invocation(context: InvocationContext): Any? {
    return TransactionReadOnly.withRequiringNew {
      context.proceed()
    }
  }
}
