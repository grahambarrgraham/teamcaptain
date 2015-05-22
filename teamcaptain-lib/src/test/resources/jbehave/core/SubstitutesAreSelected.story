Narrative A player cannot play and so a substitute is selected

Scenario: player cannot play
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
When joe then declines
Then a decline acknowledgement goes to joe
And a selection notification is sent to peter  

Scenario: a player is on holiday and is is not eligible to play
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And john is on holiday on the date of the match
And a match is scheduled
Then a selection notification is sent to joe, peter and stacy

Scenario: a standby player confirms, and then the original player fails to respond 2 days before match, standby player is selected
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accept
And time elapses till 4 days before the match
And peter is selected to standby and accepts
When time elapses till 2 days before the match
Then the match is confirmed with joe, peter and stacy 

