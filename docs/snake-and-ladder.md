# Snake and Ladder Game
> Turn-based board game for N players on a 100-cell board with snakes and ladders.

---

## Core Entities

| Class | Role |
|---|---|
| `Game` | Orchestrates gameplay loop — players, board, dice, turn order |
| `Board` | 100-cell grid; holds all snakes and ladders; resolves jumps |
| `Player` | Has name and current position (1–100) |
| `Dice` | Rolls 1–6; supports multiple dice |
| `Snake` | `head → tail` (moves player backwards) |
| `Ladder` | `start → end` (moves player forwards) |

---

## Relationships

```
Game
 ├── Board
 │    ├── List<Snake>   (head position → tail)
 │    └── List<Ladder>  (start → end)
 ├── List<Player>
 └── Dice
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Model-View separation** | All logic in model classes; no UI coupling |
| **Simple OOP** | No over-engineering — matches game's natural simplicity |

---

## Key Flow — One Turn

```
1. Player rolls Dice → value (1–6)
2. newPos = player.position + diceValue
3. if newPos > 100 → skip turn (bounce back or stay)
4. Board checks:
   ├── Snake at newPos? → player moves to snake.tail
   └── Ladder at newPos? → player moves to ladder.end
5. Player.position = finalPos
6. if finalPos == 100 → player wins, game ends
7. next player's turn
```

---

## Rules Encoded
- Exact roll to 100 required to win (overshoot = no move)
- Snakes always go down, ladders always go up
- Multiple dice: sum of rolls used
