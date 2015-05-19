Narrative Team selections isn't going well, the captain is alerted

Scenario: there are insufficient eligible players
Given a match is scheduled and is in the selection window
When there are insufficient eligible players to fulfill the match
Then the team captain is notified of the insufficient players message from the system

Scenario: a team is not confirmed 3 days before the match, all players are notified of status 
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
When time elapses till 3 days before the match
Then all notified players who have not declined are sent a detailed match status

 
 