# Quarkus Transactional Read-Write Library

This library provides enhanced transactional management for Quarkus applications, offering distinct handling for read-only and write transactions. It allows for fine-grained control over transaction propagation and execution.

## Features

- Separate annotations for read-only and write transactions
- Support for joining existing transactions or creating new ones
- Thread-safe transaction level management
- Extensible processor system for custom transaction handling

## Interaction with @Transactional

This library is designed to work alongside the standard `@Transactional` annotation from Jakarta EE. However, it's important to note the following:

1. When both `@Transactional` and one of this library's annotations (`@TransactionalReadOnly` or `@TransactionalWrite`) are present, the behavior is determined by the interceptor ordering.

2. It's recommended to use either this library's annotations or `@Transactional`, but not both on the same method to avoid confusion.

3. If you need to use both, be aware that:
  - This library's annotations provide more fine-grained control over read/write behavior.
  - `@Transactional` provides more options for configuring transaction attributes like isolation level and timeout.

4. In a mixed environment, ensure that your transaction management strategy is consistent and well-documented to prevent unexpected behavior.

## Installation

Add the following dependency to your project:

```xml
<dependency>
  <groupId>ru.code4a</groupId>
  <artifactId>quarkus-transactional-rw-lib</artifactId>
  <version>[VERSION]</version>
</dependency>
```

Replace `[VERSION]` with the latest version of the library.

## Usage

### Annotations

The library provides two main annotations for transaction management:

1. `@TransactionalReadOnly`: For read-only operations
2. `@TransactionalWrite`: For write operations

Both annotations accept a `type` parameter to specify the transaction propagation behavior:

- `Type.JOIN_EXISTING`: Joins an existing transaction if present, otherwise creates a new one (default)
- `Type.REQUIRES_NEW`: Always creates a new transaction

Example usage:

```kotlin
@TransactionalReadOnly
fun readOnlyOperation() {
    // Read-only operation
}

@TransactionalWrite(type = TransactionalWrite.Type.REQUIRES_NEW)
fun writeOperation() {
    // Write operation in a new transaction
}
```

### Programmatic Transaction Control

For more fine-grained control, you can use the `TransactionReadOnly` and `TransactionWrite` objects:

```kotlin
TransactionReadOnly.withJoiningExisting {
    // Read-only operation joining an existing transaction
}

TransactionWrite.withRequiringNew {
    // Write operation in a new transaction
}
```

## Recommended Usage

To ensure proper transaction usage and prevent write transactions from being nested within read transactions, it is strongly recommended to use this library in conjunction with the Detekt static code analysis tool and the Usage Detection Plugin.

### Using with Detekt and Usage Detection Plugin

1. Add Detekt and the Usage Detection Plugin to your project. In your `build.gradle.kts` file:

```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt") version "<detekt_version>"
}

dependencies {
    detektPlugins("ru.code4a:usage-detection-detekt-plugin:<plugin_version>")
}
```

2. Configure the rules in your Detekt configuration file (e.g., `detekt.yml`):

```yaml
foura_usage_detection:
  ValidateAllowDeepInvokesDetektRule:
    active: true
    configYaml: |
      rootRules:
        - message: "Write transaction detected inside a read-only transaction"
          visitFilter:
            rootsAndNested:
              is:
                methodsWithAnnotations:
                  - ru.code4a.quarkus.transactional.rw.annotations.TransactionalReadOnly
          notAllowedInvokes:
            methodsWithAnnotations:
              - ru.code4a.quarkus.transactional.rw.annotations.TransactionalWrite
```

3. Run Detekt as part of your build process or manually:

```shell
./gradlew detekt
```

By integrating Detekt with the Usage Detection Plugin, you can automatically detect and prevent cases where write transactions are incorrectly nested within read-only transactions. This helps maintain the integrity of your transactional boundaries and ensures proper usage of the Quarkus Transactional Read-Write library.

Remember to adjust the configuration according to your specific package names and requirements. This setup will help catch potential misuses of transactions at compile-time, reducing the risk of runtime errors and improving overall code quality.

## Transaction Level Management

The library uses `CurrentTransactionLevel` to manage transaction levels for the executing thread. This ensures proper nesting and propagation of transaction levels.

## Interceptors

The library provides interceptors for each combination of transaction type and propagation behavior:

- `TransactionalReadOnlyJoinExistingInterceptor`
- `TransactionalReadOnlyRequiringNewInterceptor`
- `TransactionalWriteJoinExistingInterceptor`
- `TransactionalWriteRequiringNewInterceptor`

These interceptors are automatically applied when using the corresponding annotations.

## Processor System

The library includes an extensible processor system that allows for custom handling of transactions:

- `ExistsWriteTransactionalRWProcessor`: For existing write transactions
- `NewReadTransactionalRWProcessor`: For new read-only transactions
- `NewWriteTransactionalRWProcessor`: For new write transactions

To implement a custom processor, create a class that implements the appropriate interface and it will be automatically picked up by the `TransactionalRWProcessorManager`.

## Exception Handling

- Read-only transactions are set to commit even if an exception occurs.
- Write transactions are set to roll back when an exception occurs.

## Limitations

- A write transaction cannot be started within a read-only transaction. Attempting to do so will result in an `IllegalStateException`.

## Best Practices

1. Use `@TransactionalReadOnly` for operations that only read data and do not modify the database.
2. Use `@TransactionalWrite` for operations that modify data.
3. Use `Type.REQUIRES_NEW` when you need to ensure a operation runs in a separate transaction, regardless of any existing transaction.
4. Be cautious when nesting transactions, especially when mixing read-only and write transactions.

# Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

# License

Apache 2.0
