Narrative A player cannot play and so a substitute is selected

Scenario: player cannot play
Given a match is scheduled and is in the selection window
And notifications have been sent out to the first pick players
When a selected player with an eligible substitute declines
Then a notification goes out to the next appropriate player in the pool
And a decline acknowledgement notification goes to the player

Scenario: a player is on holiday and is is not eligible to play
Given a match is scheduled
And a member of the pool is on holiday on the date of the match
When it is 10 days before the match
Then the player on holiday is not notified
And a notification goes out to the next appropriate player in the pool

Scenario: a standby player confirms, and then the original player fails to respond 2 days before match, standby player is selected
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
And time elapses till 4 days before the match
And the next appropriate player accepts the standby request
When time elapses till 2 days before the match
Then the standby player is selected
And the outstanding player is automatically declined
And the outstanding player is notified of the automatic decline

 