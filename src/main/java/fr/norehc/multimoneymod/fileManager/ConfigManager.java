package fr.norehc.multimoneymod.fileManager;

import fr.norehc.multimoneymod.MultiMoneyMod;
import fr.norehc.multimoneymod.models.DropModel;
import fr.norehc.multimoneymod.models.MoneyModel;
import fr.norehc.multimoneymod.models.ValueDropModel;
import info.pixelmon.repack.yaml.snakeyaml.DumperOptions;
import info.pixelmon.repack.yaml.snakeyaml.Yaml;
import info.pixelmon.repack.yaml.snakeyaml.constructor.Constructor;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigManager {

    private static final File CONFIG_FILE = new File("config/multimoney/config.yml");
    private static final Map<Integer, MoneyModel> moneys = new HashMap<>();
    public static Map<String, String> textMap = new HashMap<>();
    public static int SAVE_INTERVAL_TICKS = 30;

    // Charger le fichier YAML
    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveDefaultConfig();
        }

        Yaml yaml = new Yaml(new Constructor(Map.class));
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Map<?, ?> data = yaml.load(reader);
            if (data != null && data.containsKey("moneys")) {
                Map<?, ?> moneysData = (Map<?, ?>) data.get("moneys");

                moneys.clear();
                for (Object name : moneysData.keySet()) {
                    Map<String, Object> money = (Map<String, Object>) moneysData.get(name);
                    Map<String, DropModel> dropModel = new HashMap<>();

                    if(!money.containsKey("params")) continue;

                    String[] eventsName = new String[] {
                            "capture", "defeating"
                    };

                    for(String eventName : eventsName) {
                        if (((Map<String, Object>)money.get("params")).containsKey(eventName)) {
                            Map<String, Object> eventData = (Map<String, Object>) ((Map<String, Object>)money.get("params")).get(eventName);
                            Map<String, Map<Integer, ValueDropModel>> eventValue = new HashMap<>();
                            String[] valueName = new String[]{
                                    "legendary", "shiny", "other"
                            };

                            if (eventData.containsKey("pokemon") && eventData.containsKey("palette")) {

                                for (String s : valueName) {
                                    if (eventData.containsKey(s)) {
                                        Map<Integer, Map<String, Integer>> legendaryValue = (Map<Integer, Map<String, Integer>>) eventData.get(s);
                                        Map<Integer, ValueDropModel> dataValues = new HashMap<>();
                                        legendaryValue.forEach((index, dataValue) -> {
                                            if (dataValue.containsKey("valueMin") && dataValue.containsKey("valueMax") && dataValue.containsKey("probability")) {
                                                dataValues.put(index, new ValueDropModel(dataValue.get("valueMin"), dataValue.get("valueMax"), dataValue.get("probability")));
                                            }
                                        });
                                        eventValue.put(s, dataValues);
                                    }
                                }

                                dropModel.put(eventName, new DropModel((ArrayList) eventData.get("pokemon"), (ArrayList) eventData.get("palette"), eventValue));
                            }

                        }
                    }


                    moneys.put(name instanceof Integer ? (Integer) name : -1, new MoneyModel(
                            money.containsKey("name") ? (String) money.get("name") : "error",
                            money.containsKey("symbol") ? (String) money.get("symbol") : "error",
                            dropModel
                            )
                    );
                }
            }

            if (data.containsKey("text")) {
                Map<?, ?> map = (Map<?, ?>) data.get("text");
                textMap.clear();
                for (Object key : map.keySet()) {
                    textMap.put(key.toString(), map.get(key).toString());
                }
            }
            if (data.containsKey("save_time")) {
                SAVE_INTERVAL_TICKS = (int) (Integer) data.get("save_time");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sauvegarde un config par défaut
    private static void saveDefaultConfig() {
        CONFIG_FILE.getParentFile().mkdirs();

        moneys.put(0, new MoneyModel("NewCoin", "$",
                new HashMap<String, DropModel>() {{
                    put("capture", new DropModel(
                            new ArrayList<String>(){{
                                    add("all");
                            }},
                            new ArrayList<String>(){{
                                add("all");
                            }},
                            new HashMap<String, Map<Integer, ValueDropModel>>() {{
                                put("legendary", new HashMap<Integer, ValueDropModel>() {{
                                    put(0, new ValueDropModel(
                                            10,
                                            20,
                                            100
                                    ));
                                    put(1, new ValueDropModel(
                                            20,
                                            40,
                                            5
                                    ));
                                }});
                                put("shiny", new HashMap<Integer, ValueDropModel>() {{
                                    put(0, new ValueDropModel(
                                            5,
                                            10,
                                            100
                                    ));
                                    put(1, new ValueDropModel(
                                            20,
                                            30,
                                            5
                                    ));
                                }});
                                put("other", new HashMap<Integer, ValueDropModel>() {{
                                    put(0, new ValueDropModel(
                                            1,
                                            2,
                                            100
                                    ));
                                    put(1, new ValueDropModel(
                                            10,
                                            20,
                                            5
                                    ));
                                }});
                            }}
                    ));
                }}
        ));

        Map<Integer, Object> dataLegendary = new HashMap<>();

        dataLegendary.put(0, new HashMap<String, Object>() {{
            put("name", "NewCoin");
            put("symbol", "$");
            put("params", new HashMap<String, Map<String, Object>>() {{
                put("capture", new HashMap<String, Object>() {{
                    put("palette", new String[]{
                            "all"});
                    put("pokemon", new String[]{
                            "all"});
                    put("legendary", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 10);
                            put("valueMax", 20);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 40);
                            put("valueMax", 60);
                            put("probability", 5);
                        }});
                    }});
                    put("shiny", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 5);
                            put("valueMax", 10);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 20);
                            put("valueMax", 30);
                            put("probability", 5);
                        }});
                    }});
                    put("other", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 1);
                            put("valueMax", 2);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 10);
                            put("valueMax", 20);
                            put("probability", 5);
                        }});
                    }});
                }});
                put("defeating", new HashMap<String, Object>() {{
                    put("palette", new String[]{
                            "all"});
                    put("pokemon", new String[]{
                            "all"});
                    put("legendary", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 10);
                            put("valueMax", 20);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 40);
                            put("valueMax", 60);
                            put("probability", 5);
                        }});
                    }});
                    put("shiny", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 5);
                            put("valueMax", 10);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 20);
                            put("valueMax", 30);
                            put("probability", 5);
                        }});
                    }});
                    put("other", new HashMap<Integer, Map<String, Integer>>() {{
                        put(0, new HashMap<String, Integer>() {{
                            put("valueMin", 1);
                            put("valueMax", 2);
                            put("probability", 100);
                        }});
                        put(1, new HashMap<String, Integer>() {{
                            put("valueMin", 10);
                            put("valueMax", 20);
                            put("probability", 5);
                        }});
                    }});
                }});
            }});
        }});

        Map<String, Object> data = new HashMap<>();

        //textMap.put("pagination_prev", "&e<-");


        //data.put("text", textMap);
        data.put("save_time", 30);
        data.put("moneys", dataLegendary);


        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MoneyModel getMoneyData(int money) {
        return moneys.getOrDefault(money, new MoneyModel("error", "error", new HashMap<>()));
    }

    public static String getText(String key) {
        return textMap.getOrDefault(key, key);
    }

    public static String translateColors(String text) {
        for (int i = 0; i < 16; i++) {
            String code = "&" + Integer.toHexString(i);
            text = text.replace(code, net.minecraft.util.text.TextFormatting.getById(i).toString());
        }
        text = text.replace("&r", net.minecraft.util.text.TextFormatting.RESET.toString());
        return text;
    }

    public static Map<Integer, MoneyModel> getAllMoneys() {
        return moneys;
    }

    public static boolean doesMoneyExist(int money) {
        return moneys.containsKey(money);
    }

    public static boolean doesMoneyExistFromName(String name) {
        AtomicBoolean doesExist = new AtomicBoolean(false);

        moneys.forEach((key, data) -> {
            if (data.name.equalsIgnoreCase(name)) {
                doesExist.set(true);
            }
        });
        return doesExist.get();
    }

    public static Integer getIdMoneyFromName(String name) {
        AtomicInteger idMoney = new AtomicInteger(-1);

        moneys.forEach((key, data) -> {
            if (data.name.equalsIgnoreCase(name)) {
                idMoney.set(key);
            }
        });
        return idMoney.get();
    }

    public static String[] getAllMoneysName() {
        String[] name = new String[moneys.size()];
        //List<String> name = new ArrayList<>();
        AtomicInteger place = new AtomicInteger();
        moneys.forEach((a, b) -> {
            name[place.get()] = b.name;
            place.getAndIncrement();
        });

        return name;
    }
}
