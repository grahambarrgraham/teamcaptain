Narrative Team selections isn't going well, the captain is alerted

Meta:
@include

Scenario: there are insufficient eligible players
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And stacy then declines 
Then the team captain is notified of the insufficient players message from the system

Scenario: a team is not confirmed 3 days before the match, all players are notified of status
Meta:
@includeonly
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe then accepts
When time elapses till 2 days before the match
Then joe, john, peter and stacy are sent a detailed match status

 
 