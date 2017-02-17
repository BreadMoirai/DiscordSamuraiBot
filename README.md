# DiscordSamuraiBot

OUTDATED

## About
This bot don't do much of anything

### Overview


#### RequiredForDevelopment
uses JDA

https://github.com/DV8FromTheWorld/JDA/

Uses Gradle

## Authors

DreadMoirai

## Functions

What can I do?!
### Responds to:
<i> All commands are case-insensitive</i><br />
#### Basic Commands:
 - `status` -displays status of bot
 - `duel..` -starts a connectFour game
 - `prefix` -changes the command prefix. Must be 4 characters or less
 - `uptime` -displays running time of bot
 - `help..` -displays help. Mentioning the bot will also display help
 - `guild.` -invite link for DreadMoirai's Samurais
 - `invite` -link to add Samurai to your server


## Contributing

1. Fork it!

2. Create your feature branch: `git checkout -b my-new-feature`

3. Commit your changes: `git commit -m 'Add some feature'`

4. Push to the branch: `git push origin my-new-feature`

5. Submit a pull request

!!!
Highly recommend using [IntelliJ](https://www.jetbrains.com/idea/) 



## History

Created 1/14/2017

## To Do List:
 - [ ] catch permission exceptions
 - [ ] make cross guild compatable
 - [ ] Track api requests
 - [ ] Create custom save files for beatmap info.	
 - [ ] Restrict !Shutdown permissions
 - [ ] Integrate Osu!
	 - [x] Beatmap Embed
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
