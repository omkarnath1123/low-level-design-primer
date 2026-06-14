# low-level-design-primer

A curated repository for low-level design, object-oriented design, and
system design interview preparation, with emphasis on senior and staff
software engineer interviews.

Use this repository to study:
- LLD and OOD interview rounds
- machine coding rounds
- design patterns and UML
- object modeling and domain boundaries
- staff-level tradeoff discussion

## Start here

If you are new to the repository, begin with these files:
- `docs/lld-handbook.md`
- `study-roadmap/README.md`
- `study-roadmap/04-8-week-roadmap.md`
- `staff-prep-checklist.md`

## Repository structure

### Core study material
- `questions.md`: broad question bank
- `resources.md`: external reading list
- `docs/`: topic notes and study guides
- `study-roadmap/`: guided preparation tracks and problem groupings
- `staff-prep-checklist.md`: self-review checklist for senior and staff prep

### Fundamentals
- `fundamentals/oop-java/`: Java OOP concept notes
  - abstraction
  - encapsulation
  - inheritance
  - polymorphism
  - interfaces
  - association, aggregation, and composition
  - classes and objects

### Design patterns
- `design-patterns/java/`: Java examples for major design patterns
- `Design-Patterns.md`: design-pattern study index
- `Distributed-Systems-Pattern.md`: distributed-systems revision notes

### Interview problem bank
- `problem-statements/`: standalone LLD problem statements
- `assets/class-diagrams/`: matching UML and class diagrams

### Implementations
- `reference-implementations/java/`: curated Java reference implementations
- `solutions/`: locally cloned external solution repositories
- `solutions.md`: original solution link index

### Tooling and automation
- `tools/CloneSolutions.java`: clone linked solution repositories
- `tools/SyncSolutions.java`: refresh cloned solution repositories
- `tools/DownloadVideos.java`: download videos from `solutions.md`
- `tools/AgentMemoryBridge.java`: REST bridge for agentmemory
- `tools/build_all.sh`: build all supported solutions in one run

## Recommended study flow

### Refresh your fundamentals
Start with:
- `fundamentals/oop-java/`
- `Design-Patterns.md`
- `Distributed-Systems-Pattern.md`

Goal:
- explain tradeoffs clearly
- justify abstraction boundaries
- identify when patterns help and when they add noise

### Practice from prompts first
Use `problem-statements/` before opening implementations.

For each problem:
1. rewrite the requirements in your own words
2. identify entities, workflows, and invariants
3. sketch a class diagram
4. explain concurrency, scale, and extension points

### Compare with implementations second
Use both:
- `reference-implementations/java/`
- `solutions/`

Goal:
- compare modeling choices
- evaluate naming, cohesion, extensibility, and pattern usage
- practice saying what you would simplify or redesign

### Practice communication explicitly
For each problem, be able to answer:
- What are the core entities?
- What are the invariants?
- Where are the extension points?
- What breaks first at scale?
- What should stay simple in interview scope?

Staff-level answers are not bigger answers. They are clearer answers.

## Study guides and companion docs

### Handbook and visual guides
- `docs/lld-handbook.md`
- `docs/lld-handbook-dark.md`
- `docs/uml-cheatsheet.md`
- `docs/interview-answer-template.md`

### Roadmaps and planning docs
- `study-roadmap/01-by-difficulty.md`
- `study-roadmap/02-by-company-style.md`
- `study-roadmap/03-by-theme.md`
- `study-roadmap/05-top-25-problems.md`
- `study-roadmap/06-mock-interview-prompts.md`
- `study-roadmap/07-revision-tracker.md`

## Build and automation

Run from the repository root:

```bash
bash tools/build_all.sh
bash tools/build_gradle.sh
bash tools/build_maven.sh
bash tools/compile_javac.sh
```

## Agentmemory REST bridge

This repository includes a Java 21 REST bridge for persistent memory:

```bash
java tools/AgentMemoryBridge.java health
java tools/AgentMemoryBridge.java remember "Use only root .gitignore" repo-hygiene git
java tools/AgentMemoryBridge.java repo-remember "low-level-design-primer" "Flatten nested solution repos into parent git" vcs repo-hygiene
java tools/AgentMemoryBridge.java search "gitignore nested git repo decision" 5
```

Environment variables:
- `AGENTMEMORY_URL` default: `http://localhost:3111`
- `AGENTMEMORY_SECRET` optional bearer token

## Imported-content policy

Content from the secondary repository was curated into dedicated directories
instead of copied wholesale.

Imported:
- OOP notes
- Java design-pattern examples
- problem statements
- class diagrams
- Java reference implementations

Excluded intentionally:
- build output
- IDE files
- duplicate repository metadata
- junk files with no preparation value

## Contribution guidance

When adding new material:
- keep the repository root clean
- put content in the correct study bucket
- prefer curated notes over repo dumps
- keep examples interview-friendly
- avoid unnecessary duplication between `problem-statements/`, `docs/`,
  `reference-implementations/`, and `solutions/`

## Credits

Credits and external sources remain with their original authors and linked
repositories. This repository acts as a curated preparation workspace for
study and comparison.
