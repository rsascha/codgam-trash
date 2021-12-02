class Game {
  constructor() {
    this.sectorIndex = 0;
    this.myStations = [];
    this.oppStations = [];
    this.planets = [];
    this.myBonuses = [];
    this.oppBonuses = [];
    this.myColonizationScore = 0;
    this.oppColonizationScore = 0;
    this.stationObjectives = {}
  }

  play() {
    // Write 1 action using console.log()
    // main actions: COLONIZE | RESUPPLY
    // bonus actions: ENERGY_CORE | ALIEN_ARTIFACT | TECH_RESEARCH | NEW_TECH
    console.log('RESUPPLY');
  }
}

class Station {
  constructor(id, mine, available, tech) {
    this.id = id;
    this.mine = mine;
    this.available = available;
    this.tech = tech;
  }
}

class StationObjective {
  constructor(stationId, objectiveScore, objective, mine) {
    this.stationId = stationId;
    this.objectiveScore = objectiveScore;
    this.objective = objective;
    this.mine = mine;
  }
}

class Planet {
  constructor(planetId, tasks, myContribution, oppContribution, colonizationScore, bonuses) {
    this.id = planetId;
    this.tasks = tasks;
    this.myContribution = myContribution;
    this.oppContribution = oppContribution;
    this.colonizationScore = colonizationScore;
    this.bonuses = bonuses;
  }
}

let mainGame = new Game();

for (let i = 0; i < 8; i++) {
  let inputs = readline().split(' ');
  const stationId = parseInt(inputs[0]);
  const mine = inputs[1] === '1';
  const objectiveScore = parseInt(inputs[2]); // receive these points if tech level objectives are met
  const obj0 = parseInt(inputs[3]);
  const obj1 = parseInt(inputs[4]);
  const obj2 = parseInt(inputs[5]);
  const obj3 = parseInt(inputs[6]);

  const stationObjective = new StationObjective(stationId, objectiveScore, [obj0, obj1, obj2, obj3], mine);
  mainGame.stationObjectives[stationId] = stationObjective
}

// game loop
while (true) {
  const sectorIndex = parseInt(readline());
  mainGame.sectorIndex = sectorIndex;
  mainGame.myStations = [];
  mainGame.oppStations = [];
  for (let i = 0; i < 8; i++) {
    let inputs = readline().split(' ');
    const stationId = parseInt(inputs[0]);
    const mine = inputs[1] === '1';
    const available = inputs[2] === '1';
    const tech0 = parseInt(inputs[3]);
    const tech1 = parseInt(inputs[4]);
    const tech2 = parseInt(inputs[5]);
    const tech3 = parseInt(inputs[6]);

    const station = new Station(stationId, mine, available, [tech0, tech1, tech2, tech3])
    if (mine) {
      mainGame.myStations.push(station)
    } else {
      mainGame.oppStations.push(station)
    }
  }
  mainGame.planets = [];
  const planetCount = parseInt(readline());
  for (let i = 0; i < planetCount; i++) {
    let inputs = readline().split(' ');
    const planetId = parseInt(inputs[0]);
    const tasks0 = parseInt(inputs[1]);
    const tasks1 = parseInt(inputs[2]);
    const tasks2 = parseInt(inputs[3]);
    const tasks3 = parseInt(inputs[4]);
    const myContribution = parseInt(inputs[5]); // the amount of tasks you have already completed
    const oppContribution = parseInt(inputs[6]);
    const colonizationScore = parseInt(inputs[7]); // points awarded to the colonizer having completed the most tasks
    const bonus0 = inputs[8];
    const bonus1 = inputs[9];
    const planet = new Planet(planetId, [tasks0, tasks1, tasks2, tasks3], myContribution, oppContribution, colonizationScore, [bonus0, bonus1])
    mainGame.planets.push(planet);
  }
  const bonusCount = parseInt(readline()); // bonuses in both you and your opponent's inventories
  // reset bonuses as we are parsing them
  mainGame.myBonuses = [];
  mainGame.oppBonuses = [];
  for (let i = 0; i < bonusCount; i++) {
    let inputs = readline().split(' ');
    const mine = inputs[0] === '1';
    const bonus = inputs[1];
    if (mine) {
      mainGame.myBonuses.push(bonus);
    } else {
      mainGame.oppBonuses.push(bonus);
    }
  }
  // points from planet colonization, does not include bonus points
  mainGame.myColonizationScore = parseInt(readline());
  mainGame.oppColonizationScore = parseInt(readline());

  // To debug: console.error()

  mainGame.play();
}