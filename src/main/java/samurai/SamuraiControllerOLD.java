
//    //debugging
//    private void getScoreList(MessageReceivedEvent event) {
//        long guildId = Long.parseLong(event.getGuild().getId());
//        if (osuGuildMap.get(guildId).getScoreMap().isEmpty()) {
//            event.getChannel().sendMessage("No scores found. Try using `!upload` as a comment with a `scores.db` file found in your osu installation folder.").queue();
//            return;
//        }
//        MessageBuilder messageBuilder = new MessageBuilder().append("```\n");
//        HashMap<String, AtomicInteger> nameScore = new HashMap<>();
//        for (LinkedList<Score> scoreList : osuGuildMap.get(guildId).getScoreMap().values()) {
//            for (Score score : scoreList) {
//                if (!nameScore.containsKey(score.getPlayer()))
//                    nameScore.put(score.getPlayer(), new AtomicInteger(0));
//                nameScore.get(score.getPlayer()).incrementAndGet();
//            }
//        }
//        for (String name : nameScore.keySet()) {
//            messageBuilder.append(String.format("%-30s", name.replace(" ", "_")).replace(" ", ".").replace("_", " ")).append(nameScore.get(name)).append("\n");
//        }
//        event.getChannel().sendMessage(messageBuilder.append("```").build()).queue();
//    }
//