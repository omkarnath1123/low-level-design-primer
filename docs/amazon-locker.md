# Amazon Locker Service
> Match delivered packages to available lockers; notify customers; handle pickup and returns.

---

## Core Entities

| Class | Role |
|---|---|
| `Locker` | A physical compartment — id, size, status, locationId |
| `LockerSize` | `SMALL / MEDIUM / LARGE / EXTRA_LARGE` |
| `LockerStatus` | `AVAILABLE / BOOKED` |
| `LockerPackage` | Joins locker ↔ order; holds pickup code + expiry |
| `LockerLocation` | Physical site — geo-location, operating hours, list of lockers |
| `Order` | Customer order with items and package info |
| `Pack` | Physical parcel — size determines locker size needed |
| `Item` | Individual product in an order |
| `Notification` | Pickup notification sent to customer |
| `GeoLocation` | lat/lng for proximity matching |

---

## Key Services

| Service | Responsibility |
|---|---|
| `LockerService` | Find available locker by size+location; validate pickup code; handle expiry |
| `DeliveryService` | Assign locker to a package on delivery |
| `CustomerService` | Customer-facing pickup flow |
| `OrderService` | Order lifecycle |
| `NotificationService` | Send pickup code to customer |
| `ReturnsService` | Handle package returns to locker |

---

## Repositories (Static In-Memory Maps)

| Repository | Stores |
|---|---|
| `LockerRepository` | `Map<lockerId, Locker>` + proximity search |
| `LockerLocationRepository` | `Map<locationId, LockerLocation>` |
| `LockerPackageRepository` | `Map<lockerId, LockerPackage>` |
| `OrderRepository` | `Map<orderId, Order>` |
| `NotificationRepository` | Notification records |

---

## Relationships

```
LockerLocation
 ├── GeoLocation
 ├── LocationTiming → Map<DayOfWeek, Timing(open, close)>
 └── List<Locker>
         ├── LockerSize
         └── LockerStatus

LockerPackage
 ├── Locker (1-to-1)
 ├── Order
 └── pickupCode + expiryDateTime

Order → List<Item>
Pack  → LockerSize (determined by SizeUtil)
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Repository** | Static map-based repos per aggregate |
| **Factory/Util** | `SizeUtil` — maps package dimensions → `LockerSize` |
| **Service Layer** | All business logic isolated in service classes |

---

## Key Flows

**Delivery Flow:**
```
1. Delivery arrives → Pack.size determined by SizeUtil
2. LockerService.getLocker(size, geoLocation) → nearest available locker
3. locker.status = BOOKED; LockerPackage created with code + expiry
4. NotificationService sends code to customer
```

**Pickup Flow:**
```
1. Customer provides lockerId + code
2. LockerService.pickFromLocker(lockerId, code, now)
3. Validate: code matches, not expired, within opening hours
4. locker.status = AVAILABLE; code cleared
```

---

## Exceptions

| Exception | When |
|---|---|
| `LockerNotFoundException` | No locker for that id/package |
| `LockeCodeMisMatchException` | Wrong pickup code entered |
| `PickupCodeExpiredException` | Code past expiry date |
| `PackPickTimeExceededException` | Outside locker opening hours |
| `PackageSizeMappingException` | Package too big for any locker |
