Narrative Its getting close to the match and several Standby players are selected to standby  

Scenario: original and standby player both fail to respond, next standby is notified
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 4 days before the match
When time elapses till 3 days before the match
Then a standby notification goes out to the 2nd next appropriate player in the pool

Scenario: unconfirmed standbys are stood down before confirmed standbys
Given a match is scheduled
And it is 10 days before the match
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 4 days before the match
And time elapses till 3 days before the match
And the 2nd standby player the standby request
When the original player declines
Then the 2nd standby player is selected
And the 1st standby player is stood down

Scenario: standby acceptance stands down lowest ranked unconfirmed standby
Given a match is scheduled and is in the selection window
And all but 2 equivalent first pick players responds
And time elapses till 3 days before the match
When one of the outstanding players accepts
Then the 1st standby player is selected
And the 2nd standby player is stood down

Scenario: standby acceptance stands down lowest ranked confirmed standby
Given a match is scheduled and is in the selection window
And all but 2 equivalent first pick players responds
And time elapses till 4 days before the match
And the 1st standby player accepts
And the 2nd standby player accepts
When one of the outstanding players accepts
Then the 1st standby player is selected
And the 2nd standby player is stood down
