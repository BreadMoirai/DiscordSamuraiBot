package samurai.action;

import org.reflections.Reflections;
import samurai.Bot;
import samurai.annotations.Key;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author TonTL
 * @version 4.x - 2/21/2017
 */
public class ActionFactory {
    private static final HashMap<String, Class<? extends Action>> actionMap;

    static {
        actionMap = new HashMap<>();
        Reflections reflections = new Reflections("samurai.action");
        Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
        for (Class<? extends Action> action : classes) {
            Key actionKey = action.getAnnotation(Key.class);
            if (actionKey == null || Objects.equals(actionKey.value(), "")) {
                System.err.printf("No key found for %s%n", action.getName());
                continue;
            }
            actionMap.put(actionKey.value(), action);
            String[] name = action.getName().substring(15).split("\\.");
            System.out.printf("%-10s mapped to %-7s.%s%n", String.format("\"%s\"", actionKey.value()), name[0], name[1]);
        }
    }

    private ActionFactory() {
    }


    public static Action newAction(String key) {
        if (!actionMap.containsKey(key)) return null;
        try {
            return actionMap.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Bot.logError(e);
            return null;
        }
    }
}
