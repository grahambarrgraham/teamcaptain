Narrative: Out of band, (i.e. unexpected messages). If possible, the team captain is notified.

Scenario: a player declines, then subsequently confirms, but a substitute has been selected  
Given a match is scheduled and is in the selection window
And a selected player with an eligible substitute declines
When that selected player then accepts
Then the team captain is notified of the unexpected message from that selected player

Scenario: a pool player whos has not been selected sends a message  
Given a match is scheduled and is in the selection window
When an unselected substitute player accepts
Then the team captain is notified of the unexpected message from the substitute player

Scenario: a confirmed player subsequently declines
Given a match is scheduled and is in the selection window
And a selected player accepts the match
When the player subsequently declines
Then the team captain is notified of the unexpected message from that selected player

