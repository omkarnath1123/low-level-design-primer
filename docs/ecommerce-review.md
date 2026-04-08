# E-Commerce Review System
> Collect, moderate, and summarise product reviews with sentiment and feature-level feedback.

---

## Core Entities

| Class | Role |
|---|---|
| `Product` | The item being reviewed — id, name, category |
| `Review` | A user's feedback — rating, text, type, sentiment, state |
| `ReviewType` | `TEXT / PHOTO / VIDEO` |
| `ReviewSentiment` | `POSITIVE / NEGATIVE / NEUTRAL` |
| `ReviewState` | `CREATED → APPROVED / REJECTED` (moderation lifecycle) |
| `Feature` | Named aspect of a product (e.g. "battery", "camera") |
| `Meta` | Review metadata — verified purchase, helpful votes, etc. |
| `User` | Reviewer — profile, location, verification status |
| `UserProfile` | Bio, activity stats |
| `VerificationStatus` | `VERIFIED / UNVERIFIED` |

---

## Summary Aggregation

| Class | Role |
|---|---|
| `ReviewSummary` | Aggregate stats for a product — avg rating, total count, sentiment breakdown |
| `FeatureSummary` | Per-feature average rating and sentiment |
| `Summary` *(interface)* | Common summary contract |

---

## Notification (Observer)

| Class | Role |
|---|---|
| `UserEmailNotifier` *(interface)* | `notify(user, state)` |
| `UserEmailNotifierImpl` | Sends email when review state changes |
| `UserEmailNotification` | Email content builder |
| `NotificationState` | Enum of notification triggers (`REVIEW_APPROVED`, `REVIEW_REJECTED`, etc.) |

---

## Relationships

```
Product
 └── (reviews via ReviewRepository)

Review
 ├── User
 ├── Product
 ├── ReviewType
 ├── ReviewSentiment
 ├── ReviewState
 ├── List<Feature>    (which features does review mention?)
 └── Meta

User
 ├── UserProfile
 ├── UserLocation
 └── VerificationStatus

ReviewSummary
 ├── Product
 └── List<FeatureSummary>
```

---

## Repositories

| Repository | Stores |
|---|---|
| `ProductRepository` | `Map<productId, Product>` |
| `ReviewRepository` | `Map<reviewId, Review>` |
| `UserRepository` | `Map<userId, User>` |

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Observer** | `UserEmailNotifier` — notified when review changes state |
| **State Machine** | `ReviewState`: CREATED → APPROVED / REJECTED |
| **Repository** | Clean data access per aggregate |
| **Strategy (implicit)** | `Summary` interface — swap summary calculation strategy |

---

## Key Flows

**Submit a Review:**
```
1. User submits Review{type, rating, text, features}
2. Review saved with state=CREATED
3. Moderation: review → APPROVED or REJECTED
4. UserEmailNotifierImpl.notify(user, notificationState)
5. ReviewSummary recalculated for product
```

**Get Product Summary:**
```
1. ReviewRepository.getByProduct(productId)
2. Aggregate: avg rating, sentiment counts, per-feature breakdowns
3. Return ReviewSummary with FeatureSummary list
```
