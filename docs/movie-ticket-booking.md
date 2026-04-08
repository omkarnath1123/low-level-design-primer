# Movie Ticket Booking
> Search movies, book seats with time-limited locks, process payments, manage theatres.

---

## Core Entities

| Class | Role |
|---|---|
| `Theatre` | Venue — has multiple `Screen`s |
| `Screen` | Projection room — has `Seat`s, hosts `Show`s |
| `Movie` | Film metadata — title, duration, language |
| `Show` | Screening — movie × screen × time slot |
| `Seat` | A physical seat in a screen |
| `Booking` | Reservation — show, seats, user, payment, status |
| `BookingStatus` | `CREATED → CONFIRMED / CANCELLED` |
| `SeatLock` | Temporary hold on a seat — user, show, expiry time |

---

## Key Services

| Service | Responsibility |
|---|---|
| `TheatreService` | Add/query theatres and screens |
| `MovieService` | Add/query movies |
| `ShowService` | Create shows; find shows for a movie |
| `SeatAvailabilityService` | Which seats are free for a given show |
| `BookingService` | Create booking, lock seats, confirm, cancel |
| `PaymentsService` | Process payment for a booking |

---

## Seat Locking (Interface + Provider)

| Class | Role |
|---|---|
| `SeatLockProvider` *(interface)* | `lockSeat(show, seat, user, duration)`, `validateLock(show, seat, user)`, `unlockSeat(...)` |
| `InMemorySeatLockProvider` | Map-based lock store — expires after timeout |

---

## REST Controllers

| Controller | Endpoints |
|---|---|
| `TheatreController` | POST /theatre, GET /theatre/{id}/shows |
| `MovieController` | POST /movie, GET /movie/{id}/shows |
| `ShowController` | POST /show, GET /show/{id}/seats |
| `BookingController` | POST /booking, POST /booking/{id}/confirm |
| `PaymentsController` | POST /payment |

---

## Relationships

```
Theatre → List<Screen>
Screen  → List<Seat>
Show    → Movie + Screen + startTime

Booking
 ├── Show
 ├── List<Seat> (booked)
 ├── String user
 ├── Payment
 └── BookingStatus

SeatLock (per show+seat)
 ├── Seat
 ├── Show
 ├── lockedBy (user)
 └── lockedAt + lockDuration
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Interface + Provider** | `SeatLockProvider` — pluggable locking (in-memory, Redis, DB) |
| **Service Layer** | Each domain area has its own service; controllers are thin |
| **State Machine** | `BookingStatus`: CREATED → CONFIRMED / CANCELLED |
| **Optimistic Locking** | `SeatLock` with TTL prevents double-booking without DB transactions |

---

## Key Flow — Book a Seat

```
1. User queries ShowService → available shows
2. SeatAvailabilityService.getAvailableSeats(show) → unbooked, unlocked seats
3. BookingService.createBooking(show, seats, user)
   → SeatLockProvider.lockSeat(show, seat, user, 10min) per seat
   → Booking{status=CREATED}
4. User completes PaymentsService.processPayment(booking)
5. BookingService.confirmBooking(booking, user)
   → validates all seat locks still held by user
   → Booking{status=CONFIRMED}
```

---

## Exceptions

| Exception | When |
|---|---|
| `SeatTemporaryUnavailableException` | Seat locked by another user |
| `SeatPermanentlyUnavailableException` | Seat already confirmed-booked |
| `ScreenAlreadyOccupiedException` | Screen has overlapping show time |
| `InvalidStateException` | e.g. confirm already-cancelled booking |
