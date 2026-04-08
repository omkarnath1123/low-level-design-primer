# Food Delivery — Uber Eats / DoorDash / Swiggy
> Browse restaurants, manage cart, place orders, track delivery, handle payments with pricing strategies.

---

## Core Entities

| Class | Role |
|---|---|
| `Restaurant` | Vendor — has `FoodMenu`, location, cuisine type |
| `FoodMenu` | Menu for a restaurant — list of `MenuItem`s |
| `MenuItem` | One dish — name, price, meal type, cuisine |
| `User` | Customer — address, order history |
| `Order` | Placed order — user, restaurant, items, status, bill |
| `OrderStatus` | `PLACED → ACCEPTED → PREPARING → OUT_FOR_DELIVERY → DELIVERED / CANCELLED` |
| `Delivery` | Delivery partner assignment + tracking |
| `Bill` | Final amount after pricing strategy applied |
| `Payment` | Payment record — type, status |
| `CouponCode` | Discount code applied at checkout |

---

## Data Stores (in-memory maps)

| Store | Holds |
|---|---|
| `RestaurantData` | All restaurants |
| `FoodMenuData` | Menus per restaurant |
| `CartData` | Active cart per user |
| `OrderData` | All orders |
| `DeliveryData` | Delivery assignments |
| `PaymentData` | Payment records |
| `UserData` | Registered users |

---

## Key Services

| Service | Responsibility |
|---|---|
| `RestaurantService` | Register/search restaurants |
| `FoodMenuService` | Manage menu items |
| `CartService` | Add/remove items; validates restaurant consistency |
| `OrderService` | Place, cancel, track orders |
| `PricingService` | Apply coupon → pick correct `PricingStrategy` → generate `Bill` |
| `DeliveryService` | Assign delivery partner, update status |
| `PaymentService` | Process payment |
| `UserService` | Register/manage users |

---

## Key Interfaces

| Interface | Implementations | Purpose |
|---|---|---|
| `PricingStrategy` | `TwentyPercentOffPricingStrategy`, `FiveHundredOffPricingStrategy` | Calculate bill given coupon |
| `CartCommandExecutor` *(abstract)* | `AddCartCommandExecutor`, `RemoveCartCommandExecutor` | Command pattern for cart mutations |
| `OrderCommandExecutor` *(abstract)* | `PlaceOrderCommandExecutor`, `CancelOrderCommandExecutor` | Command pattern for order operations |

---

## Relationships

```
User  →  Cart  →  Restaurant + List<MenuItem>
Order →  User + Restaurant + List<MenuItem> + Bill + OrderStatus
Bill  →  totalAmount (post-pricing)
Payment → Order + PaymentType + PaymentStatus

PricingService
 └── List<PricingStrategy> → isApplicable(coupon) → generateBill(items)
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `PricingStrategy` — plug in any discount/pricing rule |
| **Command** | `CartCommandExecutor` / `OrderCommandExecutor` — operations as objects; extensible without modification |
| **Repository (Data classes)** | `CartData`, `OrderData` etc. — centralised in-memory stores |
| **MVC** | Controllers → Services → DataStores; thin controllers |
| **Chain of Responsibility** | `PricingService` iterates strategies, picks first `isApplicable` |

---

## Key Flow — Place an Order

```
1. User browses RestaurantService → picks restaurant
2. CartService.add(userId, restaurantId, itemId)
   → AddCartCommandExecutor validates item exists → adds to CartData
3. PricingService.generateBill(cart, couponCode)
   → iterate PricingStrategies → find isApplicable(coupon) → Bill
4. OrderService.placeOrder(userId, restaurantId) → Order{status=PLACED}
5. DeliveryService assigns delivery partner
6. PaymentService.processPayment(orderId, paymentType)
7. Order state machine: PLACED → ACCEPTED → PREPARING → DELIVERED
```

---

## Enums Summary

| Enum | Values |
|---|---|
| `OrderStatus` | `PLACED, ACCEPTED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED` |
| `PaymentStatus` | `PENDING / SUCCESS / FAILED` |
| `PaymentType` | `CASH / UPI / CARD / WALLET` |
| `CuisineType` | `INDIAN / CHINESE / ITALIAN ...` |
| `MealType` | `BREAKFAST / LUNCH / DINNER / SNACK` |
| `CartCommandType` | `ADD / REMOVE` |
| `OrderCommandType` | `PLACE / CANCEL` |
