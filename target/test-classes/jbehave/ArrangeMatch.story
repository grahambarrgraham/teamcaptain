Scenario: a notification email is sent out to the first pick players
Given a match is scheduled
When it is 10 days before the match
Then an availability notification is sent to the first pick members

Scenario: a player can play
Given a match is scheduled
And notifications have been sent out to the proposed team members
When a team member acknowledges their availability
Then they are assigned to the match
And an acknowledgement notification goes to the player

Scenario: player cannot play
Given a match is scheduled
And notifications have been sent out to the first pick players
When a player responds that they are not available
Then a notification goes out to the next appropriate player in the pool
And an acknowledgement notification goes to the player

Scenario: a player does not respond
Given a match is scheduled
And it is at least 4 days before the match  
And more than 1 day has elapsed since a notification was sent to the player
When a team member fails to acknowledges their availability 
Then a reminder notification is sent to the player

Scenario: a player continues not to respond
Given a match is scheduled
And it is less than 4 days before the match
And more than 1 day has elapsed since a notification was sent to the player
When a team member fails to acknowledges their availability 
Then a reminder notification is sent to the player
Then a reserve notification goes out to the next appropriate player in the pool
And an administrator alert is raised

Scenario: a team is confirmed
Given a match is scheduled
When sufficient players are assigned to the match  
Then a match confirmation notification is sent out to all notified players 
And the confirmation contains the list of players assigned to the match 
And the confirmation contains the match details
And an administration notification is raised

Scenario: a player is on holiday and is is not eligible to play
Given a match is scheduled
And a member of the pool is on holiday on the date of the match
When players are notified
Then the player on holiday is not notified 
   
Scenario: a player is injured and so is not eligible to play
Given a match is scheduled
And a member of the pool is injured
When players are notified
Then the injured player is not notified 
 
Scenario: there are insufficient eligible players
Given a match is scheduled
And an there are insufficient eligible players to fulfill the match
When it is 10 days before the match
Then an administrator alert is raised

Scenario: Cambridge League Mixed Badminton
Given a mixed doubles match is scheduled
When players are chosen
Then the 3 strongest eligible ladies are chosen from the pool
And the 3 strongest eligible men are chosen from the pool

Scenario: Cambridge League Mens Badminton
Given a Cambridge league mens doubles badminton match is scheduled
When players are chosen
Then the 6 strongest eligible and available men are chosen from the pool

Scenario: Cambridge League Ladies Badminton
Given a Cambridge league mens doubles badminton match is scheduled
When players are chosen
Then the 6 strongest eligible and available ladies are chosen from the pool

Scenario: Cambridgeshire County Team Nominated Team
Given a Cambridge county match is scheduled
When players are chosen
Then the nominated players are chosen first

Scenario: Cambridgeshire County Team substitute gender
Given a Cambridge county match is scheduled
When players are chosen
Then the nominated ladies are chosen
And the nominated men are chosen
 
Scenario: Cambridgeshire County Team substitutes
Given a Cambridge county match is scheduled
And a nominated player is unavailable
When players are chosen
Then the next strongest player of the same gender is chosen 


