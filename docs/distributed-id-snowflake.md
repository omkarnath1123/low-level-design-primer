# Distributed ID Generation — Twitter Snowflake
> Generate globally unique, sortable 64-bit IDs across multiple nodes without coordination.

---

## The Snowflake ID Structure

```
 63        22        12         0
  |<─ 41 ─>|<── 10 ──>|<── 12 ──>|
  timestamp  node_id    sequence
  (ms since  (machine)  (per-ms
   epoch)                counter)
```

| Segment | Bits | Range | Purpose |
|---|---|---|---|
| Timestamp | 41 | ~69 years | Milliseconds since custom epoch |
| Node ID | 10 | 0–1023 | Identifies the generating machine |
| Sequence | 12 | 0–4095 | Counter within same millisecond |

**Result:** 4096 unique IDs/ms/node × 1024 nodes = **4M IDs/ms globally**

---

## Core Entities

| Class/Interface | Role |
|---|---|
| `IdGenerator` *(interface)* | `generateId() → String` — UUID variant |
| `SequenceIdGenerator` *(interface)* | `generateId() → long` — Snowflake variant |
| `SnowflakeSequenceIdGenerator` | Main impl — bit-shifts timestamp + nodeId + sequence |
| `UUIDGenerator` | Simple `IdGenerator` impl using `java.util.UUID` |
| `BeanDefinition` | Spring config — injects `generatingNodeId` |

---

## Relationships

```
IdGenerator  (interface)
 └── UUIDGenerator

SequenceIdGenerator  (interface)
 └── SnowflakeSequenceIdGenerator
      ├── generatingNodeId  (injected by Spring)
      ├── lastTimestamp     (volatile long)
      └── currentSequence   (volatile long)
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `IdGenerator` vs `SequenceIdGenerator` — swap UUID ↔ Snowflake |
| **Singleton (via Spring)** | `@Service` on generator — one instance per node |
| **Template Method** | `generateId()` handles clock drift, sequence overflow consistently |

---

## Key Flow — Generate One Snowflake ID

```
synchronized(lock):
1. currentTs = now() - EPOCH_START
2. if currentTs < lastTimestamp → ClockMovedBackException
3. if currentTs == lastTimestamp:
   ├── sequence++ (mod 4096)
   └── if overflow → wait until next millisecond
4. else → sequence = 0
5. lastTimestamp = currentTs
6. id = (currentTs << 22) | (nodeId << 12) | sequence
7. return id
```

---

## Guarantees & Constraints
- **Monotonically increasing** — sortable by creation time
- **No coordination** — nodes generate IDs independently
- **Clock drift protection** — throws `ClockMovedBackException` on backwards clock
- **Node ID bounds** — validated at startup via `@PostConstruct`
- Thread-safe via `synchronized(lock)` block
