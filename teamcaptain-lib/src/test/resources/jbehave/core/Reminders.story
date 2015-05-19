Narrative Players are sent reminders if they don't respond

Scenario: a player does not respond
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
When time elapses till the match
Then a daily reminder is sent to the non-responding player from 7 days before the match

Scenario: a player responds after being reminded
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 5 days before the match
When the remaining team member acknowledges their availability
Then no further reminders are sent to the player
 
Scenario: a standby reminder is sent to players on standby notification who have failed to respond
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
When time elapses till 3 days before the match
Then a standby reminder goes out to the next appropriate player in the pool