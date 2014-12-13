Scenario: a notification email is sent out to the first pick players
Given a match is scheduled
When it is 10 days before the match
Then an availability notification is sent to the first pick members

Scenario: a player can play
Given a match is scheduled
And notifications have been sent out to the proposed team members
When a team member acknowledges their availability
Then they are assigned to the match
And an acceptance acknowledgement notification goes to the player

Scenario: player cannot play
Given a match is scheduled
And notifications have been sent out to the first pick players
When a player responds that they are not available
Then a notification goes out to the next appropriate player in the pool
And a decline acknowledgement notification goes to the player

Scenario: a player does not respond
Given a match is scheduled
And it is 10 days before the match
And all but one first pick players responds
When times elapses till the match
Then a daily reminder is sent to the non-responding player from 7 days before the match  

Scenario: a player responds after being reminded
Given a match is scheduled
And it is 10 days before the match
And all but one first pick players responds
And times elapses till the 5 days before the match
When the remaining team member acknowledges their availability
Then no further reminders are sent to the player  

Scenario: a player continues not to respond
Given a match is scheduled
And it is 10 days before the match
And all but one first pick players responds
When times elapses till the 4 days before the match
Then a standby notification goes out to the next appropriate player in the pool
And an administrator alert is raised

Scenario: a team is confirmed
Given a match is scheduled
When sufficient players are assigned to the match  
Then a match confirmation notification is sent out to all notified players 
And the confirmation contains the list of players assigned to the match 
And the confirmation contains the match details
And an administration confirmation notification is raised
