package fr.norehc.multimoneymod.fileManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerMoneyFileManager {

    private static final File PLAYERS_DIR = new File("config/multimoney/dataPlayers");
    private static final Gson GSON = new Gson();

    static {
        if (!PLAYERS_DIR.exists()) {
            PLAYERS_DIR.mkdirs();
        }
    }

    public static Map<Integer, Double> loadStats(UUID playerId) {
        File file = new File(PLAYERS_DIR, playerId.toString() + ".json");
        if (!file.exists()) return new HashMap<>();

        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, new TypeToken<Map<Integer, Double>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void saveStats(UUID playerId, Map<Integer, Double> stats) {
        if (stats.isEmpty()) return;
        File file = new File(PLAYERS_DIR, playerId.toString() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(stats, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<UUID, Map<Integer, Double>> loadAllStats() {
        Map<UUID, Map<Integer, Double>> playersData = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(PLAYERS_DIR.toURI()))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        File file = new File(path.toUri());
                        String playerUUID = path.getName(path.getNameCount()-1).toString().split("\\.")[0];
                        try (FileReader reader = new FileReader(file)) {
                            playersData.put(UUID.fromString(playerUUID), GSON.fromJson(reader, new TypeToken<Map<Integer, Double>>() {
                            }.getType()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return playersData;
    }
}
