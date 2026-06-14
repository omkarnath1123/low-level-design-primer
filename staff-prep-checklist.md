# Staff Engineer LLD and System Design Prep Checklist

Use this checklist to prepare for senior and staff-level low-level design interviews.

## 1. Fundamentals and design judgment
- [ ] I can explain abstraction, encapsulation, inheritance, composition, and polymorphism with tradeoffs.
- [ ] I can explain when composition is better than inheritance.
- [ ] I can explain SOLID without forcing every pattern into every answer.
- [ ] I can explain DRY, YAGNI, and KISS with concrete examples.
- [ ] I can identify over-engineering quickly.

## 2. Problem framing
- [ ] I start by clarifying scope, assumptions, constraints, and non-goals.
- [ ] I separate must-have requirements from nice-to-have requirements.
- [ ] I can identify core entities, relationships, workflows, and invariants.
- [ ] I can say what I am intentionally not modeling due to time limits.

## 3. Object model and domain boundaries
- [ ] I can derive classes from business requirements instead of guessing patterns first.
- [ ] I can identify aggregates and ownership boundaries.
- [ ] I can define interfaces where behavior actually varies.
- [ ] I can keep responsibilities cohesive and avoid god objects.
- [ ] I can distinguish domain model, orchestration logic, and infrastructure concerns.

## 4. Concurrency and correctness
- [ ] I can identify shared mutable state in a design.
- [ ] I can explain locking, optimistic concurrency, idempotency, and ordering where relevant.
- [ ] I can describe race conditions likely in the given problem.
- [ ] I can explain failure modes for booking, inventory, payments, and notifications.

## 5. Extensibility and tradeoffs
- [ ] I can explain what parts of the design are stable versus likely to change.
- [ ] I can design extension points without prematurely abstracting everything.
- [ ] I can justify where strategy, observer, factory, state, or command patterns help.
- [ ] I can explain why a simpler implementation may be better in an interview.

## 6. Persistence and scale thinking
- [ ] I can explain what must be persisted and what can stay in memory.
- [ ] I can identify read-heavy versus write-heavy behavior.
- [ ] I can reason about indexing, caching, and lookup paths.
- [ ] I can connect local object design to scale and operational concerns.

## 7. Communication quality
- [ ] I can narrate my design in a top-down way.
- [ ] I can draw or describe class diagrams cleanly.
- [ ] I can explain tradeoffs without rambling.
- [ ] I can recover gracefully when requirements change mid-interview.
- [ ] I can compare alternate designs and choose one deliberately.

## 8. Practice coverage
- [ ] I have practiced at least 5 easy problems.
- [ ] I have practiced at least 10 medium problems.
- [ ] I have practiced at least 8 hard problems.
- [ ] I have practiced commerce, scheduling, social, and infra-style systems.
- [ ] I have practiced at least 3 problems under time pressure.

## 9. Review discipline
After every mock or self-practice:
- [ ] Write what I missed.
- [ ] Write what I overcomplicated.
- [ ] Write what assumptions I forgot to state.
- [ ] Write one cleaner version of the same answer.

## 10. Staff-level bar
- [ ] My answers show judgment, not just pattern recall.
- [ ] I make tradeoffs explicit.
- [ ] I model failure and operational reality where appropriate.
- [ ] I keep the design scoped to the interview instead of trying to rebuild the internet.

If you cannot explain why a design is simple enough, it probably is not.
