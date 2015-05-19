Narrative A match is scheduled and selected players respond

Scenario: a notification email is sent out to the first pick players
Given a match is scheduled and is in the selection window
Then an availability notification is sent to the first pick members

Scenario: a player can play
Given a match is scheduled and is in the selection window
And notifications have been sent out to the proposed team members
When a selected player accepts the match
Then they are assigned to the match
And an acceptance acknowledgement notification goes to the player

