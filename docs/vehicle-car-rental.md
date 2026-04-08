# Vehicle Car Rental — Zoomcar
> Search, reserve, and invoice vehicle rentals with add-ons across multiple pricing models.

---

## Core Entities

| Class | Role |
|---|---|
| `HireableVehicle` *(abstract)* | Rentable vehicle — type, category, status, daily/hourly/monthly costs |
| `VehicleReservation` | A booking — account, vehicle, dates, type, status, add-ons |
| `Invoice` | Final bill — base cost + addon costs |
| `RentalLocation` | Pickup/drop location with coordinates |
| `VehicleInventory` | Vehicle availability state |
| `VehicleLog` | Audit log of vehicle events |

### Vehicle Hierarchy

```
HireableVehicle (abstract)
 ├── Car
 ├── Bike
 └── Van
```

### Account Hierarchy

```
Account (abstract)
 ├── User    (standard renter)
 ├── Driver  (chauffeur) + LicenseInfo
 └── Admin   (fleet manager)
```

### Add-On Hierarchy (Strategy Pattern)

```
AddonService (abstract/interface)
 ├── Chauffeur
 ├── ChildSeat
 ├── DeepCleaning
 ├── Insurance
 ├── Navigation
 ├── OnDemandTowing
 ├── PassengerScreen
 ├── ReservationReminder
 └── WiFi
```

---

## Key Services & Interfaces

| Interface | Implementations | Purpose |
|---|---|---|
| `InvoiceService` | `DayInvoiceService`, `HourInvoiceService`, `MonthInvoiceService`, `PackageInvoiceServiceImpl` | Calculate base rental cost by pricing type |
| `AccountService` | `AccountServiceImpl` | Account CRUD |
| `VehicleReservationService` | `VehicleReservationServiceImpl` | Book, cancel, modify reservations |
| `VehicleSearchService` | `VehicleSearchServiceImpl` | Search available vehicles by filters |
| `VehicleService` | `VehicleServiceImpl` | Fleet management |
| `BookingReminderService` | `BookingReminderServiceImpl` | Upcoming reservation reminders |
| `InvoiceNotificationService` | `InvoiceNotificationServiceImpl` | Send invoice to user |

---

## Relationships

```
VehicleReservation
 ├── Account (user/driver)
 ├── HireableVehicle
 ├── RentalLocation (pickup + drop)
 ├── VehicleReservationType (HOURLY / DAILY / MONTHLY / PACKAGE)
 ├── ReservationStatus
 └── List<AddonService>

Invoice
 ├── VehicleReservation
 ├── VehicleFixedCosts + VehicleDailyCosts (etc.)
 └── addon costs (AddonCostUtil)

AccountRepository (Factory)
 └── AccountRepositoryFactory → UserRepository / DriverRepository / AdminRepository
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `InvoiceService` — swap daily/hourly/monthly calculation |
| **Factory** | `InvoiceServiceFactory` — returns correct `InvoiceService` for reservation type |
| **Factory** | `AccountRepositoryFactory` — returns correct repo for account type |
| **Observer** | `InvoiceNotificationService` — notifies user after invoice generated |
| **Decorator (implicit)** | `AddonService` adds cost on top of base invoice |
| **Repository** | Per-aggregate repos for accounts, vehicles, reservations |

---

## Key Flow — Make a Reservation

```
1. VehicleSearchService.search(location, dates, type) → available vehicles
2. User selects vehicle + add-ons + reservation type
3. VehicleReservationService.createReservation(...)
   → VehicleReservation{status=PENDING}
   → VehicleInventory updated
4. InvoiceServiceFactory.getService(reservationType) → InvoiceService
5. InvoiceService.generateInvoice() = baseCost + AddonCostUtil.calculate(addons)
6. InvoiceNotificationService.notify(user, invoice)
7. ReservationStatus = CONFIRMED
```

---

## Pricing Types

| Type | Basis |
|---|---|
| `HOURLY` | Per hour × `VehicleHourlyCosts` |
| `DAILY` | Per day × `VehicleDailyCosts` |
| `MONTHLY` | Per month × `VehicleMonthlyCosts` |
| `PACKAGE` | Fixed package price |
