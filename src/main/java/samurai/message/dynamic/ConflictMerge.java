package samurai.message.dynamic;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.Bot;
import samurai.data.SamuraiUser;
import samurai.message.DynamicMessage;
import samurai.message.modifier.ReactionEvent;
import samurai.osu.Score;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/23/2017
 */
public class ConflictMerge extends DynamicMessage {

    private final static List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("✅", "\uD83C\uDE51", "\uD83D\uDEAE", "❌"));
    private final static List<String> CONFIRM = Collections.unmodifiableList(Arrays.asList("✅", "❌"));

    private final HashMap<String, LinkedList<Score>> annex;
    private final SamuraiUser uploader;
    private final HashMap<String, LinkedList<Score>> base;
    private final ArrayList<Conflict> conflicts;
    private ListIterator<Conflict> itr;
    private int duplicateScores, newScores, totalScores, userScoresMerged;
    private int totalConflicts, conflictPos;
    private Conflict current;
    private boolean canceled;

    public ConflictMerge(HashMap<String, LinkedList<Score>> annex, HashMap<String, LinkedList<Score>> base, SamuraiUser uploader) {
        super();
        this.annex = annex;
        this.base = base;
        this.uploader = uploader;
        duplicateScores = 0;
        newScores = 0;
        totalScores = 0;
        userScoresMerged = 0;
        canceled = false;
        conflicts = new ArrayList<>();
    }

    private void findConflictsAndRemoveDupes() {
        String name = uploader.getOsuName();
        Iterator it = annex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> pair = (Map.Entry<?, ?>) it.next();
            LinkedList<?> list = (LinkedList<?>) pair.getValue();
            Iterator it2 = list.listIterator();
            while (it2.hasNext()) {
                Score s = (Score) it2.next();
                if (isDupe(s)) {
                    it2.remove();
                    duplicateScores++;
                } else if (!s.getPlayer().equals(name)) {
                    addConflict(s);
                    it2.remove();
                } else {
                    newScores++;
                }
                totalScores++;
            }
            if (list.isEmpty())
                it.remove();
        }
        totalConflicts = conflicts.size();
        conflictPos = 1;
    }

    private boolean isDupe(Score s) {
        if (!base.containsKey(s.getBeatmapHash())) return false;
        for (Score o : base.get(s.getBeatmapHash())) {
            if (s.equals(o)) return true;
        }
        return false;
    }

    private void addConflict(Score s) {
        for (Conflict c : conflicts) {
            if (c.name.equals(s.getPlayer())) {
                c.addScore(s);
                return;
            }
        }
        Conflict c = new Conflict(s.getPlayer());
        c.addScore(s);
        conflicts.add(c);
    }


    @Override
    public Message getMessage() {
        final int stage = getStage();
        if (stage == 0) {
            return new MessageBuilder().append("Analyzing Data...").build();
        } else if (stage == 1) {
            return new MessageBuilder()
                    .append(conflicts.size() == 0 ? "No Conflicts Found..." : conflicts.size() + " irregularities found. Attempting to Resolve...")
                    .build();
        } else if (stage == 2 && conflicts.size() == 0) {
            return new MessageBuilder()
                    .append(String.format("Found `%d` scores for User: `%s`%nDuplicate scores found: `%d`%nNew scores found: `%d`%nMerge?  **Yes:** ✅, **Cancel: **❌", totalScores, uploader.getOsuName(), duplicateScores, newScores))
                    .build();
        } else if (stage < getLastStage() - 1) {
            return new MessageBuilder()
                    .append(String.format("**CONFLICT %d/%d**%n**%d** scores found for `%s`%n%n✅ Merge and rename as `%s`.%n\uD83C\uDE51 Accept and merge as-is.%n\uD83D\uDEAE Do not merge.%n❌ Cancel operation. No data will be modified", conflictPos++, totalConflicts, current.scoreCount, current.name, uploader.getOsuName())).build();
        } else if (stage == getLastStage() - 1) {
            MessageBuilder mb = new MessageBuilder();
            mb.append("Found `").append(newScores).append("` scores for User: `")
                    .append(uploader.getOsuName()).append("`");
            for (Conflict c : conflicts)
                if (!c.renamed)
                    mb.append("\nFound `").append(c.scoreCount).append("` scores for player `")
                            .append(c.scores.getFirst().getPlayer()).append("`");
            mb.append("\nMerge?  **Yes:** ✅, **No: **❌");
            return mb.build();
        } else if (stage == getLastStage()) {
            if (canceled) {
                return new MessageBuilder().append("Operation Cancelled.").build();
            } else if (!conflicts.isEmpty()) {
                MessageBuilder mb = new MessageBuilder();
                for (Conflict c : conflicts) {
                    if (!c.approved) Bot.log("Non approved conflict found.");
                    if (!c.renamed) {
                        mb.append("\n`").append(c.scoreCount).append("` scores added to `").append(c.name).append('`');
                    }
                }
                mb.append("\n`").append(userScoresMerged).append("` scores added to `").append(uploader.getOsuName()).append("`");
                return mb.build();
            } else {
                return new MessageBuilder()
                        .append("Success. `")
                        .append(String.valueOf(userScoresMerged))
                        .append("` scores merged.").build();
            }
        } else {
            Bot.log(String.format("Score Merge Error by <@%d>", uploader.getDiscordId()));
            return new MessageBuilder().append("An Unknown Error has occurred!").build();
        }
    }


    @Override
    protected boolean valid(ReactionEvent action) {
        if (getStage() < 2 || getStage() == getLastStage()) return false;
        else if (conflicts.isEmpty()) {
            return CONFIRM.contains(action.getName());
        } else
            return REACTIONS.contains(action.getName());
    }

    @Override
    protected void execute(ReactionEvent action) {
        if (getStage() == getLastStage() - 1) {
            switch (action.getName()) {
                case "✅":
                    nextStage();
                    merge();
                    return;
                case "❌":
                    canceled = true;
                    setStage(getLastStage());
            }
        } else if (getStage() < getLastStage() - 1)
            switch (action.getName()) {
                case "✅":
                    current.approve();
                    if (itr.hasNext())
                        current = itr.next();
                    else current = null;
                    nextStage();
                    return;
                case "\uD83C\uDE51":
                    current.ignore();
                    if (itr.hasNext())
                        current = itr.next();
                    else current = null;
                    nextStage();
                    return;
                case "\uD83D\uDEAE":
                    itr.remove();
                    if (itr.hasNext())
                        current = itr.next();
                    else current = null;
                    nextStage();
                    return;
                case "❌":
                    canceled = true;
                    setStage(getLastStage());
            }
        else Bot.log(String.format("Illegal Access in ConflictMerge by <@%d>", uploader.getDiscordId()));
    }


    @Override
    public Consumer<Message> createConsumer() {
        if (getStage() == 0) {
            return message -> {
                setMessageId(Long.parseLong(message.getId()));
                findConflictsAndRemoveDupes();
                nextStage();
                message.editMessage(getMessage()).queue(createConsumer());
                if (!conflicts.isEmpty()) {
                    itr = conflicts.listIterator();
                    current = itr.next();
                }
            };
        } else if (getStage() == 1) {
            if (conflicts.isEmpty())
                return newMenuConsumer(CONFIRM);
            else return newMenuConsumer(REACTIONS);
        } else if (getStage() == 2) {
            if (conflicts.isEmpty()) {
                return message -> message.clearReactions().queue();
            }
            return newEditConsumer();
        } else if (getStage() == getLastStage()) {
            return message -> message.clearReactions().queue();
        } else if (getStage() == getLastStage() - 1) {
            return newEditConsumer().andThen(message -> message.getReactions().forEach(messageReaction -> {
                final String name = messageReaction.getEmote().getName();
                if (name.equals("\uD83C\uDE51") || name.equals("\uD83D\uDEAE")) {
                    messageReaction.getUsers().queue(users -> users.forEach(user -> messageReaction.removeReaction(user).queue()));
                }
            }));
        } else return newEditConsumer();
    }

    @Override
    protected int getLastStage() {
        return conflicts.isEmpty() ? 3 : totalConflicts + 3;
    }

    private void merge() {
        for (Map.Entry<String, LinkedList<Score>> entry : annex.entrySet()) {
            if (base.containsKey(entry.getKey()))
                base.get(entry.getKey()).addAll(entry.getValue());
            else
                base.put(entry.getKey(), entry.getValue());
            userScoresMerged += entry.getValue().size();
        }
        for (Conflict c : conflicts)
            if (c.approved) {
                for (Score s : c.scores)
                    if (base.containsKey(s.getBeatmapHash()))
                        base.get(s.getBeatmapHash()).add(s);
                    else {
                        LinkedList<Score> list = new LinkedList<>();
                        list.add(s);
                        base.put(s.getBeatmapHash(), list);
                    }
                if (c.renamed) {
                    userScoresMerged += c.scoreCount;
                }

            }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConflictMerge{");
        sb.append("\nuploader=").append(uploader);
        sb.append("\nconflicts=").append(conflicts);
        sb.append("\nStage=").append(getStage());
        sb.append("\nLastStage=").append(getLastStage());
        sb.append("\ncanceled=").append(canceled);
        sb.append("\n}");
        return sb.toString();
    }

    private class Conflict {
        String name;
        LinkedList<Score> scores;
        int scoreCount;
        boolean approved, renamed;

        Conflict(String name) {
            this.name = name;
            scoreCount = 0;
            scores = new LinkedList<>();
            approved = false;
            renamed = false;
        }

        void addScore(Score s) {
            scores.add(s);
            scoreCount++;
        }

        void approve() {
            approved = true;
            for (Score s : scores) {
                s.setPlayer(uploader.getOsuName());
                newScores++;
            }
            renamed = true;
        }

        void ignore() {
            approved = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Conflict conflict = (Conflict) o;

            return name.equals(conflict.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Conflict{");
            sb.append("\nname='").append(name).append('\'');
            sb.append("\nscores=").append(scores);
            sb.append("\napproved=").append(approved);
            sb.append("\nrenamed=").append(renamed);
            sb.append("\n}");
            return sb.toString();
        }
    }
}