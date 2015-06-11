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
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe then accepts
When time elapses till 2 days before the match
Then joe, john, peter and stacy are sent a detailed match status
And the team captain is sent a detailed match status

Scenario: the match date passes unconfirmed, all except declining players are notified it has passed
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And john then declines
And time elapses till 3 days before the match
And peter then declines 
And time elapses till 0 days before the match
Then joe, jed and stacy are sent a detailed match status
And the team captain is sent a detailed match status
 
