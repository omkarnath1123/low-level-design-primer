# LRU Cache System
> Generic key-value cache with pluggable eviction policy and storage backend.

---

## Core Entities

| Class | Role |
|---|---|
| `Cache<K,V>` | Facade — coordinates storage + eviction policy |
| `HashMapBasedStorage<K,V>` | `Storage` impl — `HashMap` with fixed capacity |
| `LRUEvictionPolicy<K>` | `EvictionPolicy` impl — doubly-linked list + hashmap |
| `DoublyLinkedList<E>` | Custom DLL — O(1) add-to-tail + detach any node |
| `DoublyLinkedListNode<E>` | DLL node with `prev` / `next` pointers |

---

## Key Interfaces

| Interface | Methods | Purpose |
|---|---|---|
| `EvictionPolicy<K>` | `keyAccessed(K)`, `evictKey()` | Decide which key to evict |
| `Storage<K,V>` | `add`, `get`, `remove` | Key-value store abstraction |

---

## Relationships

```
Cache
 ├── EvictionPolicy<K>  (interface)
 │    └── LRUEvictionPolicy  ←  DoublyLinkedList + HashMap<K, Node>
 └── Storage<K,V>       (interface)
      └── HashMapBasedStorage  ←  HashMap + capacity limit
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `EvictionPolicy` — swap LRU for LFU/MRU without touching `Cache` |
| **Strategy** | `Storage` — swap HashMap for concurrent/disk storage |
| **Factory** | `CacheFactory.defaultCache(capacity)` — wires LRU + HashMap together |
| **Template Method** | `Cache.put()` — evicts then retries on `StorageFullException` |

---

## LRU Mechanics

```
keyAccessed(key):
  if key exists → detach node from DLL, re-add at tail (most-recent)
  if new key    → add node at tail, store in hashmap

evictKey():
  return & remove DLL.firstNode (least-recently-used = head)
```

**Why DLL + HashMap?**
- DLL → O(1) arbitrary node removal (with prev/next pointers)
- HashMap → O(1) node lookup by key
- Together → O(1) access and eviction

---

## Key Flow — Cache Put

```
1. storage.add(key, value)
   ├── OK      → evictionPolicy.keyAccessed(key)
   └── StorageFullException
         → evictionPolicy.evictKey() → keyToRemove
         → storage.remove(keyToRemove)
         → retry put(key, value)  ← recursive
```

---

## Exceptions

| Exception | When |
|---|---|
| `StorageFullException` | Storage at capacity and eviction needed |
| `NotFoundException` | `get()` on a key that doesn't exist |
