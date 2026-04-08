# Logger System
> Structured, leveled logging with fast and sugared interfaces, DI-wired modules.

---

## Core Interfaces

| Interface | Methods | Purpose |
|---|---|---|
| `Logger` | `log(level, msg)`, `info/warn/error/debug(msg)` | Core logging contract |
| `LogClient` | `log(msg)`, `startTimer()`, `stopTimer()` | Higher-level client for timed operations |

---

## Implementations

| Class | Implements | Notes |
|---|---|---|
| `FastLogger` | `Logger` | Minimal overhead — direct write, no formatting overhead |
| `SugaredLogger` | `Logger` | Richer API — structured fields, key-value pairs |
| `LogClientImpl` | `LogClient` | Wraps a `Logger`; tracks `Timer` for duration logging |
| `Timer` | — | Start/stop wall-clock timer; computes elapsed duration |

---

## Dependency Injection (Guice)

| Module | Binds |
|---|---|
| `LoggingModule` | `Logger → FastLogger` (or `SugaredLogger` depending on config) |
| `PropertiesModule` | Loads external config properties into DI graph |

---

## Relationships

```
LogClient (interface)
 └── LogClientImpl
      ├── Logger (interface)
      │    ├── FastLogger
      │    └── SugaredLogger
      └── Timer

LoggingModule  (Guice)
 └── binds Logger → concrete impl

Main / TaskManager
 └── uses LogClient (injected)
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Interface Segregation** | `Logger` vs `LogClient` — separate concerns (formatting vs timing) |
| **Dependency Injection** | Guice modules wire implementations |
| **Strategy** | Swap `FastLogger` ↔ `SugaredLogger` without changing call sites |
| **Facade** | `LogClientImpl` — simpler API wrapping the raw `Logger` |

---

## Key Flow — Log a Timed Task

```java
LogClient client = injector.getInstance(LogClient.class);
client.startTimer();
// ... do work ...
client.stopTimer();
client.log("Task completed in " + timer.elapsed() + "ms");
// Internally → Logger.info(msg) → writes to output sink
```

---

## Log Levels (conventional)
`DEBUG < INFO < WARN < ERROR`  
Level filtering: only logs at or above configured level are written.
