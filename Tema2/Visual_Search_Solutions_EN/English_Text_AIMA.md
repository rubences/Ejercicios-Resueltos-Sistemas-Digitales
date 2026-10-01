# Solve, See and Verify

## Complete editable English text

English text companion to **Visual_Search_Solutions_AIMA_EN.pdf**. The 55 page numbers below match the visual guide. Explanations, table entries, source notes and figure labels are retained. Consult the matching PDF page for the spatial arrangement of trees, arrows and boards; a sequence of extracted figure labels is not itself a solution path.

The original source identifiers and symbolic notation are retained, including Sucesor, IslaA/IslaB and the island objects L (wolf), O (sheep), C (cabbage), B (boatman).


---

# Page 01 — Solve, see and verify

Search problems explained through states, decisions and evidence.

**Reaching the goal is not enough. We must explain**

**why it works.**

a₁

a₂

a₃

s₀

s₁

s₂

G

**FORMULATE**

What changes and what must stay the same.

**SEARCH**

Which node leaves the frontier, and why.

**VERIFY**

Validity, cost and whether a solution exists.

Student support material · Intelligent Systems

English edition. Labels and symbols remain compatible with the course notes.


*Source / scope note:* Teaching material developed from the supplied files. AIMA, 3rd edition, Chapters 2–4.


---

# Page 02 — A journey through the exercises

Click a section to open it. Pause questions come before the explanation.

01 | Method and notation | 3

02 | Coin change: 15 cents | 6

03 | Missionaries and cannibals | 9

04 | Wolf, sheep, cabbage and boatman | 14

05 | Ping-pong: proving impossibility | 17

06 | DLS: the pruning problem | 19

07 | Grafo2: five strategies | 22

08 | grafoclase: cost and reopenings | 29

09 | Number tree: goal 13 | 33

10 | Romania: A* from Oradea | 35

11 | Vacuum world and belief states | 38

12 | Explorers and boat capacity | 40

13 | 8-puzzle: the Unit2 goal | 42

14 | Column Jump: eight jumps | 46

15 | Best-first and local search | 50

16 | Summary, sources and classroom use | 53


*Source / scope note:* Unit1 and CourseIntroduction provide context; they are not treated as collections of solved problems.


---

# Page 03 — The same template for every problem

First define the problem; then choose how to explore its possibilities.

1

**INITIAL STATE**

Where do we start?

2

**ACTIONS**

Which moves are legal?

3

**TRANSITION**

Which state does each action produce?

4

**GOAL TEST**

How do we know we have finished?

5

**COST**

What makes one solution better?

**The notes' Sucesor function brings three elements together.**

Sucesor(s) returns (action, resulting state, cost): it is compatible with ACTIONS, RESULT and c in the book.


*Source / scope note:* AIMA §3.1.1, pp. 66–68; Unit2, p. 16.


---

# Page 04 — A state is not a node

The same configuration can occur along different paths.

**STATE**

Describes the world.

Example: Sibiu.

It does not, by itself, contain a parent, a depth or an accumulated cost.

**SEARCH NODE**

Describes one way of reaching that state.

**id, state, parent, action, d, g, h, f.**

Sibiu can appear with d=1 and with d=3. These are the same state but two different nodes.

140

**Arad**

**Sibiu**

75

71

151

**Arad**

**Zerind**

**Oradea**

**Sibiu**

d=1 · g=140 | d=3 · g=297


*Source / scope note:* AIMA §3.3, pp. 75–80; Unit2, pp. 47–50 and 97–100.


---

# Page 05 — Which algorithm version are we tracing?

An explicit convention prevents comparisons between numbers with different meanings.

**Aspect** | **In the course-note traces** | **In the book's specific BFS**

Goal test | When the node is popped | When the successor is generated

Repeated states | Allowed in the frontier; checked when popped

Avoided in the frontier and explored set

Depth | Root d=0; DLS accepts a goal at d=ℓ | Same depth, different implementation

Priority | BFS: d · DLS: −d · UCS: g | BFS: FIFO queue

Tie-breaking in this guide

Lowest creation id; ordered successors | Must be declared for each implementation

**Popping, expanding and finding are different operations.**

A CUT node is popped but not expanded. The goal is popped and recognised; its children are not generated.


*Source / scope note:* AIMA fig. 3.11, pp. 81–82; Unit2, pp. 34, 49, 96–105; ExampleDLSProblemPruning, pp. 1–3.


---

# Page 06 — 15 cents, with a limited coin supply

This is not foreign exchange: we are looking for a combination of coins.

50

**1 coin**

10

**4 coins**

5

**1 coin**

1

**4 coins**

**STATE**

((n50,n10,n5,n1), X)

n: coins already given. X: amount still due. Start: ((0,0,0,0),15).

**RULE AND GOAL**

Give an available coin whose value does not exceed X. Update its counter and subtract its value.

**Goal: X=0. Cost: 1 per coin.**

**Pause before the tree**

Why is storing only X generally insufficient to describe a coin-change problem with limited supplies?


*Source / scope note:* CurrencyExchange, p. 1; AIMA §3.1 and §3.4.1. Original solution of the Y=15 instance.


---

# Page 07 — The tree grows level by level

Operator order in the statement: 50, 10, 5, 1. The 50 operator is not applicable.

15

10

5

5

10

1

14

5

0

id=4

1

4

id=5

10

0

id=6

1

9

id=7

10

4

id=8

5

9

id=9

1

13

id=10

id=1 · give 10 | id=2 · give 5 | id=3 · give 1

id=0 · X still due

**Two paths, the same goal state**

10→5 and 5→10 reach ((0,1,1,0),0). The path order changes, not the final combination.


*Source / scope note:* CurrencyExchange, p. 1; tree computed for Y=15. Each node in the second layer represents a path.


---

# Page 08 — The frontier makes the order visible

The ids match the preceding tree. The head of the queue is on the left.

**Step** | **Popped** | **Frontier afterwards** | **Decision**

0 | — | [0] | Insert the root

1 | 0: X=15 | [1,2,3] | Generate 10, 5 and 1

2 | 1: X=5 | [2,3,4,5] | Generate goal 4, but do not stop

3 | 2: X=10 | [3,4,5,6,7] | Another path reaches the goal

4 | 3: X=14 | [4,5,6,7,8,9,10] | Finish the preceding level

5 | 4: X=0 | [5,6,7,8,9,10] | GOAL · reconstruct 0→1→4

2

coins: 10 + 5

2

actions; optimal cost

11

nodes created, including the root


*Source / scope note:* CurrencyExchange, p. 1; Unit2 GRAPH-SEARCH, pp. 49–50. Original trace with the goal tested when popped.


---

# Page 09 — Represent without duplicating information

We retain the two banks in the statement and add an equivalent shorthand.

**ORIGINAL NOTATION**

L<M C B> R<M C B>

Start: L<3 3 1> R<0 0 0>. Goal: L<0 0 0> R<3 3 1>.

**SHORTHAND IN THIS GUIDE**

(m,c,b): left-bank counts and the

boat's side, L or R.

The right bank contains 3−m missionaries and 3−c cannibals.

**00 START**

(3, 3, L)

M | M | M

C | C | C

L [B] | R

**11 GOAL**

(0, 0, R)

M | M | M

C | C | C

L | R [B]


*Source / scope note:* MisionariesAndCannibals, p. 1; Unit2, pp. 27–28; AIMA, Exercise 3.9, p. 115.


---

# Page 10 — A move must be safe on both banks

The formalisation sheet only begins to define the successor function.

**Candidate loads: M, C, MM, CC and MC.**

**Filter** | **What we check** | **Example from (3,3,L)**

Capacity | 1 ≤ passengers ≤ 2 | Empty boat: rejected

Availability | Everyone is on the boat's bank | No passengers are taken from the other bank

Safety on L | m=0 or m≥c | M alone leaves 2M and 3C: invalid

Safety on R | 3−m=0 or 3−m≥3−c | The arrival bank is also checked

Cost | One crossing costs 1 | BFS minimises crossings

**Legal initial successors**

C→ (3,2,R) · CC→ (3,1,R) · MC→ (2,2,R). M and MM fail the safety condition.


*Source / scope note:* AIMA, Exercise 3.9; hw1soln, Problem 1(c), pp. 1–2; completing the partial function in MisionariesAndCannibals.


---

# Page 11 — The solution in panels · 1/2

M = missionary · C = cannibal · [B] marks the boat's bank after the crossing.

**00 START**

(3, 3, L)

M | M | M

C | C | C

L [B] | R

01 CC →

(3, 1, R)

M | M | M

C | C | C

L | R [B]

02 C ←

(3, 2, L)

M | M | M

C | C | C

L [B] | R

03 CC →

(3, 0, R)

M | M | M

C | C | C

L | R [B]

04 C ←

(3, 1, L)

M | M | M

C | C | C

L [B] | R

05 MM →

(1, 1, R)

M

C

M | M

C | C

L | R [B]

**The decisive crossing is still to come**

After step 5, 1M and 1C remain on L. To continue, an MC pair must return.


*Source / scope note:* Original solution, verified by BFS and checks of every transition; rules from AIMA, Exercise 3.9.


---

# Page 12 — The solution in panels · 2/2

M = missionary · C = cannibal · [B] marks the boat's bank after the crossing.

06 MC ←

(2, 2, L)

M | M

C | C

M

C

L [B] | R

07 MM →

(0, 2, R)

C | C

M | M | M

C

L | R [B]

08 C ←

(0, 3, L)

C | C | C

M | M | M

L [B] | R

09 CC →

(0, 1, R)

C

M | M | M

C | C

L | R [B]

10 C ←

(0, 2, L)

C | C

M | M | M

C

L [B] | R

11 CC →

(0, 0, R)

M | M | M

C | C | C

L | R [B]

**Goal reached**

Everyone is on R. There have been 11 crossings, and missionaries were never outnumbered on either bank.


*Source / scope note:* Original solution, verified by BFS and checks of every transition; rules from AIMA, Exercise 3.9.


---

# Page 13 — Counting states is not counting paths

The complete reachable graph. Connections are reversible; the green line marks a shortest solution.

33L

31R

32R

22R

32L | 30R | 31L | 11R | 22L | 02R

03L

01R

02L

11L

00R | 01L

Example: 31L = (3,1,L).

01L appears after passing through the goal if we continue enumerating the graph.

32

combinations: 4 × 4 × 2

20

satisfy safety on both banks

16

reachable from the initial state


*Source / scope note:* Original enumeration of the state space; hw1soln 1(e) gives 16, although its list repeats an entry.


---

# Page 14 — The boatman is part of the state too

The diagram uses (IslaA,IslaB), with objects L, O, C and B.

**OBJECTS AND ACTIONS**

L: wolf · O: sheep
C: cabbage · B: boatman

The boatman crosses alone or with one object on his island. Cost 1.

**SAFETY CONDITION**

Without B, these pairs cannot stay together:

L and O · the wolf and the sheep. O and C · the sheep and the cabbage.

These restrictions are not written in the image: they are made explicit here.

**Start: ({L,O,C,B}, ∅)**

**Goal: (∅, {L,O,C,B})**

**Reading the image does not replace validating its edges**

The diagram's initial crossing by B alone is rejected under these rules: it leaves L, O and C unsupervised.


*Source / scope note:* espacioestadoIslas, p. 1. Safety rules made explicit as assumptions of the classical model.


---

# Page 15 — Seven crossings, eight states

After taking the sheep, take the cabbage before the wolf; the symmetric alternative also exists.

**00 START**

**ISLAND A**

L O C B

**ISLAND B**

∅

01 O →

**ISLAND A**

L C

**ISLAND B**

O B

**02 Alone ←**

**ISLAND A**

L C B

**ISLAND B**

O

03 C →

**ISLAND A**

L

**ISLAND B**

O C B

04 O ←

**ISLAND A**

L O B

**ISLAND B**

C

05 L →

**ISLAND A**

O

**ISLAND B**

L C B

**06 Alone ←**

**ISLAND A**

O B

**ISLAND B**

L C

07 O →

**ISLAND A**

∅

**ISLAND B**

L O C B

O → · B ← · C → · O ← · L → · B ← · O →


*Source / scope note:* Path computed under the preceding page's safety assumptions; notation from espacioestadoIslas.


---

# Page 16 — Why returning is necessary

Bringing the sheep back is not a mistake: it preserves safety for the next transfer.

**AFTER TAKING THE CABBAGE**

Island A: {L}
Island B: {B,O,C}

If B returns alone, O and C are left unsupervised.

**B must bring O back.**

**AFTER TAKING THE WOLF**

Island A: {O}
Island B: {B,L,C}

L and C can stay together.

**B can return alone.**

7

optimal cost: the first BFS goal

10

safe states reachable under these rules


*Source / scope note:* Analysis of the preceding path; original enumeration of 10 safe reachable states.


---

# Page 17 — Formulation does not guarantee a solution

The first AB game is already included in the initial counters.

**State = (game, a, b, c)**

**A wins**

AB

AC

**B wins**

AB

BC

(AB,1,1,0) → (AC,2,1,1) | (AB,1,1,0) → (BC,1,2,1)

**SUCCESSOR**

The winner stays; the waiting player joins. Increment the counters of both players in the new game.

**GOAL AND PRUNING**

Goal: (a,b,c)=(10,15,17). Do not generate a state exceeding any of these totals. Cost 1 per transition.

Each successor needs its own copy of the counters.


*Source / scope note:* Pinpong, p. 1. The successor function is expressed without modifying counters shared across branches.


---

# Page 18 — The correct answer: no such sequence exists

A short proof explains more than an enormous tree with no goal.

42

player appearances: 10 + 15 + 17

21

games: 42 / 2

11

minimum games for A, who starts

**A plays first and can never sit out two consecutive games.**

Whenever A sits out, A joins the next game. Thus, starting on the table in a 21-game sequence requires at least 11 appearances.

A

1

—

2

A

3

—

4

A

5

—

6

A

7

—

8

A

9

—

10

A

11

—

12

A

13

—

14

A

15

—

16

A

17

—

18

A

19

—

20

A

21

Illustrative minimum pattern: 11 appearances and 10 rests.

**The contradiction**

The target allows A only 10 games. Enumeration of 562 bounded states also fails to reach the goal: the maximum is 20 games.


*Source / scope note:* Original deduction from Pinpong, p. 1; cross-checked by enumerating states with bounded counters.


---

# Page 19 — Sibiu was visited… but at what depth?

Limit ℓ=4. Arad→Zerind→Oradea→Sibiu is explored first.

1

2

3

**Arad**

**Zerind**

**Oradea**

**Sibiu**

**Sibiu at d=3: 1 action remains within the limit.**

1

2

3

**Arad**

**Sibiu**

**Fagaras**

Buc.

**Sibiu at d=1: 3 actions remain. The goal now fits.**

**The error in Boolean pruning**

If closed only says "Sibiu is present", it discards the second arrival and may report failure despite a depth-3 solution.


*Source / scope note:* ExampleDLSProblemPruning, pp. 1–2; Unit2, pp. 82–84; AIMA §3.4.4.


---

# Page 20 — Reopening preserves a better opportunity

Priority −d orders the frontier; depth d determines whether one arrival dominates another.

**Stage** | **Popped node** | **Information in closed** | **What we do**

First arrival | Sibiu, d=3 | Not present | Expand; store d=3

Branch cutoff | Children with d=4 | Not expanded | Apply the limit

Second arrival | Sibiu, d=1 | Sibiu: 3 | 1<3: REOPEN

Next | Fagaras, d=2 | Not present | Expand

Goal | Bucharest, d=3 | — | Stop and reconstruct

140

99

211

**Arad**

**Sibiu**

**Fagaras**

Buc.

**3 actions · cost 450 · DLS does not guarantee the lowest cost.**


*Source / scope note:* ExampleDLSProblemPruning, pp. 2–3. Explicit distinction between positive depth and negative value.


---

# Page 21 — Three different outcomes

Test the goal before rejecting a node that lies exactly at the limit.

**SOLUTION**

A legal path reaches a goal within ℓ.

**CUTOFF**

The limit stopped search.
Not a proof of impossibility.

**FAILURE**

All possibilities are exhausted, with no solution hidden by the limit.

pop n
if goal(n.state): return solution
if n.d == limit: record cutoff; continue
if state not in closed or n.d < closed[state]:
    closed[state] = n.d
    generate and order successors

**Rule for IDS**

Increase ℓ after cutoff, and reset visited-state tracking at each iteration.


*Source / scope note:* AIMA Fig. 3.17, p. 88; ExampleDLSProblemPruning, pp. 2–3. Teaching pseudocode for reopening.


---

# Page 22 — One graph, five ways to explore

ZIP instance: start 0, goal 9. This is not Unit2's 0→7 graph.

1

3

6

6

0

5

3

6

9

**A path with cost 16**

**Strategy** | **Path found** | d | **Cost**

BFS | 0 → 5 → 2 → 6 → 9 | 4 | 16

UCS | 0 → 5 → 3 → 6 → 9 | 4 | 16

DLS | 0 → 5 → 2 → 1 → 8 → 3 → 6 → 9 | 7 | 43

Greedy | 0 → 5 → 2 → 6 → 9 | 4 | 16

A* | 0 → 5 → 3 → 6 → 9 | 4 | 16

Computed with successors in ascending state order and ties broken by id. DLS uses ℓ=7.


*Source / scope note:* Grafo2.zip: 2.pdf and trees 2__Breadth_, 2__Uniform_, 2__Depth7_, 2__Greedy_, 2__A_.


---

# Page 23 — Learning to read the ZIP's nodes

The three-row format of the original trees is preserved.

11; 16

9 S

4; 16; 0

First row: id; value or priority.

Second row: state and marker.

Third row: depth; g; h.

S | * | CUT | **CUT_DEPTH**

Solution path | Expanded, outside the path | Repeated state discarded | Cut off by the limit

**Priority by strategy**

BFS: d · DLS: −d · UCS: g · Greedy: h · A*: g+h. Never confuse a node's id with its state.


*Source / scope note:* Grafo2.zip, five trees; Unit2, pp. 97–100. CUT, * and S follow the course notes.


---

# Page 24 — Complete tree · BFS

Green: solution path · blue: expanded · red: discarded · grey: pending when search ends.

0; 0 0  S

0; 0; 6

1; 1 5  S

1; 1; 3

1

2; 2 2  S

2; 7; 3

6

3; 2 3  *

2; 4; 5

3

4; 3 1  *

3; 15; 2

8

5; 3

3  CUT

3; 15; 5

8

6; 3 6  S

3; 10; 4

3

7; 3 7  *

3; 16; 12

9

8; 3

6  CUT

3; 10; 4

6

9; 3 8  *

3; 10; 9

6

10; 4

5  CUT

4; 20; 3

5

11; 4

7  CUT

4; 16; 12

1

12; 4

8  CUT

4; 21; 9

6

13; 4

8  CUT

4; 18; 9

8

14; 4

9  S

4; 16; 0

6

15; 4

1

4; 17; 2

7

16; 4

3

4; 20; 5

10

17; 4

5

4; 12; 3

2

18; 4

6

4; 19; 4

9

19; 4

7

4; 16; 12

6

**Path: 0 → 5 → 2 → 6 → 9 | Cost: 16**

15 pops · 8 expansions · 20 nodes created, including the root.


*Source / scope note:* Grafo2.zip, BFS tree; original reconstruction with the same ordering rules. Root id=0.


---

# Page 25 — Complete tree · UCS

Green: solution path · blue: expanded · red: discarded · grey: pending when search ends.

0; 0 0  S

0; 0; 6

1; 1 5  S

1; 1; 3

1

2; 7 2  *

2; 7; 3

6

3; 4 3  S

2; 4; 5

3

6; 15

1  *

3; 15; 2

8

7; 15

3  CUT

3; 15; 5

8

8; 10

6  CUT

3; 10; 4

3

9; 16

7  *

3; 16; 12

9

4; 10

6  S

3; 10; 4

6

5; 10

8  *

3; 10; 9

6

10; 18

8

4; 18; 9

8

11; 16

9  S

4; 16; 0

6

12; 17

1

4; 17; 2

7

13; 20

3

4; 20; 5

10

14; 12 5  CUT

4; 12; 3

2

15; 19

6

4; 19; 4

9

16; 16

7

4; 16; 12

6

17; 20

5

4; 20; 3

5

18; 16

7

4; 16; 12

1

19; 21

8

4; 21; 9

6

**Path: 0 → 5 → 3 → 6 → 9 | Cost: 16**

12 pops · 8 expansions · 20 nodes created, including the root.


*Source / scope note:* Grafo2.zip, UCS tree; original reconstruction with the same ordering rules. Root id=0.


---

# Page 26 — Complete tree · DLS with ℓ=7

Green: solution path · blue: expanded · red: discarded · grey: pending when search ends.

0; 0 0  S

0; 0; 6

1; -1

5  S

1; 1; 3

1

2; -2

2  S

2; 7; 3

6

3; -2

3

2; 4; 5

3

4; -3

1  S

3; 15; 2

8

5; -3

3

3; 15; 5

8

6; -3

6

3; 10; 4

3

7; -3

7

3; 16; 12

9

8; -4

5  CUT

4; 20; 3

5

9; -4

7  *

4; 16; 12

1

10; -4

8  S

4; 21; 9

6

11; -5

1  CUT

5; 28; 2

7 | 12; -5

3  S

5; 31; 5

10

13; -5

5

5; 23; 3

2

14; -5

6

5; 30; 4

9

15; -5

7

5; 27; 12

6

16; -6

6  S

6; 37; 4

6

17; -6

8

6; 37; 9

6

18; -7

8  CUT_DEPTH

7; 45; 9 | 8

19; -7

9  S

7; 43; 0

6

**Path: 0 → 5 → 2 → 1 → 8 → 3 → 6 → 9 | Cost: 43**

12 pops · 8 expansions · 20 nodes created, including the root.


*Source / scope note:* Grafo2.zip, DLS tree; original reconstruction with the same ordering rules. Root id=0.


---

# Page 27 — Complete tree · Greedy

Green: solution path · blue: expanded · red: discarded · grey: pending when search ends.

0; 6 0  S

0; 0; 6

1; 3 5  S

1; 1; 3

1

2; 3 2  S

2; 7; 3

6

3; 5

3

2; 4; 5

3

4; 2 1  *

3; 15; 2

8

5; 5

3

3; 15; 5

8

6; 4 6  S

3; 10; 4

3

7; 12

7

3; 16; 12

9

8; 3

5  CUT

4; 20; 3

5

9; 12

7

4; 16; 12

1

10; 9

8

4; 21; 9

6

11; 9

8

4; 18; 9

8

12; 0

9  S

4; 16; 0

6

**Path: 0 → 5 → 2 → 6 → 9 | Cost: 16**

7 pops · 5 expansions · 13 nodes created, including the root.


*Source / scope note:* Grafo2.zip, Greedy tree; original reconstruction with the same ordering rules. Root id=0.


---

# Page 28 — Complete tree · A*

Green: solution path · blue: expanded · red: discarded · grey: pending when search ends.

0; 6 0  S

0; 0; 6

1; 4 5  S

1; 1; 3

1

2; 10

2  *

2; 7; 3

6

3; 9 3  S

2; 4; 5

3

6; 17

1

3; 15; 2

8

7; 20

3

3; 15; 5

8

8; 14

6  CUT

3; 10; 4

3

9; 28

7

3; 16; 12

9

4; 14

6  S

3; 10; 4

6

5; 19

8

3; 10; 9

6

10; 27

8

4; 18; 9

8

11; 16

9  S

4; 16; 0

6

**Path: 0 → 5 → 3 → 6 → 9 | Cost: 16**

7 pops · 5 expansions · 12 nodes created, including the root.


*Source / scope note:* Grafo2.zip, A* tree; original reconstruction with the same ordering rules. Root id=0.


---

# Page 29 — Few steps do not imply a low cost

Instance: blue start 0; red goal 10. This interpretation of the diagram is explicit; no additional statement was supplied.

4

2

6

7

5

0

6

2

9

4

10

**BFS: 5 actions · cost 24**

4

2

6

7

2

2

0

6

2

9

4

7

10

**UCS and A*: 6 actions · cost 23**

**The difference comes at the end**

4→10 costs 5. The alternative 4→7→10 costs 2+2=4, although it uses one extra step.


*Source / scope note:* grafoclase, p. 1, image rotated and transcribed; original results for the directed edges.


---

# Page 30 — A* separates actual cost from its estimate

With h(s) taken from the graph labels and f=g+h, values do not always increase.

id | **State** | g | h | f | **Decision**

0 | 0 | 0 | 23 | 23 | EXPAND

1 | 6 | 4 | 9 | 13 | EXPAND

3 | 3 | 12 | 7 | 19 | EXPAND

2 | 2 | 6 | 14 | 20 | EXPAND

7 | 9 | 12 | 4 | 16 | EXPAND

6 | 3 | 11 | 7 | 18 | REOPEN

11 | 8 | 15 | 6 | 21 | EXPAND

5 | 8 | 16 | 6 | 22 | CUT

9 | 4 | 19 | 3 | 22 | EXPAND

17 | 7 | 21 | 2 | 23 | EXPAND

24 | 10 | 23 | 0 | 23 | GOAL

**State 3 is first reached with g=12, then with g=11. The better arrival is reopened.**


*Source / scope note:* grafoclase, p. 1; AIMA §3.5.2, pp. 93–95. Original trace with reopening for a better g.


---

# Page 31 — An admissible heuristic can be inconsistent

Admissibility compares against the true remaining cost. Consistency checks each edge.

**ADMISSIBILITY**

h(s) ≤ h*(s)

At the start: h(0)=23 and h*(0)=23.

**CONSISTENCY**

h(s) ≤ c(s,t)+h(t)

On 0→6: 23 > 4+9. The condition fails.

**State** | 0 | 2 | 4 | 6 | 7 | 9 | 10

h | 23 | 14 | 3 | 9 | 2 | 4 | 0

h* | 23 | 17 | 4 | 19 | 2 | 11 | 0

**Practical consequence**

This A* implementation reopens states when g decreases. A plausible-looking heuristic does not, by itself, establish consistency.


*Source / scope note:* AIMA §3.5.2, pp. 94–95; grafoclase, p. 1. Remaining distances computed by edge relaxation.


---

# Page 32 — Audit the graph before searching

Transcribed adjacency list: destination(cost). This is the calculation reference for this guide.

**State** | h | **Directed successors**

0 | 23 | 6(4)

1 | 13 | 3(9)

2 | 14 | 3(5), 9(6)

3 | 7 | 0(4), 8(4)

4 | 3 | 0(8), 1(5), 3(8), 5(1), 7(2), 8(1), 9(10), 10(5)

5 | 15 | 0(10), 1(10)

6 | 9 | 2(2), 3(8)

7 | 2 | 1(6), 3(4), 4(3), 10(2)

8 | 6 | 1(2)

9 | 4 | 1(7), 4(7)

10 | 0 | 2(2)

**BFS and Greedy return 24 in this instance; UCS and A* with reopening return 23.**


*Source / scope note:* grafoclase, p. 1, figure with no extractable text. Declared successor order: ascending destination state.


---

# Page 33 — Reaching 13: seeing the whole structure

Each state n generates 2n and 2n+1. The root has depth 0.

1

2 | 3

4 | 5 | 6 | 7

8 | 9 | 10 | 11 | 12 | 13 | 14 | 15

d=0

d=1

d=2

d=3

**Solution: 1 → 3 → 6 → 13 · Three actions**


*Source / scope note:* hw1soln, Problem 2, p. 2. AIMA Exercise 3.15 uses a different goal; 13 is retained here.


---

# Page 34 — BFS, DLS and IDS visit in different orders

Here, "visited" means popped/tested; the goal is included and search stops there.

BFS

1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13

DLS · ℓ=3

1, 2, 4, 8, 9, 5, 10, 11, 3, 6, 12, 13

IDS | **Order after each restart**

ℓ=0 | 1

ℓ=1 | 1, 2, 3

ℓ=2 | 1, 2, 4, 5, 3, 6, 7

ℓ=3 | 1, 2, 4, 8, 9, 5, 10, 11, 3, 6, 12, 13

**Backwards: 13 → 6 → 3 → 1, since parent(n)=⌊n/2⌋. Backward branching factor: 1.**


*Source / scope note:* hw1soln, Problem 2(b,c), p. 2; AIMA §3.4.1, §3.4.4–6. Left successor before right.


---

# Page 35 — A* from Oradea to Bucharest

We retain hw1soln's starting city rather than replacing it with Arad from the book's example.

151

80

97

101

**Oradea**

**Sibiu**

**Rimnicu**

**Pitesti**

Buc.

151 + 80 + 97 + 101 = 429

**ACCUMULATED COST g**

0 → 151 → 231 → 328 → 429. This is the cost already incurred, not an estimate.

**HEURISTIC h**

380 → 253 → 193 → 100 → 0. Straight-line distance to Bucharest.

In the 3rd edition, the heuristic table is Figure 3.22; the older worksheet refers to Figure 4.1.


*Source / scope note:* hw1soln, Problem 4, pp. 2–3; AIMA Figs. 3.2 and 3.22, pp. 68 and 93.


---

# Page 36 — Do not stop at the first generated goal

Bucharest enters with cost 461, but Pitesti improves it to 429.

**Expanded** | **Frontier ordered by f** | **What to notice**

Start | Oradea: 380 | g=0, h=380

Oradea | Sibiu: 404 · Zerind: 445 | Sibiu =151+253

Sibiu | Rimnicu: 424 · Fagaras: 426 · Zerind: 445 · Arad: 657 | Rimnicu =231+193

Rimnicu | Fagaras: 426 · Pitesti: 428 · Zerind: 445 · Craiova: 537 · Arad: 657 | Pitesti =328+100

Fagaras | Pitesti: 428 · Zerind: 445 · Bucharest: 461 · Craiova: 537 · Arad: 657 | The goal is not first in the queue

Pitesti | Bucharest: 429 · Zerind: 445 · Craiova: 537 · Arad: 657 | Update 461 → 429

Bucharest | GOAL | Popped with the lowest f


*Source / scope note:* hw1soln, A* table, p. 3; AIMA §3.5.2, pp. 93–96. Frontier of best arrivals, as in the worksheet.


---

# Page 37 — Changing the start changes the result

The Arad example compares the number of steps with total distance.

**FROM ARAD · 3 STEPS**

Arad → Sibiu → Fagaras → Bucharest.

140 + 99 + 211 = 450.

A shallow solution. This is the path found by corrected DLS and BFS with the declared ordering.

**FROM ARAD · 4 STEPS**

Arad → Sibiu → Rimnicu → Pitesti → Bucharest.

140 + 80 + 97 + 101 = 418.

UCS minimises kilometres, not hops.

**Pause at Unit2, page 7**

Bucharest is not connected in that drawing: using only that graph, no path to the goal is shown. A path does exist in the complete map on page 6.


*Source / scope note:* Unit2, pp. 5–7, 62 and 94; AIMA Fig. 3.2 and §3.4.1–2. Sums computed from the map.


---

# Page 38 — A physical state and a belief state

Uncertainty changes what the search must store.

**PHYSICAL STATE**

(position, dirtA, dirtB).

2 × 2 × 2 = 8 states.

Goal: both squares clean.

**SENSORLESS**

The agent maintains a set of possible states. Each action is applied to every member of that set.

**Action** | **Possible set afterwards** | **What is known**

Start | {1,2,3,4,5,6,7,8} | The configuration is unknown

Right | {2,4,6,8} | The agent is in B

Suck | {4,8} | B is clean

Left | {3,7} | The agent is in A and B is clean

Suck | {7} | Both are clean


*Source / scope note:* Unit2, pp. 11–14 and 20–22; Unit1, pp. 35–37. Vacuum-world sequences from the course notes.


---

# Page 39 — 300 states or 307,200?

It depends on which variables the model includes; the statement does not fully specify this.

×

×

×

3

2

5

10

**Power** | **Camera** | **Brush height** | **Position**

300

3 × 2 × 5 × 10: robot configuration

307,200

300 × 2¹⁰: if each of 10 squares is clean/dirty

**State the scope of the count**

The 300 cases exclude a variable dirt map. The 307,200 cases require ten additional binary variables and independence.


*Source / scope note:* Unit2, p. 22. Original count assuming independent variables; robot and environment configurations are distinguished.


---

# Page 40 — Transporting people and cargo

The total weight on any crossing cannot exceed 100 kg.

90 kg

Alex

80 kg

Brook

60 kg

Chris

40 kg

Dusty

20 kg

Cargo

**STATE**

(A,B,C,D,E,b), with each value indicating a bank.

E denotes the cargo; b denotes the boat. Start: everything on the first bank. Goal: everything on the second.

**LEGAL ACTION**

A subset on the boat's bank, including at least one explorer.

Their total weight is ≤100. All selected items and the boat switch banks.

**Two especially useful combinations**

Chris + Dusty = 100 kg. Brook + cargo = 100 kg. Alex must cross alone.


*Source / scope note:* Unit2, p. 29. Explicit assumptions: indivisible 20 kg package, at least one person rowing, and cost 1 per crossing.


---

# Page 41 — An optimal nine-crossing solution

The cargo crosses with Brook; Chris and Dusty return the boat when needed.

**Crossing** | **Direction** | **Transported** | **Weight**

1 | → | Chris + Dusty | 100

2 | ← | Chris | 60

3 | → | Alex | 90

4 | ← | Dusty | 40

5 | → | Chris + Dusty | 100

6 | ← | Chris | 60

7 | → | Brook + Cargo | 100

8 | ← | Dusty | 40

9 | → | Chris + Dusty | 100

**Every crossing respects capacity. The first BFS goal appears at depth 9.**


*Source / scope note:* Original solution and BFS verification for the model made explicit on the preceding page.


---

# Page 42 — Solving Unit2's specific goal

The blank moves up, down, left or right without leaving the board.

7 | 2 | 4

5 | · | 6

8 | 3 | 1

1 | 2 | 3

4 | 5 | 6

7 | 8 | ·

**Search**

**START** | **GOAL**

**FORMULATION**

State: nine positions, 0=blank. Cost: 1 per move. Goal: (1,2,3,4,5,6,7,8,0).

**HEURISTIC**

Manhattan: sum the eight tiles' distances from their final positions, excluding the blank.


*Source / scope note:* Unit2, pp. 23–24; AIMA §3.2 and §3.6, pp. 70–71 and 102–103. Original solution for the displayed goal.


---

# Page 43 — Twenty verified moves · 1/3

Each panel's letter denotes the blank's most recent move.

**00 · START**

7 | 2 | 4

5 | · | 6

8 | 3 | 1

01 · D

7 | 2 | 4

5 | 3 | 6

8 | · | 1

02 · R

7 | 2 | 4

5 | 3 | 6

8 | 1 | ·

03 · U

7 | 2 | 4

5 | 3 | ·

8 | 1 | 6

04 · L

7 | 2 | 4

5 | · | 3

8 | 1 | 6

05 · L

7 | 2 | 4

· | 5 | 3

8 | 1 | 6

06 · U

· | 2 | 4

7 | 5 | 3

8 | 1 | 6

**Sequence: D R U L L U R R D L D L U R U L D R R D**


*Source / scope note:* Original BFS path; Unit2 rules and goal, pp. 23–24. U=up, D=down, L=left, R=right.


---

# Page 44 — Twenty verified moves · 2/3

Each panel's letter denotes the blank's most recent move.

07 · R

2 | · | 4

7 | 5 | 3

8 | 1 | 6

08 · R

2 | 4 | ·

7 | 5 | 3

8 | 1 | 6

09 · D

2 | 4 | 3

7 | 5 | ·

8 | 1 | 6

10 · L

2 | 4 | 3

7 | · | 5

8 | 1 | 6

11 · D

2 | 4 | 3

7 | 1 | 5

8 | · | 6

12 · L

2 | 4 | 3

7 | 1 | 5

· | 8 | 6

13 · U

2 | 4 | 3

· | 1 | 5

7 | 8 | 6

**Sequence: D R U L L U R R D L D L U R U L D R R D**


*Source / scope note:* Original BFS path; Unit2 rules and goal, pp. 23–24. U=up, D=down, L=left, R=right.


---

# Page 45 — Twenty verified moves · 3/3

Each panel's letter denotes the blank's most recent move.

14 · R

2 | 4 | 3

1 | · | 5

7 | 8 | 6

15 · U

2 | · | 3

1 | 4 | 5

7 | 8 | 6

16 · L

· | 2 | 3

1 | 4 | 5

7 | 8 | 6

17 · D

1 | 2 | 3

· | 4 | 5

7 | 8 | 6

18 · R

1 | 2 | 3

4 | · | 5

7 | 8 | 6

19 · R

1 | 2 | 3

4 | 5 | ·

7 | 8 | 6

20 · D

1 | 2 | 3

4 | 5 | 6

7 | 8 | ·

GOAL

**Cost 20**

**Sequence: D R U L L U R R D L D L U R U L D R R D**


*Source / scope note:* Original BFS path; Unit2 rules and goal, pp. 23–24. U=up, D=down, L=left, R=right.


---

# Page 46 — Jumps that remove same-colour groups

We use the 4×4 board in hw1soln; 0 denotes an empty square.

1 | 2 | 2 | 1

2 | 1 | 3 | 2

3 | 1 | 3 | 2

· | 2 | 3 | ·

**MOVE**

A ball jumps over one or more adjacent balls of a single

colour different from its own.

It lands in the empty square immediately beyond them. All jumped balls are removed.

**GOAL AND COST**

Exactly one ball remains, in any square. Cost 1 per jump, even when several balls are removed.

Coordinates are (row,column), starting at 1.


*Source / scope note:* hw1soln, Problem 6, pp. 3–5. Horizontal/vertical jumps are made explicit, as in its example moves.


---

# Page 47 — The worked example · 1/2

Numbers identify colours. The visual colour is only an aid; it does not change the rule.

**00 · START**

1 | 2 | 2 | 1

2 | 1 | 3 | 2

3 | 1 | 3 | 2

· | 2 | 3 | ·

01 · (2,1) → (4,1)

1 | 2 | 2 | 1

· | 1 | 3 | 2

· | 1 | 3 | 2

2 | 2 | 3 | ·

02 · (1,4) → (4,4)

1 | 2 | 2 | ·

· | 1 | 3 | ·

· | 1 | 3 | ·

2 | 2 | 3 | 1

03 · (1,1) → (1,4)

· | · | · | 1

· | 1 | 3 | ·

· | 1 | 3 | ·

2 | 2 | 3 | 1

04 · (4,2) → (1,2)

· | 2 | · | 1

· | · | 3 | ·

· | · | 3 | ·

2 | · | 3 | 1


*Source / scope note:* Original hw1soln sequence, p. 4; every jump and the final board have been checked using the model.


---

# Page 48 — The worked example · 2/2

Numbers identify colours. The visual colour is only an aid; it does not change the rule.

05 · (4,4) → (4,2)

· | 2 | · | 1

· | · | 3 | ·

· | · | 3 | ·

2 | 1 | · | ·

06 · (4,1) → (4,3)

· | 2 | · | 1

· | · | 3 | ·

· | · | 3 | ·

· | · | 2 | ·

07 · (4,3) → (1,3)

· | 2 | 2 | 1

· | · | · | ·

· | · | · | ·

· | · | · | ·

08 · (1,4) → (1,1)

1 | · | · | ·

· | · | · | ·

· | · | · | ·

· | · | · | ·

**One ball.**

**Eight jumps.**


*Source / scope note:* Original hw1soln sequence, p. 4; every jump and the final board have been checked using the model.


---

# Page 49 — Two safe heuristics and an evidence limit

Not every expression in the worksheet can be used without checking its conditions.

h₀(s)=0

Never overestimates a non-negative cost.

A* with this heuristic is equivalent to UCS; with unit costs it finds the same minimum cost as BFS.

**h₁(s)=colours(s)−1**

A jump removes balls of just one colour, so it cannot eliminate more than one colour.

Going from k colours to 1 requires at least k−1 jumps.

7×7 count: with k colours, (k+1)⁴⁹ configurations. The worksheet's 7⁴⁹ assumes six colours plus

the empty square.

Caution: min(occupied rows,occupied columns) equals 1 at a goal, so the unadjusted expression

is not admissible.

**What has and has not been verified**

The 4×4 example has a minimum solution length of 8 jumps. testExample1/2/3.txt were not attached: no running times are invented.


*Source / scope note:* hw1soln, 6(a,c,f–h), pp. 4–5; AIMA §3.5.2. Experimental verification is limited to the supplied 4×4 board.


---

# Page 50 — Poor guidance can explore more than BFS

"Best-first" is a family. Here the exercise is interpreted as Greedy, which prioritises h.

S

A

B

C | D

G

h=1 | h=1 | h=1

h=2

h=0

**Greedy: S,A,C,D,G · 5 pops · cost 4.**

**BFS: S,A,B,C,G · 5 pops · cost 2.**


*Source / scope note:* hw1soln, Problem 3, p. 2; AIMA §3.5.1. Original teaching example: it does not reproduce the worksheet's ambiguous counts.


---

# Page 51 — Be precise about what is being counted

The preceding diagram has equal numbers of pops but different costs. We extend it to make one strategy worse on both measures.

S | A₁ | A₂ | A₃ | A₄ | G

**Long branch: 5 actions; h(Aᵢ)=1. Short branch: S→B→G, with h(B)=2 and h(G)=0.**

**GREEDY**

Pops S,A₁,A₂,A₃,A₄,G.

**6 pops · 5 actions.**

BFS

Pops S,A₁,B,A₂,G.

**5 pops · 2 actions.**

All edges cost 1; A₁ before B; goal test when popped. The heuristic is deliberately inadmissible: h(B)=2, although B→G costs 1.


*Source / scope note:* Original example completing hw1soln Problem 3 without attributing unspecified data to its solution.


---

# Page 52 — Limiting cases of local algorithms

The worksheet's equivalences also depend on acceptance and stopping rules.

**Case** | **Worksheet answer** | **Necessary qualification**

Local beam, k=1 | Hill climbing | The usual equivalence, provided the best improvement is accepted and the stopping rule is shared.

Beam with no state limit

BFS | Starts from one root and retains every successor at each level.

Annealing, T→0⁺, no stopping test

First-choice hill climbing | Worsening moves are rejected; plateaus require a defined tie-handling rule.

Genetic algorithm, N=1 | Random walk through mutations

Crossing an individual with itself combines no diversity; without elitism, improvement is not guaranteed.

**Do not literally substitute T=0 into a division by T. Interpret this as a limit, as the worksheet suggests.**


*Source / scope note:* hw1soln, Problem 5, p. 3; AIMA §4.1, pp. 122–128 and Exercise 4.1, p. 157.


---

# Page 53 — What students should be able to explain

A complete answer is more than a list of states.

**FORMULATION**

What the state contains; which data stay unchanged; which actions are legal; the goal; and how cost is computed.

**TRACE**

Which node leaves the frontier; which priority selects it; which successors are generated; which arrivals are discarded.

**JUSTIFICATION**

Why each step is valid; how to reconstruct the path; which property establishes optimality.

**LIMITS**

What depends on an assumption; when the result is only cutoff; when impossibility is proved.

**Formulate → Simulate → Reconstruct → Verify → Explain**


*Source / scope note:* Teaching summary of the method in AIMA Chapter 3, applied to the supplied worksheets.


---

# Page 54 — What is preserved and what was completed

The book provides the algorithmic foundations; the statements define each instance.

**Material** | **Use in this guide**

AIMA, 3rd ed. | Chapter 3: problems, search and heuristics. Chapter 4: local search.

Unit2 / Unit1 / CourseIntroduction | Notation and teaching approach; vacuum-world, explorers and puzzle examples.

MisionariesAndCannibals / CurrencyExchange

Statements and formalisation; completing successors and developing solutions.

ExampleDLSProblemPruning | Repeated states at shallower depths; the worksheet uses −d as priority.

Grafo2.zip / grafoclase | Different instances: 0→9 and 0→10, respectively. Transcribed edges.

Pinpong / espacioestadoIslas | Ping-pong: original impossibility proof. Islands: explicit safety assumptions.

hw1soln | Goal 13, A* from Oradea, local search and the 4×4 Column Jump example.

The generated package excludes the original PDFs and the book. The guide's figures were prepared for this explanation.


*Source / scope note:* Source correspondence record. Data, assumptions and original calculations are explicitly distinguished.


---

# Page 55 — How to use this in the classroom

Teaching proposal; it does not change the course's official assessment or schedule.

**Stage** | **Brief activity**

Before the algorithm | Ask two students to propose a state representation.

During the trace | Hide the next page; vote on which node will be popped next.

After a path | Review cost, constraints and why a better path might exist.

When no goal is found | Contrast ping-pong with DLS cutoff: they lead to different conclusions.

To conclude | Compare BFS/UCS/A* without changing the graph, goal or tie-breaking.

**The idea connecting every exercise**

A search is only as correct as the model it explores and the rules it uses to discard paths.


*Source / scope note:* Original proposal for using this material. Numerical checks apply only to the included instances.
