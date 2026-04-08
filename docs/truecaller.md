# TrueCaller — Caller ID & Spam Detection
> Identify callers, manage contacts with search, block spam, and support business profiles.

---

## Core Entities

| Class | Role |
|---|---|
| `Account` *(abstract)* | Base for all account types — phone number, account status |
| `User` | Individual user — personal info, contacts, blocked list, social profiles |
| `Business` | Business account — operating hours, address, category, size |
| `Contact` | A phone entry — name, number, tags, spam flag |
| `PersonalInfo` | Name, DOB, gender, email |
| `SocialInfo` | Social profiles map (`SocialProfileType → URL`) |
| `GlobalSpam` | Central spam registry — number → spam count |
| `GlobalContacts` | App-wide contact directory — all known numbers |
| `Tag` | User-defined labels on contacts |

---

## Enums

| Enum | Values |
|---|---|
| `UserCategory` | `SILVER / GOLD / PLATINUM` (subscription tier) |
| `BusinessSize` | `SMALL / MEDIUM / LARGE` |
| `SocialProfileType` | `FACEBOOK / TWITTER / LINKEDIN` etc. |
| `Gender` | `MALE / FEMALE / OTHER` |
| `Days` | Mon–Sun (for business hours) |

---

## Trie — Contact Search

```
ContactTrie
 └── TrieNode (root)
      └── children: Map<Character, TrieNode>
               └── ... → leaf holds Contact reference

Search by prefix → O(k) where k = prefix length
```
Used for **"search as you type"** — e.g. typing "Jo" finds all contacts starting with "Jo".

---

## Relationships

```
User (extends Account)
 ├── PersonalInfo
 ├── SocialInfo
 ├── UserCategory
 ├── List<Contact>       (personal contacts)
 ├── Set<String>         (blocked numbers)
 └── ContactTrie         (per-user fast search)

Business (extends Account)
 ├── Address
 ├── Map<Days, OperatingHours>
 └── BusinessSize

GlobalContacts           (singleton/static)
 └── Map<phoneNumber, Contact>

GlobalSpam               (singleton/static)
 └── Map<phoneNumber, spamCount>
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Trie (data structure)** | `ContactTrie` / `TrieNode` — O(k) prefix search |
| **Singleton** | `GlobalContacts`, `GlobalSpam` — app-wide registries |
| **Inheritance** | `Account → User / Business` — shared phone/status logic |
| **Strategy (implicit)** | Spam threshold configurable via `Constant` class |

---

## Key Flows

**Incoming Call Identification:**
```
1. Caller number received
2. Check User's personal contacts → show saved name if found
3. Else check GlobalContacts → show crowd-sourced name
4. Check GlobalSpam → if spamCount > threshold → show "SPAM"
5. Check User's blocked list → silently reject if blocked
```

**Contact Search (Trie):**
```
1. User types prefix in search box
2. ContactTrie.search(prefix) → traverse TrieNode path
3. Return all contacts at and below that node
```

---

## Constraints / Rules
- `Constant.MAX_CONTACTS` — cap on personal contacts per user
- `Constant.MAX_BLOCK_LIMIT` — max numbers a user can block
- Spam is crowd-sourced: any user can mark a number as spam → `GlobalSpam` incremented
