SOLVE, SEE AND VERIFY — ENGLISH EDITION
Intelligent Systems · Visual Search Solutions

This package translates the previously prepared Spanish solution guide and
its trace notebook. It preserves their organisation, examples, assumptions,
qualifications, numerical results and source references. It is not a
translation of the whole AIMA textbook or a new analysis of every question
in the original course units.

START HERE
Open Visual_Search_Solutions_AIMA_EN.pdf.
It contains 55 landscape pages, a clickable contents page (page 2), 18
bookmarks, vector diagrams and pause questions. CONTENTS in the footer
returns to the contents page.

COMPLETE TRACES
Open Complete_Search_Traces_EN.html in a browser. It contains 13 traces
and 171 pop steps. Open a step to inspect the frontier, closed states,
generated nodes and parent pointers. All interface text and explanatory
notes are in English. No external scripts, libraries or online resources
are loaded. Keep the HTML and PDF in the same folder to preserve the
links to the corresponding PDF pages.

EDITABLE TEXT
English_Text_AIMA.txt provides the complete English text for copying and
editing. English_Text_AIMA.md provides the same text with headings and
lightweight formatting. Both follow the PDF's 55 page sections and retain
figure labels. They are text companions, not replacements for the visual
layout: use the corresponding PDF page to interpret arrows, trees and
board arrangements. A series of figure labels is not itself a path.

PDF CONTENTS
 3–5   Method, states versus nodes, and search conventions.
 6–8   Coin change: 15 cents.
 9–13  Missionaries and cannibals.
14–16  Wolf, sheep, cabbage and boatman.
17–18  Ping-pong: formulation and impossibility proof.
19–21  DLS and reopening at shallower depths.
22–28  Grafo2: BFS, UCS, DLS with limit 7, Greedy and A*.
29–32  grafoclase: paths, costs, heuristics and adjacency lists.
33–34  Number tree with goal 13.
35–37  Romania: Oradea and comparison with Arad.
38–39  Vacuum world and state counts.
40–41  Explorers and the 100 kg capacity.
42–45  Unit2's 8-puzzle: 20 moves.
46–49  Column Jump: 4×4 board and eight jumps.
50–52  Best-first and limiting cases of local search.
53–55  Summary, sources and teaching notes.

NOTATION
The island labels remain L=wolf, O=sheep, C=cabbage and B=boatman,
matching the original source. Source filenames and identifiers such as
Sucesor, IslaA and IslaB remain unchanged where they identify the supplied
materials or their notation. A node id is not a state. d is depth, g is
accumulated cost, h is the heuristic, and f is the priority/evaluation.

VERIFICATION DATA
Verification_Data_EN.json retains the numerical data, paths, states,
heuristics, edges and traces from the Spanish package. Only textual
event/action labels were translated: EXPANDE→EXPAND, META→GOAL,
LÍMITE→LIMIT, REABRE→REOPEN and Solo→Alone. CUT remains unchanged.
Verification_EN.txt records checks performed on this English edition.
SHA256SUMS.txt records file integrity hashes.

SCOPE AND QUALIFICATIONS
The source guide's caveats are preserved: Unit2 and AIMA's specific BFS
use different goal-test timings; graph instances have different goals;
island safety and explorer cargo assumptions are explicit; missing
Column Jump benchmark files do not acquire invented running times.

The package does not include the original worksheets, the AIMA textbook
or standalone font files. The Spanish edition remains a separate file.
