Scenario: a notification email is sent out to the first pick players
Given a match is scheduled and is in the selection window
Then an availability notification is sent to the first pick members

Scenario: a player can play
Given a match is scheduled and is in the selection window
And notifications have been sent out to the proposed team members
When a team member acknowledges their availability
Then they are assigned to the match
And an acceptance acknowledgement notification goes to the player

Scenario: player cannot play
Given a match is scheduled and is in the selection window
And notifications have been sent out to the first pick players
When a player responds that they are not available
Then a notification goes out to the next appropriate player in the pool
And a decline acknowledgement notification goes to the player

Scenario: a player does not respond
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till the match
Then a daily reminder is sent to the non-responding player from 7 days before the match  

Scenario: a player responds after being reminded
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 5 days before the match
When the remaining team member acknowledges their availability
Then no further reminders are sent to the player  

Scenario: a player continues not to respond
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till 4 days before the match
Then a standby notification goes out to the next appropriate player in the pool
And an administrator standby alert is raised

Scenario: a team is confirmed
Given a match is scheduled and is in the selection window
When sufficient players are assigned to the match  
Then a match confirmation notification is sent out to all notified players 
And the confirmation contains the list of players assigned to the match 
And the confirmation contains the match details
And an administration confirmation notification is raised

Scenario: a player is on holiday and is is not eligible to play
Given a match is scheduled
And a member of the pool is on holiday on the date of the match
When it is 10 days before the match
Then the player on holiday is not notified
And a notification goes out to the next appropriate player in the pool

Scenario: there are insufficient eligible players
Given a match is scheduled and is in the selection window
When there are insufficient eligible players to fulfill the match
Then an administrator insufficient players alert is raised

Scenario: a player declines, then subsequently confirms  
Given a match is scheduled and is in the selection window
And a selected player declines
When a selected player accepts
Then they become eligible again and are returned to the pool as the highest ranked substitute
And they are notified that they are eligible again

Scenario: a player declines, then subsequently confirms, and is subsequently picked  
Given a match is scheduled and is in the selection window
And a selected player declines
When a selected player accepts
And the next appropriate player declines 
Then they are assigned to the match 
 
Scenario: a standby player confirms, and then the original player declines, standby player is selected
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
When the next appropriate player accepts
And the outstanding player declines
Then the standby player is selected 

Scenario: a standby player confirms, and then the original player fails to respond 2 days before match, standby player is selected
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
And the next appropriate player accepts the standby request
When time elapses till 2 days before the match
Then the next appropriate player is selected 
And the outstanding player is automatically declined
And the outstanding player is notified of the automatic decline

Scenario: a standby player confirms, and then the original player confirms in time, original player is selected, standby is stood down
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
And the next appropriate player accepts the standby request
When time elapses till 3 days before the match
Then the outstanding player is selected
And the standby player is stood down

Scenario: original and standby player both fail to respond, next standby is notified
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
When time elapses till 3 days before the match
Then a standby notification goes out to the 2nd next appropriate player in the pool

Scenario: unconfirmed standbys are stood down before confirmed standbys
Given a match is scheduled
And it is 10 days before the match
And all but one first pick players responds
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

Scenario: a confirmed player can subsequently decline
Given a match is scheduled and is in the selection window
And a selected player accepts
When the player subsequently declines
Then a notification goes out to the next appropriate player in the pool
And a decline acknowledgement notification goes to the player
 
Scenario: an unconfirmed standby who has been stood down can be repicked for standby
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
And the standby player accepts
And the original player accepts
And time elapses till 3 days before the match
And the original player declines
And a standby notification goes out to the standby player
 
Scenario: player who has declined standby cannot be selected to play
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 4 days before the match
And the standby player declines
When all picked players decline
Then the declined standby is not selected 

Scenario: a standby reminder is sent to players on standby notification who have failed to respond
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till 3 days before the match
Then a standby reminder goes out to the next appropriate player in the pool

Scenario: the match is confirmed, confirmed standby players are stood down
Given a match is scheduled and is in the selection window
And all but 2 first pick players responds
And time elapses till 4 days before the match
And all standby players accept
When all outstanding selected players accept
Then the match is confirmed
Then all confirmed standby players are stood down and notified
 
Scenario: the match is confirmed, players notified for standby are stood down
Given a match is scheduled and is in the selection window
And all but one first pick players responds
And time elapses till 3 days before the match
When all outstanding selected players accept
Then the match is confirmed
Then all unconfirmed standby players are stood down and notified

Scenario: the match is not confirmed 3 days before the match, all players are notified of status 
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till 3 days before the match
Then all notified players who have not declined are sent a detailed match status

Scenario: the match date passes, all players are notified it has passed, and notes player status
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till after the match
Then all notified players who have not declined are sent a detailed match status with completed status.
 
Scenario: the match date passes, selected players who accept are notified that the match has passed
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till after the match
Then the outstanding player is notified that the match has passed 

Scenario: the match date passes, selected players who accept are notified that the match has passed
Given a match is scheduled and is in the selection window
And all but one first pick players responds
When time elapses till after the match
Then the standby player is notified that the match has passed 

Scenario: Admin alerts contain full match and all player statuses
Scenario: Admin alerts contain full event notification history for the match  
Scenario: a standby player declines, and then the original player confirms
Scenario: player who has declined standby cannot be selected for standby again
 
 
 