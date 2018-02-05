/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.github.breadmoirai.samurai.plugins.points;

import net.dv8tion.jda.core.OnlineStatus;

import java.time.Instant;

public class PointSession {
    private final PointExtension database;
    private final long id;

    private OnlineStatus status;
    private double points;

    private Instant lastMessageSent;

    public PointSession(long id, double points, OnlineStatus status, PointExtension database) {
        this.id = id;
        this.points = points;
        this.status = status;
        this.database = database;
    }

    public long getId() {
        return id;
    }

    public double getPoints() {
        return points;
    }

    public Instant getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(Instant lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    public void commit() {
        database.setPoints(id, points);
    }

    public void offsetPoints(double offset) {
        points += offset;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus status) {
        this.status = status;
    }
}
