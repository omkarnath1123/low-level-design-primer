# Bill Sharing — Splitwise
> Track shared expenses among groups; split costs; settle balances with notifications.

---

## Core Entities

| Class | Role |
|---|---|
| `Expense` | A shared bill — id, amount, title, date, status, group |
| `ExpenseGroup` | Members of the split + each member's `UserShare` |
| `UserShare` | One member's owed amount + list of `Contribution`s made |
| `Contribution` | A payment made by a user towards their share |
| `User` | Has email, name — identified by email |
| `ExpenseStatus` | `CREATED → ACTIVE → SETTLED` |

---

## Key Services

| Service | Responsibility |
|---|---|
| `ExpenseService` | Create expense, add members, assign shares, check if settled |
| `UserService` | Register users |
| `NotificationService` *(interface)* | Notify user when added to an expense |
| `NotificationServiceImpl` | Concrete notification sender |

---

## Relationships

```
Expense
 ├── userId (creator)
 ├── ExpenseStatus
 └── ExpenseGroup
      ├── List<User>  (groupMembers)
      └── Map<emailId, UserShare>
               ├── owedAmount
               └── List<Contribution>
                        └── contributionValue
```

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Strategy / Interface** | `NotificationService` — swap email/SMS/push without touching `ExpenseService` |
| **Builder** | `Expense.builder()` (Lombok) — clean object construction |
| **Repository** | Static `ExpenseRepository` / `UserRepository` maps |
| **Service Layer** | Business logic in service, not in models |

---

## Key Flow — Create and Settle an Expense

```
1. ExpenseService.createExpense(title, amount, creatorId)
   → Expense{status=CREATED, group=empty}

2. addUsersToExpense(expenseId, emailId)
   → group.members.add(user)
   → NotificationService.notifyUser(user, expense)

3. assignExpenseShare(expenseId, emailId, share)
   → group.userContributions.put(emailId, UserShare(share))

4. User makes contribution → UserShare.contributions.add(Contribution)

5. isExpenseSettled(expenseId):
   total = expense.amount
   for each UserShare → subtract contributions
   return total <= 1  (float tolerance)
```

---

## Exceptions

| Exception | When |
|---|---|
| `ExpenseDoesNotExistsException` | Operating on non-existent expense |
| `ContributionExceededException` | Contribution > owed share |
| `ExpenseSettledException` | Modifying an already-settled expense |
| `InvalidExpenseState` | Invalid state transition |
