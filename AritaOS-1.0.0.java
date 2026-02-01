import java.io.*;
import java.util.*;

public class AritaOS {

    // ====== CONSTANTS ======
    static final String SYSTEM_DIR = "system";
    static final String USERS_DIR = "users";
    static final String APPS_DIR = "apps";
    static final String LOGS_DIR = "logs";
    static final String STATE_FILE = "system/state.sys";
    static final String USERS_DB = "system/users.db";

    static Scanner scanner = new Scanner(System.in);
    static String currentUser;

    // ====== MAIN ======
    public static void main(String[] args) {
        boot();
        shell();
    }

    // ====== BOOT SEQUENCE ======
    static void boot() {
        System.out.println("Booting Arita OS...");

        if (!checkFileSystem()) {
            firstBoot();
        }

        if (isCritical()) {
            showCritical();
        }

        loadUsers();
        System.out.println("System ready.\n");
    }

    // ====== FILE SYSTEM CHECK ======
    static boolean checkFileSystem() {
        return new File(SYSTEM_DIR).exists();
    }

    // ====== FIRST BOOT ======
    static void firstBoot() {
        System.out.println("First boot detected.");
        System.out.println("Initializing filesystem...");

        try {
            new File(SYSTEM_DIR).mkdir();
            new File(USERS_DIR).mkdir();
            new File(APPS_DIR).mkdir();
            new File(LOGS_DIR).mkdir();

            createState("OK", "FIRST_BOOT");
            createFirstUser();

            // ---- Додаємо компілятор автоматично ----
            createCompilerApp();

        } catch (Exception e) {
            critical("FILESYSTEM_INIT_FAILED");
        }
    }

    // ====== CREATE FIRST USER ======
    static void createFirstUser() {
        System.out.print("Create user name: ");
        String username = scanner.nextLine();

        File userDir = new File(USERS_DIR + "/" + username);
        File homeDir = new File(userDir, "home");

        userDir.mkdir();
        homeDir.mkdir();

        try (FileWriter fw = new FileWriter(USERS_DB)) {
            fw.write(username + ":1000\n");
        } catch (IOException e) {
            critical("USER_DB_WRITE_FAILED");
        }

        currentUser = username;
        System.out.println("User created: " + username);
    }

    // ====== CREATE COMPILER APP ======
    static void createCompilerApp() {
        try {
            File compilerDir = new File(APPS_DIR + "/compiler");
            compilerDir.mkdir();

            File compilerFile = new File(compilerDir, "main.txt");
            if (!compilerFile.exists()) {
                try (PrintWriter pw = new PrintWriter(compilerFile)) {
                    pw.println("print Welcome to Arita Script Compiler!");
                    pw.println("input sourcePath");
                    pw.println("input outputPath");
                    pw.println("print Compiling $sourcePath to $outputPath...");
                    pw.println("sleep 1000");
                    pw.println("print Compilation finished!");
                }
            }

            System.out.println("Compiler app installed at /apps/compiler");
        } catch (IOException e) {
            critical("COMPILER_INSTALL_FAILED");
        }
    }

    // ====== SYSTEM STATE ======
    static void createState(String status, String reason) {
        try (FileWriter fw = new FileWriter(STATE_FILE)) {
            fw.write("status=" + status + "\n");
            fw.write("reason=" + reason + "\n");
        } catch (IOException e) {
            critical("STATE_WRITE_FAILED");
        }
    }

    static boolean isCritical() {
        try (Scanner fs = new Scanner(new File(STATE_FILE))) {
            while (fs.hasNextLine()) {
                if (fs.nextLine().equals("status=CRITICAL")) return true;
            }
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    static void showCritical() {
        System.out.println("\n=== CRITICAL ERROR ===");
        try (Scanner fs = new Scanner(new File(STATE_FILE))) {
            while (fs.hasNextLine()) {
                System.out.println(fs.nextLine());
            }
        } catch (IOException ignored) {}
        System.out.println("System halted.");
        System.exit(1);
    }

    static void critical(String reason) {
        try (FileWriter fw = new FileWriter(STATE_FILE)) {
            fw.write("status=CRITICAL\n");
            fw.write("reason=" + reason + "\n");
        } catch (IOException ignored) {}

        showCritical();
    }

    // ====== LOAD USERS ======
    static void loadUsers() {
        try (Scanner fs = new Scanner(new File(USERS_DB))) {
            if (fs.hasNextLine()) {
                currentUser = fs.nextLine().split(":")[0];
            }
        } catch (IOException e) {
            critical("USERS_DB_MISSING");
        }
    }

    // ====== SHELL ======
    static void shell() {
        System.out.println("Welcome " + currentUser);
        System.out.println("Type 'help' for commands.");

        while (true) {
            System.out.print(currentUser + "@arita> ");
            String input = scanner.nextLine();

            if (input.equals("shutdown")) shutdown();
            else if (input.equals("help")) help();
            else if (input.startsWith("run ")) runApp(input.substring(4));
            else if (input.equals("ls")) listApps();
            else System.out.println("Unknown command");
        }
    }

    // ====== COMMANDS ======
    static void help() {
        System.out.println("help        - show commands");
        System.out.println("ls          - list apps");
        System.out.println("run <app>   - run app");
        System.out.println("shutdown    - power off");
    }

    static void shutdown() {
        createState("OK", "NORMAL_SHUTDOWN");
        System.out.println("System powered off.");
        System.exit(0);
    }

    // ====== APP SYSTEM ======
    static void listApps() {
        File dir = new File(APPS_DIR);
        String[] apps = dir.list();
        if (apps == null || apps.length == 0) {
            System.out.println("No apps installed.");
            return;
        }
        for (String app : apps) {
            System.out.println("- " + app);
        }
    }
static void runApp(String name) {
    File appDir = new File(APPS_DIR + "/" + name);
    File main = new File(appDir, "main.txt");

    if (!main.exists()) {
        System.out.println("App not found.");
        return;
    }

    Map<String, String> vars = new HashMap<>();
    Stack<Integer> loops = new Stack<>();
    List<String> loopLines = new ArrayList<>();
    boolean inLoop = false;
    int loopCount = 0;

    try (Scanner fs = new Scanner(main)) {
        while (fs.hasNextLine()) {
            String line = fs.nextLine().trim();
            if (line.isEmpty()) continue;

            // ====== LOOP ======
            if (line.startsWith("loop ")) {
                loopCount = Integer.parseInt(line.substring(5).replace(":", "").trim());
                loopLines.clear();
                inLoop = true;
                continue;
            }
            if (line.equals("end") && inLoop) {
                for (int i = 1; i <= loopCount; i++) {
                    for (String l : loopLines) {
                        executeLine(l, vars, i);
                    }
                }
                inLoop = false;
                continue;
            }
            if (inLoop) {
                loopLines.add(line);
                continue;
            }

            // ====== REGULAR LINE ======
            executeLine(line, vars, 0);
        }
    } catch (Exception e) {
        System.out.println("App crashed: " + e.getMessage());
    }
}

static void executeLine(String line, Map<String, String> vars, int loopIndex) throws Exception {
    line = line.replace("$loop", String.valueOf(loopIndex));
    for (String key : vars.keySet()) {
        line = line.replace("$" + key, vars.get(key));
    }

    if (line.startsWith("print ")) {
        System.out.println(line.substring(6));
    } else if (line.startsWith("sleep ")) {
        Thread.sleep(Integer.parseInt(line.substring(6)));
    } else if (line.startsWith("input ")) {
        String var = line.substring(6).trim();
        System.out.print(var + ": ");
        vars.put(var, scanner.nextLine());
    } else if (line.startsWith("let ")) {
        String[] parts = line.substring(4).split("=");
        if (parts.length == 2) vars.put(parts[0].trim(), parts[1].trim());
    } else if (line.startsWith("if ")) {
        String condition = line.substring(3).replace(":", "").trim();
        if (!evalCondition(condition, vars)) {
            skipIfBlock();
        }
    }
}

static boolean evalCondition(String cond, Map<String, String> vars) {
    // Прості умови: змінна > число
    try {
        if (cond.contains(">")) {
            String[] p = cond.split(">");
            String val = vars.getOrDefault(p[0].trim(), "0");
            return Integer.parseInt(val) > Integer.parseInt(p[1].trim());
        } else if (cond.contains("<")) {
            String[] p = cond.split("<");
            String val = vars.getOrDefault(p[0].trim(), "0");
            return Integer.parseInt(val) < Integer.parseInt(p[1].trim());
        } else if (cond.contains("==")) {
            String[] p = cond.split("==");
            String val = vars.getOrDefault(p[0].trim(), "");
            return val.equals(p[1].trim());
        }
    } catch (Exception e) {
        return false;
    }
    return false;
}

static void skipIfBlock() {
    // Для простоти поки пропускаємо все до "end"
        }
   
    }