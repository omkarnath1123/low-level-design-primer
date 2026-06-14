# Interview Answer Template

Use this template to structure a low-level design answer during an interview.

## 1. Clarify scope first
Start by asking:
- What are the must-have features?
- What scale or concurrency assumptions should I care about?
- Should I optimize for extensibility, speed of implementation, or correctness?
- What is explicitly out of scope?

### Good opening line
> Let me first clarify scope, core workflows, and constraints so I can keep the design focused.

## 2. Restate the problem
Summarize the problem in your own words.

Template:
> We need to design a system that allows `<actors>` to `<main workflow>`, while preserving `<key invariants>` and supporting `<important extensions or scale assumptions>`.

## 3. Identify core entities
List the main domain objects.

Template:
- Actor / User types:
- Core entities:
- Supporting value objects:
- Important enums or states:

Example prompts:
- What is persisted?
- What has lifecycle?
- What is just metadata?

## 4. Identify invariants
Write the rules that must never be broken.

Examples:
- a seat cannot be double-booked
- a payment cannot be captured twice
- a rider can have only one active trip
- an auction bid must exceed current winning bid

Template:
> The key invariants are:
> 1. ...
> 2. ...
> 3. ...

## 5. Sketch high-level object model
State the main classes and responsibilities.

Template:
- `X` manages...
- `Y` represents...
- `ZService` orchestrates...
- `Repository` persists...
- `Strategy` varies...

Rule:
Do not dump 25 classes at once. Start with the few that explain the core workflow.

## 6. Walk one primary flow
Choose the most important path and narrate it.

Examples:
- booking flow
- checkout flow
- dispatch flow
- publish-subscribe flow

Template:
1. user initiates...
2. service validates...
3. repository loads...
4. policy/strategy decides...
5. state changes...
6. notification/event emitted...

## 7. Add interfaces only where behavior varies
Good candidates:
- pricing policy
- ranking strategy
- allocation strategy
- notification channel
- payment provider

Template:
> I would introduce an interface here because this behavior is likely to vary independently from the core entity model.

## 8. Discuss concurrency and failure modes
Always do this for staff-level answers.

Examples:
- double booking
- duplicate requests
- stale reads
- conflicting updates
- out-of-order events

Template:
> The main concurrency risk is ...
> I would protect it using ...

## 9. Discuss persistence and scale
Template questions:
- what should be in memory?
- what must be durable?
- what needs indexing?
- what is read-heavy vs write-heavy?
- where would caching help?

## 10. State tradeoffs explicitly
This is where good answers become strong answers.

Template:
> For interview scope, I am intentionally simplifying ...
> In production, I would likely add ...
> I chose this design because ...

## 11. Close with extension points
Examples:
- add more payment providers
- add more notification channels
- support more ride allocation strategies
- support audit and analytics later

Template:
> This design is easy to extend in these areas without rewriting the core domain model: ...

---

## 60-second mini template
Use this when time is tight:

1. clarify scope
2. identify actors and entities
3. state invariants
4. sketch 5 to 7 core classes
5. walk one key flow
6. mention concurrency risk
7. mention one tradeoff and one extension point

---

## Reusable phrases
- "Let me separate domain entities from orchestration logic first."
- "The key invariant I want to protect is ..."
- "I would avoid over-abstracting this part until we know the variation really exists."
- "This is where I would use a strategy because policy is likely to change."
- "For interview scope, I am keeping persistence behind a repository boundary."
- "The main failure mode here is ..."
- "If we scale this, the first pain point would likely be ..."

## Final rule
A structured answer beats a clever but scattered one.
