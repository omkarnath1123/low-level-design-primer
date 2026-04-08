# Cab Booking — Uber / Ola
> Match riders with nearby available cabs, price the trip, and track trip lifecycle.

---

## Core Entities

| Class | Role |
|---|---|
| `Rider` | User requesting a cab — has id, location |
| `Cab` | A vehicle with driver — tracks location, availability, currentTrip |
| `Trip` | One ride: rider ↔ cab, from→to, price, status |
| `Location` | 2D coordinate with `distance()` helper |
| `TripStatus` | `REQUESTED → IN_PROGRESS → ENDED` |

---

## Key Managers (In-Memory DB)

| Manager | Responsibility |
|---|---|
| `RidersManager` | CRUD for riders |
| `CabsManager` | CRUD + location/availability updates + proximity search |
| `TripsManager` | Orchestrates trip creation, history, end-trip |

---

## Key Interfaces

| Interface | Purpose | Implementations |
|---|---|---|
| `CabMatchingStrategy` | Pick best cab from candidates | `DefaultCabMatchingStrategy` (first available) |
| `PricingStrategy` | Calculate trip fare | distance-based impl |

---

## Relationships

```
Rider ──────────── Trip ──────────── Cab
  1            *         *         1
              (currentTrip on Cab = active trip)
```

- `TripsManager` HAS-A `CabsManager`, `RidersManager`, `CabMatchingStrategy`, `PricingStrategy`
- `Cab` HAS-A `Trip` (current active trip, null if available)

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `CabMatchingStrategy` — swap matching algo without changing `TripsManager` |
| **Strategy** | `PricingStrategy` — surge, flat-rate, etc. are plug-and-play |
| **Repository** | `CabsManager` / `RidersManager` / `TripsManager` — in-memory data stores |

---

## Key Flow — Book a Cab

```
1. Rider requests trip (from, to)
2. CabsManager.getCabs(from, MAX_DISTANCE) → nearby cabs
3. Filter: cab.currentTrip == null (available)
4. CabMatchingStrategy.matchCabToRider() → selectedCab
5. PricingStrategy.findPrice(from, to) → price
6. new Trip(rider, cab, price) created; cab.currentTrip = trip
7. Trip ends → cab.currentTrip = null, trip status = ENDED
```

---

## Constraints / Rules
- Search radius: `MAX_ALLOWED_TRIP_MATCHING_DISTANCE = 10.0` units
- A cab can only have **one active trip** at a time
- `NoCabsAvailableException` thrown if no match found
