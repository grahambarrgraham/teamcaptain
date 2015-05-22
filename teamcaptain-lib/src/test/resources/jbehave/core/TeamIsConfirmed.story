Narrative A team for the match is confirmed

Scenario: a team is confirmed, all players received details
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window 
When joe and john and stacy then accept
Then the match is confirmed with joe, john and stacy

Scenario: the match is confirmed, confirmed standby players are stood down
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window 
And stacy is selected and accepts
And time elapses till 4 days before the match
And peter and jed are selected to standby and accept
When joe and john then accept
Then the match is confirmed with joe, john and stacy
And peter and jed are stood down

 
Scenario: the match is confirmed, players notified for standby are selected 
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window 
And stacy and john then accept
And time elapses till 3 days before the match
When peter is selected and accepts
Then the match is confirmed with stacy, peter, and john
 
