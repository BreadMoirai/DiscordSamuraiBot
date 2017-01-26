# DiscordSamuraiBot

## About

This bot don't do much of anything

###Overview
/All relevant code is stored in src\DreadMoirais\Samurais
- Main class BotListener.java
	`This class listens to events from discord and responds appropriately`

[[https://github.com/Mansfew/DiscordSamuraiBot/blob/master/src/diagram/png|alt=diagram]]

#### RequiredForDevelopment
uses JDA

https://github.com/DV8FromTheWorld/JDA/

Download the latest Beta version with dependencies
## Authors

DreadMoirai

## Functions

What can I do?!
### Responds to:
<i> All commands are case-insensitive</i><br />
##### Basic Commands:
 - `!stat [@mention]`*
 - `!roll [upperLimit]`*
 - `!duel [@mention]`
 - `!flame [@mention]`
 - `!shutdown`
##### Osu-related Commands
 - `!osu [username]`
 - `!initialize`
 	- `!upload`
	- `!beatmap` 
 	- `!getScores`†
<br />
 \* <sub>argument optional</sub><br />
 † <sub>Under Construction&#13;&#10;</sub> <br />

## To Do List:	
 - [ ] Restrict !Shutdown permissions
 - [ ] Integrate Osu!
	 - [x] Add file Parser
	 - [x] Add Enums
	 - [ ] create File Writer
	 - [ ] Merge binary files and send back to guild
	 - [x] take file data and rename to match user
	 - [x] analyze file data and transfer to SamuraiStats
	 - [x] add custom emojis for score_, rank_, and combo_
	 - [ ] expand saveData to correlate users
	 - [ ] Add methods
		- [x] !beatmap //getRandomMap
			- [x] Reaction Interactable
		- [ ] !lowscore
		- [ ] !noscore
		- [ ] !getScore //requires Osu!API & osu.db
 - [x] Retrieve User Information from Osu
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
