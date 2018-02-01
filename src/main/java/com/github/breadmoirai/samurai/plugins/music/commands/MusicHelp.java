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

package com.github.breadmoirai.samurai.plugins.music.commands;

import com.github.breadmoirai.breadbot.framework.annotation.Name;
import com.github.breadmoirai.breadbot.framework.annotation.command.MainCommand;

public class MusicHelp {

    @MainCommand
    @Name("MusicHelp")
    public String music() {
        return "**Music Commands**\n" +
                "`!queue <link>` - see Supported Websites below\n" +
                "`!queue [search terms]` - this will search youtube\n" +
                "`!queue sc [search terms]` - this will search soundcloud\n" +
                "`!play [search terms]`- this will queue the first search result from youtube\n" +
                "`!play sc [search terms]`- this will queue the first search result from soundcloud\n" +
                "`!nowplaying` - view the current song and the queued songs\n" +
                "`!skip` - skip the currently playing song\n" +
                "`!prev` - go back to the previous song\n" +
                "`!history` - view what songs have recently been played\n" +
                "\n" +
                "`!related` - search youtube for tracks related to song. \n" +
                "                     Only works when the current song is from youtube.\n" +
                "**Playlist Commands**\n" +
                "`!select [indexes/words]` - select the specified indexes or tracks that include the specified words\n" +
                "`!remove [indexes/words]` - remove the specified indexes or tracks that include the specified words\n" +
                ":twisted_rightwards_arrows: shuffle - shuffle order of playlist\n" +
                ":eject: cancel - cancel loading playlist\n" +
                ":arrow_forward: queue - confirm your selection and adds all songs to queue\n" +
                ":left_right_arrow: change page - view the next page of songs\n" +
                "**AutoPlay**\n" +
                "`!autoplay` `true`/`false` - AutoPlay is only triggered if the last played AudioTrack is from YouTube\n" +
                "**Voice Channel Commands**\n" +
                "`!join`\n" +
                "`!leave`\n" +
                "**Supported Websites**\n" +
                "YouTube\n" +
                "SoundCloud\n" +
                "BandCamp\n" +
                "NicoNico\n" +
                "Vimeo\n" +
                "Twitch\n" +
                "\n" +
                "Also any url that points directly to an audio file\n" +
                "ex. https://gensokyoradio.net/GensokyoRadio.m3u\n" +
                "or http://www.freesound.org/data/previews/367/367153_2188-hq.mp3\n";
    }
}
