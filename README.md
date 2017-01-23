# DiscordSamuraiBot

## About

This bot don't do much of anything

###Overview
/All relevant code is stored in src\DreadMoirais\Samurais
- Main class BotListener.java
	`This class listens to events from discord and responds appropriately`

#### RequiredForDevelopment
uses JDA

https://github.com/DV8FromTheWorld/JDA/

Download the latest Beta version with dependencies
## Authors

DreadMoirai

## Functions

What can I do?!
#### Responds to:
<i> All commands are case-insensitive</i>
 - `!stat`*
 - `!roll`
 - `!duel`**
 - `!flame`**
 - `!upload`<sup>†</sup>
 - `!getScores`<sup>†</sup>
 - `!shutdown`
 * <sub>@mention optional</sub>  
 ** <sub>@mention required&#13;&#10;</sub> 
 † <sub>Under Construction&#13;&#10;</sub> 

## To Do:	
 - [ ] Restrict !Shutdown permissions
 - [ ] Add File input and output `scores.db`
	 - [ ] Merge binary files and send back to guild
	 - [ ] take file data and rename to match user
	 - [ ] analyze file data and transfer to SamuraiStats
	 - [ ] expand saveData to correlate users
	 - [ ] Add methods 
		- [ ] !lowscore
		- [ ] !noscore
		- [ ] !getScore //requires Osu!API & osu.db
 - [ ] Remove Bots from dataFiles
 - [x] Added empty hangman class for game
    - [ ] Complete and Implement Duel.Hangman
    - [ ] Custom emojis to select game?
 - [x] Standardize inputs into classic !command
 - [x] Simplify @Override methods to increase clarity of commands
 	- [x] Delegate method bodies to helper methods	
 - [x] Complete Connect 4.
 	- [x] responds to reactions
 - [x] Condense BotData and helper classes: Stat, UserStat 
	 - [x] collapse into inner classes 
	 - [x] Add duel data	
 - [x] Correct saveData()


## Contributing

1. Fork it!

2. Create your feature branch: `git checkout -b my-new-feature`

3. Commit your changes: `git commit -m 'Add some feature'`

4. Push to the branch: `git push origin my-new-feature`

5. Submit a pull request


## History

Created 1/14/2017
