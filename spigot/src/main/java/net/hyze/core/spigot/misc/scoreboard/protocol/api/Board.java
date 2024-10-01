package net.hyze.core.spigot.misc.scoreboard.protocol.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.misc.scoreboard.Boardable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.hyze.core.spigot.misc.scoreboard.protocol.common.TaskManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Board implements Boardable {

    public static final Set<Board> REGISTERED_BOARDS = Sets.newConcurrentHashSet();
    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private final TaskManager taskManager = new TaskManager(this);
    private final BoardObjective objective;
    private final Map<Integer, BoardLine> lines = Maps.newHashMap();

    public Board(String displayName) {
        objective = new BoardObjective(this, NEXT_ID.getAndIncrement() + "Board", displayName);
    }

    @Override
    public void setTitle(String display) {
        objective.setDisplayName(display);
    }

    @Override
    public void set(int index, String text) {
        if (isWritable()) {
            write(index, text);
        } else {
            modifyLine(index, text);
        }
    }

    @Override
    public void reset(int index) {
        if (lines.get(index) == null) {
            return;
        }

        BoardLine line = lines.remove(index);

        if (!isWritable()) {
            line.remove();
        }
    }

    public BoardLine write(int index, String text) {
        Preconditions.checkArgument(isWritable(), "Board don't writable");
        Preconditions.checkArgument(lines.size() < 16, "Cannot write more than 15 lines");
        return lines.put(index, check(new BoardLine(this, index, text)));
    }

    public void modifyLine(int index, String text) {
        Preconditions.checkArgument(!isWritable(), "Board must be created");

        if (lines.containsKey(index)) {
            lines.get(index).setText(text);
        } else {
            lines.put(index, check(new BoardLine(this, index, text)));
        }
    }

    public void addUpdater(long interval, BoardUpdater task) {
        taskManager.addUpdater(interval, task);
    }

    public boolean isWritable() {
        return !objective.isRegistered();
    }

    public void create() {
        Preconditions.checkArgument(isWritable(), "Board already created");
        objective.register();

        lines.values().forEach(BoardLine::update);

        if (taskManager.size() > 0) {
            taskManager.startUpdate();
        }

        REGISTERED_BOARDS.add(this);
    }

    public void remove() {
        Preconditions.checkArgument(!isWritable(), "Board not created yet");
        
        objective.unregister();
        taskManager.cancel();
        
        REGISTERED_BOARDS.remove(this);
    }

    public BoardObjective getObjective() {
        return objective;
    }

    private BoardLine check(BoardLine line) {
        String text = line.getText();
        while (getLines().contains(text)) {
            text += "§r";
        }
        line.setText(text);
        return line;
    }

    public List<String> getLines() {
        return lines.values().stream().map(BoardLine::getText).collect(Collectors.toList());
    }

    public Collection<BoardLine> lineWrappers() {
        return lines.values();
    }

    public void send() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    public void send(Player... player) {
        objective.display(player);
    }

    public static BoardBuilder getBuilder(Board board) {
        return new BoardBuilder(board);
    }

    public static BoardBuilder getBuilder(String title) {
        return getBuilder(new Board(title));
    }

    public static BoardBuilder getPersonalBuilder(String title, Player player) {
        return getBuilder(new PersonalBoard(title, player));
    }
}
