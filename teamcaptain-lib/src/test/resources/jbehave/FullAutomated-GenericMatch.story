
Scenario: a player declines, then subsequently confirms, but a substitute has been selected  
Given a match is scheduled and is in the selection window
And a selected player with an eligible substitute declines
When that selected player then accepts
Then they are notified that they are now a confirmed standby player

Scenario: a player declines, then subsequently confirms, but no substitute is available 
Given a match is scheduled and is in the selection window
And a selected player with no eligible substitute declines
When that selected player then accepts
Then they are assigned to the match

Scenario: a player declines, a substitute declines then the original player confirms   
Given a match is scheduled and is in the selection window
And a selected player with an eligible substitute declines
And the selected substitute player declines
When that selected player then accepts
Then they are assigned to the match 
