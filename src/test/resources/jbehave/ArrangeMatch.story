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
