Narrative The match date passes

Meta:
@wip

Scenario: the match date passes, all selected players (including the standby) are notified it has passed, and notes player status
Given a match is scheduled and is in the selection window
And all selected players accept except one, and that player has an eligible substitute in the pool
When time elapses till after the match
Then all notified players who have not declined are sent a detailed match status with completed status
 