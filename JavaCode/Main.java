import java.util.*;
import java.util.stream.Collectors;

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

    public Planet(int id, List<Integer> tasks, int myContribution, int oppContribution, int colonizationScore,
            List<String> bonuses) {
        this.id = id;
        this.tasks = tasks;
        this.myContribution = myContribution;
        this.oppContribution = oppContribution;
        this.colonizationScore = colonizationScore;
        this.bonuses = bonuses;
    }

    int majority() {
        return (int) Math.ceil(totalPrice() / 2.0);
    }

    int totalPrice() {
        int totalPrice = 0;
        for (int task : tasks) {
            totalPrice += task;
        }
        return totalPrice + myContribution + oppContribution;
    }

    int remainingPrice() {
        int price = 0;
        for (int task : tasks) {
            price += task;
        }
        return price;
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

    static class StationPlanetPair {
        Station station;
        Planet planet;

        public StationPlanetPair(Station station, Planet planet) {
            this.station = station;
            this.planet = planet;
        }
    }

    Map<StationPlanetPair, Integer> ratedPairs;

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
        ratedPairs = new HashMap<>();
    }

    enum Tech {
        TERRA, ETHIC, ENGINEERING, AGRICULTURE;
    }

    public void play() {
        ratedPairs.clear();
        // main actions: COLONIZE | RESUPPLY
        // bonus actions: ENERGY_CORE | ALIEN_ARTIFACT | TECH_RESEARCH | NEW_TECH
        if (shouldUseEnergyCore()) {
            return;
        }

        if (investInPlanet()) {
            return;
        }
        if (canColonizePlanet()) {
            return;
        }
        System.out.println("RESUPPLY");
    }

    private boolean investInPlanet() {
        System.err.print("Ratings: ");
        for (Station myStation : getAvailableStations()) {
            for (Planet planet : getRelevantPlanets()) {
                StationPlanetPair pair = new StationPlanetPair(myStation, planet);
                int rating = ratePair(myStation, planet);
                System.err.print("[(" + myStation.id + "->" + planet.id + "):" + rating + "] ");
                ratedPairs.put(pair, rating);
            }
        }
        System.err.println();

        Optional<Map.Entry<StationPlanetPair, Integer>> bestRating = ratedPairs.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        if (bestRating.isPresent()) {
            StationPlanetPair bestPair = bestRating.get().getKey();
            int rating = ratedPairs.get(bestPair);
            if (rating > 0) {
                colonize(bestPair);
                System.err
                        .println("Chosen: [(" + bestPair.station.id + "->" + bestPair.planet.id + "):" + rating + "]");
                return true;
            }
        }
        return false;
    }

    /**
     * Give a rating how beneficial this move would be. Considers:
     * * Will be earn the planet when doing the move?
     * * Can we still get the target planet?
     * * How many of our points would we invest?
     * * Have we invested anything into the planet yet?
     * * Avoid investing if it would help the opponent
     * 
     * @param station
     * @param planet
     * @return score of the move. the higher the value, the preferable. If 0, don't
     *         do the move.
     */
    private int ratePair(Station station, Planet planet) {
        List<Integer> tech = station.tech;
        List<Integer> tasks = planet.tasks;

        int investable = 0;
        for (int i = 0; i < 4; i++) {
            investable += Math.min(tech.get(i), tasks.get(i));
        }

        // This move wouldn't do anything, no sense in checking other conditions on this
        // move
        if (investable == 0)
            return 0;

        // Can we sack a planet with our move?
        if ((planet.myContribution + investable) >= planet.majority()) {
            int score = 100; // can sack in one move. massivly prefer the move
            score += planet.colonizationScore; // the higher the planet score, the preferable

            // Check if planet has a points bonus. If yes, add that to the score.
            for (String item : planet.bonuses) {
                if (item.startsWith("POINTS_")) {
                    int bonus = Integer.parseInt(item.substring(item.length() - 1));
                    score += bonus;
                    break;
                }
            }
            return score;
        }
        // Can't sack planet in one move, but can we still get it?
        if (planet.oppContribution < planet.majority()) {
            return investable;
        }
        // Can't get the planet anymore. Have we already invested? If not, doing so will
        // at least give us a bonus
        if (planet.myContribution == 0) {
            // We can't win the planet anymore, so investing 1 of 5 tech makes no difference
            // anymore,
            // so give them all the same value.
            return 1;
        }
        // Can't get the planet anymore. No sense in helping the opponent.
        return 0;
    }

    private boolean canColonizePlanet() {
        for (Station myStation : getAvailableStations()) {
            for (Planet planet : planets) {
                if (canColonizePlanet(myStation, planet)) {
                    colonize(myStation, planet);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canColonizePlanet(Station myStation, Planet planet) {
        List<Integer> tech = myStation.tech;
        List<Integer> tasks = planet.tasks;

        for (int i = 0; i < 4; i++) {
            if (tech.get(i) < tasks.get(i)) {
                return false;
            }
        }
        return true;
    }

    void colonize(StationPlanetPair pair) {
        colonize(pair.station, pair.planet);
    }

    void colonize(Station myStation, Planet planet) {
        int bonusIndex = bonusIndex(myStation, planet);
        System.out.println("COLONIZE " + myStation.id + " " + planet.id + " " + bonusIndex);
    }

    private int bonusIndex(Station myStation, Planet planet) {
        int result = 0;
        int points = 0; // Number of additional points we get via bonus
        // If there is a point bonus, prefer the highest one
        for (int i = 0; i < 2; i++) {
            String bonus = planet.bonuses.get(i);
            if (bonus.startsWith("POINTS_")) {
                int value = Integer.parseInt(bonus.substring(bonus.length() - 1));
                if (value > points) {
                    points = value;
                    result = i;
                }
            }
        }
        if (points > 0)
            return result;

        // TODO: Other boni:
        // * If there's a tech bonus, and we still have need for it, then choose needed
        // bonus, else
        // * Choose alien artifacts, they allow us to boost a planet, so we may colonize
        // it in one turn, else
        // * Choose energy core
        return result;
    }

    List<Station> getAvailableStations() {
        return myStations.stream()
                .filter(station -> station.available)
                .collect(Collectors.toList());
    }

    List<Planet> getRelevantPlanets() {
        return planets.stream()
                .filter(planet -> planet.oppContribution < planet.myContribution + planet.remainingPrice())
                .collect(Collectors.toList());
    }

    boolean shouldUseEnergyCore() {
        boolean energyCoreAvailable = false;
        for (String bonus : myBonuses) {
            if (bonus.equals("ENERGY_CORE")) {
                energyCoreAvailable = true;
                break;
            }
        }
        if (getAvailableStations().size() == 0 && energyCoreAvailable) {
            System.out.println("ENERGY_CORE");
            return true;
        }
        return false;
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
            StationObjective stationObjective = new StationObjective(stationId, objectiveScore,
                    Arrays.asList(obj0, obj1, obj2, obj3), mine);
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
                        Arrays.asList(bonus0, bonus1));
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
            mainGame.myColonizationScore = in.nextInt(); // points from planet colonization, does not include bonus
                                                         // points
            mainGame.oppColonizationScore = in.nextInt();

            mainGame.play();
        }
    }
}