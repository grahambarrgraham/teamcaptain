Narrative Standby players are selected to standby if the selected players don't respond  

Scenario: a player continues not to respond, so a standby player is selected to standby
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
When time elapses till 4 days before the match
Then a standby notification goes out to the next appropriate player in the pool
And an administrator standby alert is raised

Scenario: a standby player accepts, and then the original player confirms in time, original player is selected, standby is stood down
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 4 days before the match
And the next appropriate player accepts the standby request
When time elapses till 3 days before the match
Then the outstanding player is selected
And the standby player is stood down

Scenario: player who has declined standby cannot be selected to play
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 4 days before the match
And the standby player declines
When all picked players decline
Then the declined standby is not selected 

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

Scenario: acceptance stands down lowest ranked unconfirmed standby
Given a match is scheduled and is in the selection window
And all but 2 equivalent first pick players responds
And time elapses till 3 days before the match
When one of the outstanding players accepts
Then the 1st standby player is selected
And the 2nd standby player is stood down

Scenario: acceptance stands down lowest ranked confirmed standby
Given a match is scheduled and is in the selection window
And all but 2 equivalent first pick players responds
And time elapses till 4 days before the match
And the 1st standby player accepts
And the 2nd standby player accepts
When one of the outstanding players accepts
Then the 1st standby player is selected
And the 2nd standby player is stood down

