# Low-Level Design Questions

This document compiles common low-level design (LLD) and machine coding interview questions, organized by domain. Use these problems to practice object-oriented design, design patterns, and clean code principles.

## Games

* **Online card game** – Design a standard online card game (such as Poker).
* **Geographically partitioned card game** – Design a geographically partitioned multi-player card game supporting multiple players and concurrent games. Each game has one contractor who can play or spectate. Integrate billing/payment systems.
* **Chess game** – Design an in-memory chess game.
* **Tic-tac-toe** – Implement a command-line or API-driven tic-tac-toe game.
* **Snake and ladder game** – Design the standard board game with modular dice, snakes, and ladders.
* **Jackpot machine** – Design the low-level logic for a slot/jackpot machine.

## Utilities and libraries

* **Finite state machine** – Implement an extensible finite state machine (FSM) with one start state, multiple end states, and configurable notifications per state.
* **TextPad** – Code an in-memory, menu-driven TextPad supporting display, line insertion/deletion, clipboard copy/paste, and multi-level undo/redo operations.
* **Simple Java GC** – Design and implement a simple Java Garbage Collector (JVM representation) with Eden, Tenured, and Perm spaces, and reference-checking eviction.
* **JSON parser** – Design a secure JSON parser from scratch for untrusted input. Validate syntax and parse concurrently into memory structures.
* **Logger library** – Design and implement a logging framework that applications can use to log messages with levels (INFO, WARN, ERROR) and multiple sinks.
* **In-memory cache** – Design a generic, bounded, thread-safe in-memory cache module supporting configurable eviction policies (LRU, LFU, FIFO, Time-based).
* **CSV parser** – Design a modular, robust CSV file parser with custom delimiter configurations.
* **Unit testing application** – Design a low-level unit testing framework supporting test registration, suite grouping, assertions, and reporting.
* **Bar graph library** – Design a reusable library for rendering customizable bar graphs from raw data.
* **UUID generator** – Design an incrementally scalable, unique ID generation system supporting high throughput.

## E-commerce and retail

* **Q&A application** – Design a product Q&A system similar to what Amazon or Walmart hosts.
* **Amazon locker service** – Design an automated package locker monitoring and placement system supporting varying package and locker dimensions.
* **Billing and auction system** – Design an auction and billing engine similar to eBay.
* **Amazon comments filtering system** – Design a system that filters, sanitizes, and categorizes customer comments.
* **Vending machine** – Design a vending machine supporting item slots, multiple payment modes (cash, card), and inventory dispensing state transitions.
* **Splitwise** – Design an expense-sharing application with groups, equal/unequal splits, and minimum transaction settlement algorithms.
* **Live auction platform** – Design a real-time auction bidding platform (e.g., IPL/EPL player auctions).
* **Inventory system** – Design a real-time product inventory tracking system.
* **Warehouse management system** – Design a system to track inventory positions, shelf spaces, and item retrievals inside warehouses.
* **Coupon and promo code system** – Design a generic coupon engine supporting parameters:
    * Minimum $Z\%$ off up to $X$ amount (with or without minimum cart size).
    * Flat discount of $X$.
    * Applicability for specific customers, merchants, or usages.

## Productivity and collaborative applications

* **Jira / Task planner** – Design a task planner like Jira supporting Sprints, task types (Bugs, Features, Stories), sub-tasks, status transitions, and user assignment metrics.
* **Calendar application** – Design a calendar application (like Google Calendar) supporting events (meetings, holidays, reminders), guest list accept/reject states, and a utility to find common free slots.
* **Survey / Google Forms** – Design an extensible online survey builder with varying question types, response validation, and analytics reporting.
* **Configuration management system** – Design a configuration service where users can add, update, delete, search, and subscribe to configuration key-value pairs with instant change notifications.
* **Application tracking system (ATS)** – Design a hiring pipeline tracking platform (like Greenhouse).
* **Real-time collaboration platform** – Design a system supporting collaborative work or shared editing within team structures.
* **Online UML diagram tool** – Design the object model and canvas logic for a collaborative diagram editor (like Lucidchart).
* **Google Doc viewer tracker** – Design a service to view, add, and remove active viewers of a document in real time.

## Transportation, logistics, and services

* **Airport runway allocator** – Design an runway allocation service that planes use to request, lock, and release landing strips. Include fallback routing and flight center communication.
* **Ride-sharing application** – Design a ride-sharing system (like Uber or Lyft) where drivers offer seats and riders request trips, including conflict resolution algorithms (like maximum overlap).
* **Car rental system** – Design an online vehicle rental platform (like Zoomcar).
* **Logistics system** – Design a complete shipment delivery and tracking logistics engine.
* **Parking lot** – Design a multi-level parking lot supporting varying vehicle sizes, ticket generation, automated spot allocation, and payment calculations.
* **Swiggy/Zomato agent heatmap** – Design a backend service to track and aggregate real-time coordinate heatmaps of delivery agents.

## Social and content platforms

* **Subscription sports website** – Design a sports score tracking portal supporting real-time score updates, game history, and subscription access controls.
* **Push notification system** – Design an extensible notification dispatcher supporting promotional events, registration tables, and delivery across iOS, Android, and Email.
* **Suggestion / recommendation system** – Design a recommendation matching engine for products or content.
* **LinkedIn** – Design a professional social network supporting user connections, posts, and profile interactions.
* **Video upload system** – Design an optimized chunked upload mechanism for uploading large (e.g., >1GB) video files over highly unstable networks.
* **Chat application** – Design the object model and message routing for a chat app (like WhatsApp or WeChat).
* **Community discussion platform** – Design a forum system supporting hierarchical threads, nesting, tags, and user upvotes.
* **Product Hunt** – Design a product-sharing and community upvoting service.
* **Clipboard manager** – Design a secure clipboard sync engine optimizing the storage of text snippets and image blocks.
* **Glassdoor salary portal** – Design a salary reporting, aggregation, and comparison engine.
* **Short video platform** – Design a low-latency content feed engine for micro-videos (like TikTok).
* **Short story platform** – Design a narrative and micro-art publishing app (like Terribly Tiny Tales).

## Infrastructure, messaging, and distributed systems

* **Message queue system** – Design a message queue holding JSON payloads with subscriber regex filtering, runtime subscription callbacks, multi-subscriber dependency sequencing, and error retry retry logic.
* **Distributed ID generator** – Design an ID generation system (like Twitter Snowflake) that creates unique, time-sorted IDs across distributed clusters.
* **Distributed cache** – Design a highly available, partitioned, distributed in-memory caching system.
* **Business rules engine** – Design an extensible engine to parse, load, evaluate, and trigger workflows based on dynamic business rules.
* **Zipkin request tracer** – Design a request-tracing framework tracking span timings and execution sequences across distinct microservices.
* **Online code judge** – Design an online code compilation and execution sandbox (like LeetCode).
* **Splunk log manager** – Design a log collection and querying system supporting high-speed filter queries over past minutes, hours, or days.
* **RocksDB implementation** – Design an embedded, persistent key-value store.
* **Zookeeper using RocksDB** – Design a centralized coordination service leveraging structured key-value primitives.

## Financial and trading systems

* **Stock exchange system** – Design a matching engine for a stock exchange supporting high-frequency BUY/SELL order matching across stocks.
* **Stock trading system** – Design a trading dashboard supporting user portfolios, watchlists, and order placement.
* **Limit order book** – Design an in-memory limit order book for high-throughput security trading.
* **Payment gateway** – Design an extensible, reliable payment gateway integration layer (like Razorpay).

## Other specialized domains

* **Home automation system** – Design an object-oriented layout to remotely configure and toggle smart switches and appliances.
* **Gym management platform (FlipFit)** – Design a booking and membership system for fitness centers with center slot capacities, multi-workout configurations, and conflict-free bookings.
* **Medical appointment scheduler** – Design a clinic booking system where doctors open flexible slots independently of predefined calendar boundaries.
* **Online exam portal** – Design a quiz engine supporting subject-wise scores, negative marking, percentiles, mock comparisons, and scaled rank calculation for millions of concurrent test-takers.
