# DiscordSamuraiBot

## About

This bot don't do much of anything

###Overview
All relevant code is stored in src\DreadMoirais\Samurais
- Main class BotListener.java
	`This class listens to events from discord and responds appropriately`

#### Required
uses JDA

https://github.com/DV8FromTheWorld/JDA/

Download the latest Beta version with dependencies

## Authors

DreadMoirai

## Functions

What can I do?!
#### Responds to:
<i> All commands are case-insensitive</i>
 - `who am i?`
 - `!stat`
 - `@samurai flame @user` - to be revised

###### In Progress:
 - <i>"duel"</i>
 - <i>"!shutdown"</i>

## To Do:
 - [ ] Remove Bots from dataFiles
 - [ ] Standardize inputs into classic !command

 - [ ] Simplify @Override methods to increase clarity of commands
 	 - [ ] Delegate method bodies to helper methods	

 - [ ] Add two more variations of Game
	 - [ ] Custom emojis to select game?	
 - [ ] Add File input and output
	 - [ ] Goal: Merge binary files and send back to guild
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
