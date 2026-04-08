# Cricket Info / Cric Score

Two solutions model a cricket scoring and information platform.

---

## Solution 1 — Cricket Portal (Skeleton / Domain Model)

### Sub-system A: CricketPortal

#### Core Entities

| Package | Key Classes |
|---|---|
| `matchdetails` | `Match`, `Inning`, `Over`, `Ball`, `Run`, `Wicket`, `Commentary`, `Stadium` |
| `teamdetails` | `Team`, `Playing11`, `TournamentSquad`, `News` |
| `users` | `Player`, `Umpire`, `Referee`, `Commentator`, `Coach`, `Admin` |
| `stats` | `MatchStat`, `TeamStat`, `TournamentStat`, `TournamentSquadStat`, `Stat` |
| `enums` | `RunType`, `WicketType`, `UmpireType` |

#### Hierarchy

```
Match
 ├── Stadium
 ├── List<Team>
 └── List<Inning>
      └── List<Over>
           └── List<Ball>
                ├── Run  (RunType: NORMAL/WIDE/NO_BALL/FOUR/SIX)
                └── Wicket (WicketType: BOWLED/CAUGHT/LBW/...)

Player  → PlayerContract (terms with team)
Team    → TournamentSquad → Playing11
Umpire  → UmpireType (ON_FIELD / THIRD / RESERVE)
```

### Sub-system B: MovieTicketBooking *(bundled in same solution)*
> See [movie-ticket-booking.md](movie-ticket-booking.md) for the full breakdown.

---

## Solution 2 — CricScore (Full Implementation)

### Core Entities

| Class | Role |
|---|---|
| `Match` *(abstract)* | Base match — teams, innings, toss, result, stadium |
| `T20Match` | 20-over format match |
| `OdiMatch` | 50-over ODI match |
| `TestMatch` | Multi-day Test match |
| `FirstClassMatch` | First-class / County cricket |
| `CountyMatch` | County-level match |
| `Innings` | One batting innings — overs, score, fall of wickets |
| `Over` | One over — 6 balls |
| `BowlerOver` | Specific over bowled by a bowler (linked to player) |
| `Ball` | One delivery — run, wicket, type |
| `BallType` | `NORMAL / WIDE / NO_BALL / DEAD_BALL` |
| `ScoreCard` | Full match scorecard per innings |
| `PlayerScore` | Individual batting/bowling stats for one innings |
| `Tournament` | Series/tournament containing multiple `Fixture`s |
| `Fixture` | Scheduled match slot in a tournament |
| `Toss` | Toss result — winner + `TossAction` (BAT/FIELD) |
| `MatchResult` | Winner, result type |
| `Fall` | Fall of wicket — score + over at dismissal |

### People Hierarchy

```
Person (abstract)
 ├── Player
 │    ├── PlayerType    (BATSMAN/BOWLER/ALL_ROUNDER/WK)
 │    ├── PlayerStats
 │    └── PlayerResponsibility
 ├── Umpire
 ├── Referee
 ├── Commentator
 ├── Scorer
 ├── Admin
 ├── Manager
 └── SupportStaff (abstract)
      ├── Coach  (CoachType: HEAD/BATTING/BOWLING/FIELDING)
      ├── Doctor
      ├── Physio
      └── Psychologist

Team
 └── PlayingMembers → List<Player>
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Inheritance + Polymorphism** | `Match → T20/ODI/Test/FirstClass` — each enforces its own over/day limits |
| **Composite** | `Tournament → Fixture → Match → Innings → Over → Ball` |
| **Repository** | `DataSink` interface — persists match data |
| **Value Objects** | `Run`, `Wicket`, `Fall`, `Toss` — immutable facts |

---

## Key Flow — Record a Ball

```
1. Over.addBall(ball) → validate < 6 legal balls
2. Ball{type, run, wicket?}
3. Innings.score += run.value
4. if BallType.WIDE or NO_BALL → over ball count not incremented
5. if wicket → PlayerScore updated; Fall recorded
6. BowlerOver tracks bowler's economy/wickets
7. if Over complete → Innings.overs++; new Over started
8. if wicket == 10 or overs == max → Innings complete
```
