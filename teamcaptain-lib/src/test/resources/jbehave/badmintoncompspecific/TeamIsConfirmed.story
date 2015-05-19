Narrative A team for the match is confirmed

Scenario: a team is confirmed, all players received details
Given a match is scheduled and is in the selection window
When sufficient players are assigned to the match
Then a match confirmation notification is sent out to all notified players
And the confirmation contains the list of players assigned to the match
And the confirmation contains the match details
And the team captain is notified of the match confirmation message from the system

Scenario: the match is confirmed, confirmed standby players are stood down
Given a match is scheduled and is in the selection window
And all but 2 first pick players responds
And time elapses till 4 days before the match
And all standby players accept
When all outstanding selected players accept
Then the match is confirmed
And all confirmed standby players are stood down and notified
 
Scenario: the match is confirmed, players notified for standby are stood down
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 3 days before the match
When all outstanding selected players accept
Then the match is confirmed
Then all unconfirmed standby players are stood down and notified
