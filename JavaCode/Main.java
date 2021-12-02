import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class StationObjective {
    public int stationId;
    public int objectiveScore;
    public List<Integer> objective;
    public boolean mine;

    public StationObjective(int stationId, int objectiveScore, List<Integer> objective, boolean mine) {
        this.stationId = stationId;
        this.objectiveScore = objectiveScore;
        this.objective = objective;
        this.mine = mine;
    }
}

class Station {
    public int id;
    public boolean available;
    public boolean mine;
    public List<Integer> tech;

    public Station(int id, boolean mine, boolean available, List<Integer> tech) {
        this.id = id;
        this.mine = mine;
        this.available = available;
        this.tech = tech;
    }
}

class Planet {

    public int id;
    public List<Integer> tasks;
    public int myContribution;
    public int oppContribution;
    public int colonizationScore;
    public List<String> bonuses;

    public Planet(int id, List<Integer> tasks, int myContribution, int oppContribution, int colonizationScore, List<String> bonuses) {
        this.id = id;
        this.tasks = tasks;
        this.myContribution = myContribution;
        this.oppContribution = oppContribution;
        this.colonizationScore = colonizationScore;
        this.bonuses = bonuses;
    }
}

class Game {

    public int sectorIndex;
    public Map<Integer, StationObjective> stationObjectives;
    public List<Station> myStations;
    public List<Station> oppStations;
    public List<Planet> planets;
    public List<String> myBonuses;
    public List<String> oppBonuses;
    public int myColonizationScore;
    public int oppColonizationScore;

    public Game() {
        sectorIndex = 0;
        myStations = new ArrayList<>();
        oppStations = new ArrayList<>();
        planets = new ArrayList<>();
        myBonuses = new ArrayList<>();
        oppBonuses = new ArrayList<>();
        stationObjectives = new HashMap<>();
        myColonizationScore = 0;
        oppColonizationScore = 0;
    }

    public void play() {
        // main actions: COLONIZE | RESUPPLY
        // bonus actions: ENERGY_CORE | ALIEN_ARTIFACT | TECH_RESEARCH | NEW_TECH
        System.out.println("RESUPPLY");
    }
}

class Player {

    public static void main(String args[]) {

        Game mainGame = new Game();

        Scanner in = new Scanner(System.in);
        for (int i = 0; i < 8; i++) {
            int stationId = in.nextInt();
            boolean mine = in.nextInt() == 1;
            int objectiveScore = in.nextInt(); // receive these points if tech level objectives are met
            int obj0 = in.nextInt();
            int obj1 = in.nextInt();
            int obj2 = in.nextInt();
            int obj3 = in.nextInt();
            StationObjective stationObjective = new StationObjective(stationId, objectiveScore, Arrays.asList(obj0, obj1, obj2, obj3), mine);
            mainGame.stationObjectives.put(stationId, stationObjective);
        }

        // game loop
        while (true) {
            int sectorIndex = in.nextInt();
            mainGame.sectorIndex = sectorIndex;
            mainGame.myStations.clear();
            mainGame.oppStations.clear();
            for (int i = 0; i < 8; i++) {
                int stationId = in.nextInt();
                boolean mine = in.nextInt() == 1;
                boolean available = in.nextInt() == 1;
                int tech0 = in.nextInt();
                int tech1 = in.nextInt();
                int tech2 = in.nextInt();
                int tech3 = in.nextInt();

                Station station = new Station(stationId, mine, available, Arrays.asList(tech0, tech1, tech2, tech3));
                if (mine) {
                    mainGame.myStations.add(station);
                } else {
                    mainGame.oppStations.add(station);
                }
            }
            mainGame.planets.clear();
            int planetCount = in.nextInt();
            for (int i = 0; i < planetCount; i++) {
                int planetId = in.nextInt();
                int tasks0 = in.nextInt();
                int tasks1 = in.nextInt();
                int tasks2 = in.nextInt();
                int tasks3 = in.nextInt();
                int myContribution = in.nextInt(); // the amount of tasks you have already completed
                int oppContribution = in.nextInt();
                int colonizationScore = in.nextInt(); // points awarded to the colonizer having completed the most tasks
                String bonus0 = in.next();
                String bonus1 = in.next();
                Planet planet = new Planet(
                        planetId,
                        Arrays.asList(tasks0, tasks1, tasks2, tasks3),
                        myContribution,
                        oppContribution,
                        colonizationScore,
                        Arrays.asList(bonus0, bonus1)
                );
                mainGame.planets.add(planet);
            }
            int bonusCount = in.nextInt(); // bonuses in both you and your opponent's inventories
            // reset bonuses as we are parsing them
            mainGame.myBonuses.clear();
            mainGame.oppBonuses.clear();
            for (int i = 0; i < bonusCount; i++) {
                boolean mine = in.nextInt() == 1;
                String bonus = in.next();
                if (mine) {
                    mainGame.myBonuses.add(bonus);
                } else {
                    mainGame.oppBonuses.add(bonus);
                }
            }
            mainGame.myColonizationScore = in.nextInt(); // points from planet colonization, does not include bonus points
            mainGame.oppColonizationScore = in.nextInt();

            mainGame.play();
        }
    }
}