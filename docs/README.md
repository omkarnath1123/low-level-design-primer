# LLD docs

This directory contains focused low-level design notes, study guides, and
interview support documents.

Use these docs to complement the raw problem statements and implementations in
other parts of the repository.

## Start with these docs
- [lld-handbook.md](lld-handbook.md): main LLD study guide
- [lld-handbook-dark.md](lld-handbook-dark.md): dark-style reading variant
- [interview-answer-template.md](interview-answer-template.md): reusable answer structure
- [uml-cheatsheet.md](uml-cheatsheet.md): quick UML and class-diagram guide

## Problem-specific notes

| Problem | Doc | Patterns highlighted |
|---|---|---|
| Cab booking | [cab-booking.md](cab-booking.md) | Strategy, Repository |
| LRU cache | [lru-cache.md](lru-cache.md) | Strategy, Factory, DLL + HashMap |
| Snake and ladder | [snake-and-ladder.md](snake-and-ladder.md) | Simple OOP |
| Chess game | [chess-game.md](chess-game.md) | Strategy, Chain of Responsibility, Template Method, Command |
| Distributed ID | [distributed-id-snowflake.md](distributed-id-snowflake.md) | Strategy, Singleton, bit masking |
| Amazon locker | [amazon-locker.md](amazon-locker.md) | Repository, Service Layer, Factory |
| Bill sharing | [bill-sharing-splitwise.md](bill-sharing-splitwise.md) | Strategy, Builder, Repository |
| Parking lot | [parking-lot.md](parking-lot.md) | Composite, Polymorphism, Repository |
| Truecaller | [truecaller.md](truecaller.md) | Trie, Singleton, Inheritance |
| Car rental | [vehicle-car-rental.md](vehicle-car-rental.md) | Strategy, Factory, Observer, Repository |
| E-commerce review | [ecommerce-review.md](ecommerce-review.md) | Observer, State Machine, Repository |
| Logger | [logger.md](logger.md) | Strategy, DI, Facade |
| Movie ticket booking | [movie-ticket-booking.md](movie-ticket-booking.md) | Provider interface, State Machine, Optimistic Locking |
| Food delivery | [food-delivery.md](food-delivery.md) | Strategy, Command, Chain of Responsibility, MVC |
| Cricket info | [cricket-info.md](cricket-info.md) | Composite, Inheritance, Value Objects |

## How to read these docs

Most problem notes follow this structure:
- core entities
- relationships
- key interfaces
- design patterns
- key flow
- constraints and invariants

Use them in this order:
1. read the problem statement first
2. sketch your own model
3. compare it with the problem note
4. compare both against implementations and diagrams

## Related repository sections
- `../problem-statements/`
- `../assets/class-diagrams/`
- `../reference-implementations/java/`
- `../solutions/`
- `../study-roadmap/`
