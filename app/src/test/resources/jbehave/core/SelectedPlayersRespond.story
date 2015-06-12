Narrative A match is scheduled and selected players respond

Meta:

Scenario: a notification email is sent out to the first pick players
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
When a match is scheduled and is in the selection window
Then joe, john and stacy are selected

Scenario: a player can play
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
When a match is scheduled and is in the selection window
And joe, john and stacy are selected and accept
Then an acceptance confirmation is sent to joe, john and stacy 

