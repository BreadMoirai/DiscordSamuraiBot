/*
 *     Copyright 2017-2018 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.breadmoirai.samurai.plugins.games.connect4;

import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;
import com.github.breadmoirai.breadbot.framework.annotation.parameter.Author;
import com.github.breadmoirai.samurai.plugins.games.connect4.strategy.MiniMaxStrategy;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.OptionalDouble;

public class ConnectFourCommand {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE};

    @MainCommand
    public ConnectFourGame connect4(JDA jda, @Author Member player, Member opponent, OptionalDouble wager, DerbyPointPlugin plugin) {
        if (opponent.getUser().equals(jda.getSelfUser())) {
            return new ConnectFourGame(player, new MiniMaxStrategy(ConnectFourGame.X_BOUND, ConnectFourGame.Y_BOUND, 4), );
        }
    }
}
