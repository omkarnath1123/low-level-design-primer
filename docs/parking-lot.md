# Parking Lot System

Two solutions exist — a simpler sketch and a full production model.

---

## Solution 1 — Lightweight Sketch

**Entities:** `Vehicle` (abstract) → `Car`, `MotorBike`, `Van`, `Truck`, `ElectricCar`, `ElectricMotorBike`  
**Enums:** `VehicleType`, `PaymentStatus`, `TicketStatus`  
**Repositories:** `ParkingLotRepository`, `AdminRepository` (static maps)

> Captures the type hierarchy and status enums. No floor/spot/ticket logic implemented — starter skeleton.

---

## Solution 2 — Full Model

### Core Entities

| Class | Role |
|---|---|
| `ParkingLot` | Top-level aggregate — has floors, entrance/exit panels |
| `ParkingFloor` | One level — has multiple parking spots |
| `ParkingSpot` *(abstract)* | One space — type, status, parked vehicle |
| `ParkingTicket` | Issued on entry — vehicle, spot, timestamps |
| `Payment` | Payment record linked to ticket |
| `PaymentPortal` | Physical payment kiosk on a floor |
| `EntrancePanel` | Entry point — issues tickets |
| `ExitPanel` | Exit point — processes payment |

### Vehicle Hierarchy

```
Vehicle (abstract)
 ├── Car
 ├── MotorBike
 ├── Van
 ├── Truck
 ├── ElectricCar       (needs charging spot)
 └── ElectricMotorBike (needs charging spot)
```

### Parking Spot Hierarchy

```
ParkingSpot (abstract)
 ├── CarParkingSpot
 ├── AbledCarParkingSpot    (handicapped)
 ├── MotorBikeParkingSpot
 ├── ElectricCarParkingSpot
 ├── ElectricBikeParkingSpot
 └── LargeVehicleParkingSpot
```

### Account Hierarchy

```
Account (abstract)
 └── Admin  ← manages lot, floors, spots
     ├── Person → PersonalInfo
     └── Contact + Address
```

---

## Relationships

```
ParkingLot
 ├── List<ParkingFloor>
 │    ├── List<ParkingSpot>
 │    └── PaymentPortal
 ├── EntrancePanel
 └── ExitPanel

ParkingTicket
 ├── Vehicle
 ├── ParkingSpot
 ├── entryTime / exitTime
 └── TicketStatus (ACTIVE / PAID / LOST)

Payment
 ├── ParkingTicket
 ├── amount
 └── PaymentStatus (UNPAID / COMPLETED / CANCELLED)
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Composite** | `ParkingLot → ParkingFloor → ParkingSpot` hierarchy |
| **Type Hierarchy (Polymorphism)** | `ParkingSpot` subtypes match `Vehicle` subtypes |
| **Repository** | `ParkingLotRepository`, `AdminRepository` — central data access |
| **Separation of Concerns** | Entrance/Exit panels are separate from the lot itself |

---

## Key Flow — Park a Vehicle

```
1. Vehicle arrives at EntrancePanel
2. Find available ParkingSpot matching VehicleType on any floor
3. Create ParkingTicket{vehicle, spot, entryTime, status=ACTIVE}
4. Mark spot as OCCUPIED
5. On exit → ExitPanel calculates fee (HourlyCost)
6. Payment processed at PaymentPortal
7. Ticket status = PAID; spot = AVAILABLE
```

---

## Pricing
- `HourlyCost` — fee calculated from `entryTime` to `exitTime`
- Different rates possible per `ParkingSpotType`
