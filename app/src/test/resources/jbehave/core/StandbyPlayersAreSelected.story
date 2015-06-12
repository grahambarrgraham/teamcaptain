Narrative Standby players are selected to standby if the selected players don't respond  

Meta:
@include

Scenario: a player continues not to respond, so a standby player is selected to standby
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accept
And time elapses till 4 days before the match
And peter is selected to standby and accepts

Scenario: a standby player accepts, and then the original player confirms in time, original player is selected, standby is stood down
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accept
And time elapses till 4 days before the match
And peter is selected to standby and accepts
When john then accepts
Then the match is confirmed with joe, john and stacy
And peter is stood down

Scenario: original and standby player both fail to respond, next standby is notified
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accept
And time elapses till 4 days before the match
And peter is selected to standby
When time elapses till 3 days before the match
Then jed is selected to standby

Scenario: unconfirmed standbys are stood down before confirmed standbys
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accept
And time elapses till 4 days before the match
And peter is selected to standby
And time elapses till 3 days before the match
When jed is selected to standby and accepts
Then peter is stood down


