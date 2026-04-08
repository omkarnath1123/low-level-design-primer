# Chess Game
> Two-player chess with pluggable move validation, piece movement strategies, and kill mechanics.

---

## Core Entities

| Class | Role |
|---|---|
| `Board` | 8×8 grid of `Cell`s; initializes pieces |
| `Cell` | One square — row, col, `currentPiece` (nullable) |
| `Piece` | A chess piece — color, type, cell, move count, alive flag |
| `PieceType` | `KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN` |
| `Player` | Has color; supplies `PlayerMove` |
| `Color` | `WHITE / BLACK` |
| `GameController` | Round-robin turn loop; delegates move to `Piece` |

---

## Key Interfaces & Abstractions

| Abstraction | Purpose | Implementations |
|---|---|---|
| `PossibleMovesProvider` *(abstract)* | Compute cells reachable via one direction type | `Horizontal`, `Vertical`, `Diagonal` |
| `NextCellProvider` | Given a cell, return the next cell in this direction | Per-direction lambdas/impls |
| `MoveBaseCondition` | Is this move type even applicable? | `NoMoveBaseCondition`, `MoveBaseConditionFirstMove` (pawn first move) |
| `PieceMoveFurtherCondition` | Can the piece keep sliding past this cell? | `Default` (rook/bishop/queen), stops after 1 (knight/king) |
| `PieceCellOccupyBlocker` | Can the piece land on this cell? | `SelfPiece` (can't take own), `KingCheck` (can't expose king) |
| `PlayerMove` *(contract)* | Encapsulates piece + target cell for a turn | — |

---

## Relationships

```
Player  →  PlayerMove  (piece + toCell)
Piece
 ├── Color
 ├── PieceType
 ├── List<PossibleMovesProvider>   ← different for each piece type
 └── currentCell → Cell

PossibleMovesProvider (abstract)
 ├── maxSteps             (1 for king/knight, 7 for rook)
 ├── MoveBaseCondition
 ├── PieceMoveFurtherCondition
 └── PieceCellOccupyBlocker (base blocker)

GameController → List<Player> + Board
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy** | `PossibleMovesProvider` — each direction is a pluggable strategy |
| **Chain of Responsibility** | `List<PieceCellOccupyBlocker>` — each blocker checked in order |
| **Template Method** | `PossibleMovesProvider.possibleMoves()` checks base condition, then calls abstract `possibleMovesAsPerCurrentType()` |
| **Factory** | `PieceCellOccupyBlockerFactory.defaultAdditionalBlockers()` — wires standard blockers |
| **Command** | `PlayerMove` — encapsulates a move as an object |

---

## Piece → Moves Mapping

| Piece | Providers | maxSteps |
|---|---|---|
| Rook | Horizontal + Vertical | 7 |
| Bishop | Diagonal | 7 |
| Queen | H + V + Diagonal | 7 |
| King | H + V + Diagonal | 1 |
| Knight | L-shape custom | 1 |
| Pawn | Vertical (forward) | 1 (2 on first move) |

---

## Key Flow — Make a Move

```
1. player.makeMove() → PlayerMove(piece, toCell)
2. piece.move(player, toCell, board, blockers)
3. piece.nextPossibleCells(board, blockers, player)
   → each PossibleMovesProvider computes reachable cells
4. if toCell ∈ nextPossibleCells:
   ├── kill piece in toCell (if any)
   ├── piece.currentCell = toCell
   └── numMoves++
5. else → InvalidMoveException
6. GameController rotates to next player
```
