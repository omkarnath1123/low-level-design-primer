# Distributed Systems Pattern Revision Notes

This repo is primarily focused on low-level design, but staff engineer interviews often expect you to connect LLD decisions to distributed systems concerns.

## Topics to revise alongside LLD
- id generation
- caching and eviction
- concurrency control
- pub-sub and eventing
- retries and idempotency
- rate limiting
- partitioning and sharding
- consistency tradeoffs
- leader election and coordination
- observability and failure handling

## Where this appears in this repo
- `problem-statements/pub-sub-system.md`
- `problem-statements/digital-wallet-service.md`
- `problem-statements/online-shopping-service.md`
- `problem-statements/ride-sharing-service.md`
- `problem-statements/food-delivery-service.md`
- `problem-statements/traffic-signal.md`
- `reference-implementations/java/`

## How to answer better in interviews
When given an LLD problem, explicitly call out:
1. local object model
2. concurrency boundaries
3. persistence assumptions
4. scale risks
5. operational risks
6. what you are intentionally not modeling due to interview scope

## Rule of thumb
A strong staff-level answer connects:
- clean class design
- domain boundaries
- failure modes
- extensibility
- operational realism

Without turning a 45-minute LLD round into an accidental PhD thesis.
