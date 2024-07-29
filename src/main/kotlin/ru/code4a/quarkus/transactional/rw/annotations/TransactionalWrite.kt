package ru.code4a.quarkus.transactional.rw.annotations

import jakarta.interceptor.InterceptorBinding
import java.lang.annotation.Inherited

/**
 * Annotation to mark methods or classes for write transactional behavior.
 *
 * @property type the type of transaction propagation
 */
@InterceptorBinding
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@Inherited
annotation class TransactionalWrite(
  val type: Type = Type.JOIN_EXISTING
) {
  /**
   * Defines the type of transaction propagation.
   */
  enum class Type {
    JOIN_EXISTING,
    REQUIRES_NEW
  }
}
