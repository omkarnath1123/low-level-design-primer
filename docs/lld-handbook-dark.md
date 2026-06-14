<div align="center">
  <h1>Low-Level Design Handbook — Dark Edition</h1>
  <p><strong>A high-contrast study companion for senior and staff software engineer interview prep</strong></p>
  <p>
    <img alt="LLD" src="https://img.shields.io/badge/Focus-Low%20Level%20Design-0f172a?style=for-the-badge" />
    <img alt="Staff" src="https://img.shields.io/badge/Level-Senior%20to%20Staff-1d4ed8?style=for-the-badge" />
    <img alt="Interview" src="https://img.shields.io/badge/Mode-Interview%20Prep-059669?style=for-the-badge" />
  </p>
</div>

---

<div style="border-left: 6px solid #60a5fa; background: #111827; color: #e5e7eb; padding: 12px 16px; border-radius: 8px;">
  <strong>Purpose:</strong> use this as a visually stronger reading mode for GitHub and markdown viewers that preserve inline HTML styles.
</div>

<div style="border-left: 6px solid #fbbf24; background: #1f2937; color: #f9fafb; padding: 12px 16px; border-radius: 8px; margin-top: 12px;">
  <strong>Mindset:</strong> staff-level LLD is not about writing more classes. It is about making better boundaries, cleaner tradeoffs, and clearer explanations.
</div>

## Core preparation pillars

<table>
  <tr>
    <th align="left">Pillar</th>
    <th align="left">What good looks like</th>
  </tr>
  <tr>
    <td>Object modeling</td>
    <td>Entities, value objects, ownership, invariants, responsibilities</td>
  </tr>
  <tr>
    <td>Design judgment</td>
    <td>Simplicity first, extensibility where justified, no abstraction cosplay</td>
  </tr>
  <tr>
    <td>Correctness</td>
    <td>Concurrency, race conditions, state transitions, failure paths</td>
  </tr>
  <tr>
    <td>Communication</td>
    <td>Structured explanation, explicit assumptions, crisp tradeoffs</td>
  </tr>
</table>

## Reading path

1. `../fundamentals/oop-java/`
2. `../Design-Patterns.md`
3. `uml-cheatsheet.md`
4. `interview-answer-template.md`
5. `../study-roadmap/04-8-week-roadmap.md`
6. `../staff-prep-checklist.md`

## Visual anchors from this repo

<table>
  <tr>
    <td align="center"><strong>Parking Lot</strong><br/><img src="../assets/class-diagrams/parkinglot-class-diagram.png" width="300" /></td>
    <td align="center"><strong>Movie Booking</strong><br/><img src="../assets/class-diagrams/movieticketbookingsystem-class-diagram.png" width="300" /></td>
  </tr>
  <tr>
    <td align="center"><strong>Ride Sharing</strong><br/><img src="../assets/class-diagrams/ridesharingservice-class-diagram.png" width="300" /></td>
    <td align="center"><strong>LinkedIn</strong><br/><img src="../assets/class-diagrams/linkedin-class-diagram.png" width="300" /></td>
  </tr>
</table>

## Staff-level reminders

<div style="border-left: 6px solid #34d399; background: #052e2b; color: #d1fae5; padding: 12px 16px; border-radius: 8px;">
  <strong>Say this explicitly:</strong>
  <ul>
    <li>what assumptions you are making,</li>
    <li>what is out of scope,</li>
    <li>where concurrency risk exists,</li>
    <li>what would change in production.</li>
  </ul>
</div>

<div style="border-left: 6px solid #f87171; background: #3f1d1d; color: #fee2e2; padding: 12px 16px; border-radius: 8px; margin-top: 12px;">
  <strong>Avoid this:</strong>
  <ul>
    <li>introducing patterns before identifying variation,</li>
    <li>mixing domain logic with persistence and IO details,</li>
    <li>ignoring invariants like double booking, duplicate payment, or stale state,</li>
    <li>turning a 45-minute interview into accidental enterprise architecture fan fiction.</li>
  </ul>
</div>

## Companion docs
- `lld-handbook.md`
- `interview-answer-template.md`
- `uml-cheatsheet.md`
- `../study-roadmap/README.md`
- `../staff-prep-checklist.md`
