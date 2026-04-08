# LLD Docs — Kulfi's Design Notes

Concise LLD breakdowns for every solution in this repo.
Each doc covers: **entities · relationships · interfaces · design patterns · key flows**.

---

## Index

| Problem | Doc | Patterns Highlighted |
|---|---|---|
| Cab Booking (Uber/Ola) | [cab-booking.md](cab-booking.md) | Strategy, Repository |
| LRU Cache | [lru-cache.md](lru-cache.md) | Strategy, Factory, DLL+HashMap |
| Snake and Ladder | [snake-and-ladder.md](snake-and-ladder.md) | Simple OOP |
| Chess Game | [chess-game.md](chess-game.md) | Strategy, Chain of Responsibility, Template Method, Command |
| Distributed ID (Snowflake) | [distributed-id-snowflake.md](distributed-id-snowflake.md) | Strategy, Singleton, bit-masking |
| Amazon Locker | [amazon-locker.md](amazon-locker.md) | Repository, Service Layer, Factory |
| Bill Sharing (Splitwise) | [bill-sharing-splitwise.md](bill-sharing-splitwise.md) | Strategy, Builder, Repository |
| Parking Lot | [parking-lot.md](parking-lot.md) | Composite, Polymorphism, Repository |
| TrueCaller | [truecaller.md](truecaller.md) | Trie, Singleton, Inheritance |
| Vehicle Car Rental (Zoomcar) | [vehicle-car-rental.md](vehicle-car-rental.md) | Strategy, Factory, Observer, Repository |
| E-Commerce Review | [ecommerce-review.md](ecommerce-review.md) | Observer, State Machine, Repository |
| Logger | [logger.md](logger.md) | Strategy, DI (Guice), Facade |
| Movie Ticket Booking | [movie-ticket-booking.md](movie-ticket-booking.md) | Provider/Interface, State Machine, Optimistic Locking |
| Food Delivery (Uber Eats) | [food-delivery.md](food-delivery.md) | Strategy, Command, Chain of Responsibility, MVC |
| Cricket Info / CricScore | [cricket-info.md](cricket-info.md) | Composite, Inheritance, Value Objects |

---

## How to Read These Docs

Each doc follows this structure:

```
# Problem Name
> One-liner problem statement

## Core Entities      ← what the main classes are and what they do
## Relationships      ← HAS-A / IS-A trees
## Key Interfaces     ← contracts + implementations
## Design Patterns    ← pattern → where it's applied
## Key Flow           ← numbered sequence for the primary use case
## Constraints/Rules  ← edge cases and invariants
```

---

## Quick Pattern Reference

| Pattern | Used in |
|---|---|
| **Strategy** | Cache, Cab, Chess, Snowflake, Car Rental, Food Delivery, Logger |
| **Factory** | Cache, Car Rental, Chess, Amazon Locker |
| **Repository** | Cab, Locker, Bill Sharing, Parking Lot, Car Rental, Review, Movie |
| **Observer** | Car Rental, Review, Bill Sharing |
| **Command** | Chess, Food Delivery |
| **Template Method** | Chess, Cache |
| **Composite** | Parking Lot, Cricket |
| **State Machine** | Movie Booking, Review, Food Delivery |
| **Trie** | TrueCaller |
| **Dependency Injection** | Logger (Guice), Snowflake (Spring) |
