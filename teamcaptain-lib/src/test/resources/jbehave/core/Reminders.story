Narrative Players are sent reminders if they don't respond

Meta:
@include

Scenario: a player does not respond, reminders are sent
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe then accepts
When time elapses till 5 days before the match
Then a reminder is sent to john and peter
And time elapses till 4 days before the match
Then a reminder is sent to john and peter 

Scenario: a player does not respond, reminders are sent
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe then accepts
And time elapses till 5 days before the match
And a reminder is sent to john and peter
When john then accepts
And time elapses till 4 days before the match
And a reminder is sent to peter
And a reminder is not sent to john

Scenario: a standby reminder is sent to players on standby notification who have failed to respond
Given a competition requires 2 men and 1 lady per match
And the player pool consists of joe, john, peter, jed, stacy
And a match is scheduled and is in the selection window
And joe and stacy then accepts
And time elapses till 4 days before the match
And a reminder is sent to john
And time elapses till 3 days before the match
And a reminder is sent to john and peter
