Scotland Yard Prototype (Individual Reassessment )

COM5033 (Surrender feature)

The main requirement was to implement a surrender feature for fugitive (Mr.X) which is a brand new feature that allows the the user to leave/end game early if the fugitive sees no chance of winning.

Steps,

Fugitive clicks surrender —> Decision Screen
Option “Do you want to Surrender this will cause you to lose the game”    Yes or no

NO —> return to game.       Yes —> Go to losing Screen.

If “YES” Server calls for fugitive Surrender confirmation , fugitive goes to LoserScreen —>  You lost due to surrender! Detectives won.

Option “Return to main menu”


Setup Info

1. Go to the GitHub repo: https://github.com/Ghaith1231/Scotland-Yard-Res
2. Download the project (ZIP or clone with Git)
3. Open the Scotland-Yard-Prototype project in Android Studio
4. Use OpenJDK 24
5. Set Android API level 16–36
6. Build and Run the app




New Implementations added

UI: SurrenderDecision Activity with a yes or no option to prevent fugitive from surrendering accidentally

Logic: Only fugitive can surrender/visually see the feature

API calls: on Surrender broadcastGameOverTask is made which announces detectives win

Integration: clients that detects game winner over a poll

Screens: Fugitive LoserScreen —— Detectives WinnerScreen



Agile Evidence

Trello Link —>  https://trello.com/b/1KEfrtfK/group-project-nick

Github Link —> https://github.com/Ghaith1231/Scotland-Yard-Res


Presentation video

Video Link —> https://youtu.be/DEKNDo-6sHc





AI Statement

The overall AI contribution is estimated at 10–15%, well within the 20% allowance.

(Structuring the BroadcastGameOverTask logic.