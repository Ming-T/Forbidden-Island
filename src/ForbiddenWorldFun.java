// Assignment 9 - Problem 2
// Der Jennifer
// jennder
// Tseng Ming-Chiang
// ming
import java.util.ArrayList;

import tester.*;
import javalib.impworld.*;

import java.awt.Color;

import javalib.worldimages.*;

import java.util.Iterator;
import java.util.Random;

// Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  double height;
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  // reports whether this cell is flooded or not
  boolean isFlooded;

  Cell() {
  }

  Cell(double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
    this.isFlooded = false;
  }

  Cell(double height, int x, int y, Cell left, Cell top, Cell right, Cell bottom) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.isFlooded = false;
  }

  // draw the given fish with the corresponding color
  WorldImage draw(int waterHeight) {
    int intH = (int) this.height;
    int belowWater = waterHeight - intH;
    if (this.isFlooded) {
      return new RectangleImage(10, 10, "solid",
          new Color(0, 0, Math.max(0, 255 - belowWater * 8)));
    }
    else if ((belowWater >= 0) && !(this.isFlooded)) {
      return new RectangleImage(10, 10, "solid",
          new Color(150, Math.max(0, 255 - 80 * belowWater), 0));

    }
    else {
      return new RectangleImage(10, 10, "solid", new Color(Math.min(255, 10 + intH * 7),
          Math.min(255, 170 + intH * 2), Math.min(255, 10 + intH * 7)));
    }
  }

  // check if the adjacent cells of this cell isFlooded
  boolean adjacentToWater() {
    return this.left.isFlooded || this.top.isFlooded || this.right.isFlooded
        || this.bottom.isFlooded;
  }
}

class OceanCell extends Cell {

  OceanCell(double height, int x, int y, Cell left, Cell top, Cell right, Cell bottom) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.isFlooded = true;
  }

  OceanCell(double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
    this.isFlooded = true;
  }

  // draw the given fish with the corresponding color
  WorldImage draw(int waterHeight) {
    return new RectangleImage(10, 10, "solid", new Color(0, 0, 180));
  }
}

class Player {
  int x;
  int y;

  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // draw the plater as the image
  WorldImage draw() {
    return new FromFileImage("pilot-icon.png");
  }

  // check if the random player is on a Cell
  Player check(ArrayList<ArrayList<Cell>> allCells) {
    // A random number generator
    Random rand = new Random();
    if (allCells.get(y).get(x).isFlooded) {
      return new Player(rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE),
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE)).check(allCells);
    }
    else {
      return this;
    }
  }

  // return true of the move is legal
  boolean legalMove(String direction, ForbiddenIslandWorld world) {
    if (direction.equals("up")) {
      return this.y - 1 >= 0 && !world.allCells.get(y).get(x).top.isFlooded;
    }
    else if (direction.equals("down")) {
      return this.y + 1 < ForbiddenIslandWorld.ISLAND_SIZE + 1
          && !world.allCells.get(y).get(x).bottom.isFlooded;
    }
    else if (direction.equals("left")) {
      return this.x - 1 >= 0 && !world.allCells.get(y).get(x).left.isFlooded;
    }
    else {
      return this.x + 1 < ForbiddenIslandWorld.ISLAND_SIZE + 1
          && !world.allCells.get(y).get(x).right.isFlooded;
    }
  }

  // EFFECT: move the player according to the given string
  void move(String direction, ForbiddenIslandWorld world) {
    if (this.legalMove(direction, world)) {
      if (direction.equals("up")) {
        this.y = this.y - 1;
      }
      else if (direction.equals("down")) {
        this.y = this.y + 1;
      }
      else if (direction.equals("left")) {
        this.x = this.x - 1;
      }
      else {
        this.x = this.x + 1;
      }
    }
  }
}

class Target {
  int x;
  int y;

  Target(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // render the targets as circle
  WorldImage draw() {
    return new CircleImage(5, "solid", Color.orange);
  }

  // check if the target is on a ocean Cell
  Target check(ArrayList<ArrayList<Cell>> allCells) {
    // A random number generator
    Random rand = new Random();
    if (allCells.get(y).get(x).isFlooded) {
      return new Target(rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE),
          rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE)).check(allCells);
    }
    else {
      return this;
    }
  }
}

class HelicopterTarget extends Target {

  HelicopterTarget(int x, int y) {
    super(x, y);
  }

  // draw the helicopter image
  WorldImage draw() {
    return new FromFileImage("helicopter.png");
  }
}

interface IList<T> extends Iterable<T> {

  // check if it has a next value
  boolean isCons();

  // change the list to a consList<T>
  ConsList<T> asCons();

}

class MTList<T> implements IList<T> {
  // check if it has a next value
  public boolean isCons() {
    return false;
  }

  // change the list to a consList<T>
  public ConsList<T> asCons() {
    throw new RuntimeException("not a consList");
  }

  // change the list into an IListIterator
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // check if it has a next value
  public boolean isCons() {
    return true;
  }

  // change the list to a consList<T>
  public ConsList<T> asCons() {
    return this;
  }

  // change the list into an IListIterator
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

}

class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  // check if the list as a next item
  public boolean hasNext() {
    return this.items.isCons();
  }

  // get the next item in the list
  // EFFECT: change the new list into the rest of the current list
  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }
}

class ForbiddenIslandWorld extends World {
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight;
  // Defines an int constant
  static final int ISLAND_SIZE = 64;
  // center of the board
  int center = ISLAND_SIZE / 2;
  // represents the heights of all the cell in the game
  ArrayList<ArrayList<Double>> allHeights = new ArrayList<ArrayList<Double>>();
  // represents all the cells in the game
  ArrayList<ArrayList<Cell>> allCells = new ArrayList<ArrayList<Cell>>();
  // sets the currentTick
  int currentTick = 0;
  // to represent the player
  Player player;
  // represents the list of targets on the world
  ArrayList<Target> targets = new ArrayList<Target>();
  // represent the helicopter
  HelicopterTarget heli = new HelicopterTarget(0, 0);
  // A random number generator
  Random rand = new Random();

  // constructor
  ForbiddenIslandWorld(String type) {
    if (type.equals("mountain")) {
      mountainHeights();
      generalCells();
      this.createTargets();
      this.board = this.boardCells();
      this.waterHeight = 0;
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      new HelicopterTarget(0, 0);
      this.createHeli();
    }
    else if (type.equals("random")) {
      randomHeights();
      generalCells();
      this.createTargets();
      this.board = this.boardCells();
      this.waterHeight = 0;
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      new HelicopterTarget(0, 0);
      this.createHeli();
    }

    else {
      this.waterHeight = 5;
      randomTerrain();
      generalCells();
      this.createTargets();
      this.board = this.boardCells();
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      new HelicopterTarget(0, 0);
      this.createHeli();
    }
  }

  // EFFECT: initialize fields of ArrayLists to empty and waterHeights to 0
  void init() {
    this.waterHeight = 0;
    this.allHeights = new ArrayList<ArrayList<Double>>();
    this.allCells = new ArrayList<ArrayList<Cell>>();
    this.targets = new ArrayList<Target>();
  }

  // EFFECT: create an ArrayList of targets
  void createTargets() {
    for (int i = 0; i < 5; i = i + 1) {
      Random rand = new Random();
      targets.add(new Target(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells));
    }
  }

  // EFFECT: create the helicopter at the highest point on the island
  void createHeli() {
    Cell highest = new Cell();
    for (Cell c : this.board) {
      if (c.height > highest.height) {
        highest = c;
      }
    }
    heli = new HelicopterTarget(highest.x, highest.y);
  }

  // EFFECT: change to different maps based on the key event
  public void onKeyEvent(String ke) {
    if (ke.equals("m")) {
      this.init();
      mountainHeights();
      generalCells();
      this.createTargets();
      this.board = boardCells();
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      this.createHeli();
    }
    else if (ke.equals("r")) {
      this.init();
      randomHeights();
      generalCells();
      this.createTargets();
      this.board = boardCells();
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      this.createHeli();
    }
    else if (ke.equals("t")) {
      this.init();
      this.waterHeight = 5;
      randomTerrain();
      generalCells();
      this.createTargets();
      this.board = boardCells();
      player = new Player(rand.nextInt(ISLAND_SIZE), rand.nextInt(ISLAND_SIZE)).check(allCells);
      this.createHeli();
    }
    else if (ke.equals("up")) {
      player.move("up", this);
    }
    else if (ke.equals("down")) {
      player.move("down", this);
    }
    else if (ke.equals("left")) {
      player.move("left", this);
    }
    else if (ke.equals("right")) {
      player.move("right", this);
    }

    // removing the target that the player touches
    ArrayList<Target> tl = new ArrayList<Target>();
    for (Target t : targets) {
      if (!(t.x == player.x && t.y == player.y)) {
        tl.add(t);
      }
      this.targets = tl;
    }
  }

  // rendering the world with targets, Helicopter, player on the island
  public WorldScene makeScene() {
    WorldScene background = new WorldScene((ISLAND_SIZE + 1) * 10, (ISLAND_SIZE + 1) * 10);
    for (Cell c : this.board) {
      background.placeImageXY(c.draw(this.waterHeight), c.x * 10 + 5, c.y * 10 + 5);
    }
    for (Target t : this.targets) {
      background.placeImageXY(t.draw(), t.x * 10 + 5, t.y * 10 + 5);
    }
    background.placeImageXY(player.draw(), player.x * 10 + 5, player.y * 10 + 5);
    background.placeImageXY(heli.draw(), heli.x * 10 + 5, heli.y * 10 + 5);
    return background;
  }

  // EFFECT: onTick method that adds 1 to waterHeigh every 10 tick and also
  // changes
  // isFlooded
  public void onTick() {
    if (currentTick == 10) {
      currentTick = 0;
      this.waterHeight = waterHeight + 1;
      this.floodedCells();
    }
    else {
      currentTick = currentTick + 1;
    }
  }

  // return a list of Cells that forms the coastline
  // EFFECT: changes the cell to be flooded if it is next to a flooded cell and
  // below waterHeight
  IList<Cell> floodedCells() {
    IList<Cell> list = new MTList<Cell>();
    for (Cell c : this.board) {
      if (waterHeight - c.height >= 0 && c.adjacentToWater() && !c.isFlooded) {
        c.isFlooded = true;
        list = new ConsList<Cell>(c, list);
      }
    }
    return list;
  }

  // world Ends function
  public WorldEnd worldEnds() {
    boolean equality = true;
    if (allCells.get(player.y).get(player.x).isFlooded) {
      return new WorldEnd(true, this.lastScene("YOU GOT FLOODED"));
    }
    if ((player.x == heli.x) && (player.y == heli.y) && (targets.size() == 0)) {
      return new WorldEnd(true, this.lastScene("YOU WON"));
    }

    for (Cell c : this.board) {
      equality = equality && c.isFlooded;
    }
    if (equality) {
      return new WorldEnd(true, this.lastScene("YOU LOST"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // last scene of the world
  public WorldScene lastScene(String s) {
    WorldScene last = this.makeScene();
    last.placeImageXY(new TextImage(s, 40, Color.RED), 320, 320);
    return last;
  }

  // EFFECT: heights for the mountain
  public void mountainHeights() {
    allHeights = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      allHeights.add(new ArrayList<Double>());
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        allHeights.get(i).add(32 - ((Math.abs(center - i) * 1.0) + (Math.abs(center - g) * 1.0)));
      }
    }
  }

  // EFFECT: heights for the random island
  public void randomHeights() {
    allHeights = new ArrayList<ArrayList<Double>>();
    mountainHeights();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        if (allHeights.get(i).get(g) > 0) {
          allHeights.get(i).set(g, rand.nextInt(center) + 1.0);
        }
        else {
          allHeights.get(i).get(g);
        }
      }
    }
  }

  // EFFECT: height for the random terrain
  public void randomTerrain() {
    allHeights = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      allHeights.add(new ArrayList<Double>());
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        allHeights.get(i).add(0.0);
      }
    }
    allHeights.get(center).set(center, 32.0);
    allHeights.get(0).set(center, 1.0);
    allHeights.get(ISLAND_SIZE).set(center, 1.0);
    allHeights.get(center).set(0, 1.0);
    allHeights.get(center).set(ISLAND_SIZE, 1.0);

    set(0, 0, ISLAND_SIZE / 2, ISLAND_SIZE / 2);
    set(0, ISLAND_SIZE / 2, ISLAND_SIZE / 2, ISLAND_SIZE);
    set(ISLAND_SIZE / 2, 0, ISLAND_SIZE, ISLAND_SIZE / 2);
    set(ISLAND_SIZE / 2, ISLAND_SIZE / 2, ISLAND_SIZE, ISLAND_SIZE);
  }

  // EFFECT: sets each quadrant
  public void set(int tli, int tlg, int bri, int brg) {
    Double tlh = allHeights.get(tli).get(tlg);
    Double trh = allHeights.get(tli).get(brg);
    Double blh = allHeights.get(bri).get(tlg);
    Double brh = allHeights.get(bri).get(brg);
    int area = (bri - tli) * (brg - tlg);
    Double randomNudge = area / 32 * Math.random() - area / 64;

    if ((tli + 1 != bri) && (tlg + 1 != brg)) {
      if (allHeights.get(tli).get((tlg + brg) / 2) == 0.0) {
        allHeights.get(tli).set((tlg + brg) / 2, randomNudge + (tlh + trh) / 2);
      }
      if (allHeights.get((tli + bri) / 2).get(tlg) == 0.0) {
        allHeights.get((tli + bri) / 2).set(tlg, randomNudge + (tlh + blh) / 2);
      }
      if (allHeights.get(bri).get((tlg + brg) / 2) == 0.0) {
        allHeights.get(bri).set((tlg + brg) / 2, randomNudge + (blh + brh) / 2);
      }
      if (allHeights.get((tli + bri) / 2).get(brg) == 0.0) {
        allHeights.get((tli + bri) / 2).set(brg, randomNudge + (trh + brh) / 2);
      }
      if (allHeights.get((tli + bri) / 2).get((tlg + brg) / 2) == 0.0) {
        allHeights.get((tli + bri) / 2).set((tlg + brg) / 2,
            randomNudge + (tlh + trh + blh + brh) / 4);
      }
      set(tli, tlg, (tli + bri) / 2, (tlg + brg) / 2);
      set(tli, (tlg + brg) / 2, (tli + bri) / 2, brg);
      set((tli + bri) / 2, tlg, bri, (tlg + brg) / 2);
      set((tli + bri) / 2, (tlg + brg) / 2, bri, brg);
    }
  }

  // EFFECT: making cells based on the height
  void generalCells() {
    allCells = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      allCells.add(new ArrayList<Cell>());
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        if (allHeights.get(i).get(g) <= waterHeight) {
          allCells.get(i).add(new OceanCell(allHeights.get(i).get(g), g, i));
        }
        else {
          allCells.get(i).add(new Cell(allHeights.get(i).get(g), g, i));
        }
      }
    }
    fixEdgeCells();
  }

  // EFFECT: fixing all the edge cases to refer back to other cells
  void fixEdgeCells() {
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        Cell c = allCells.get(i).get(g);
        if (i == 0) {
          c.top = c;
        }
        else {
          c.top = allCells.get(i - 1).get(g);
        }
        if (i == ISLAND_SIZE) {
          c.bottom = c;
        }
        else {
          c.bottom = allCells.get(i + 1).get(g);
        }
        if (g == 0) {
          c.left = c;
        }
        else {
          c.left = allCells.get(i).get(g - 1);
        }
        if (g == ISLAND_SIZE) {
          c.right = c;
        }
        else {
          c.right = allCells.get(i).get(g + 1);
        }
      }
    }
  }

  // change the ArrayList to an IList
  IList<Cell> boardCells() {
    IList<Cell> list = new MTList<Cell>();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1) {
      for (int g = 0; g < ISLAND_SIZE + 1; g = g + 1) {
        list = new ConsList<Cell>(allCells.get(i).get(g), list);
      }
    }
    return list;
  }
}

class Examples {
  ForbiddenIslandWorld mw;
  ForbiddenIslandWorld rw;
  ForbiddenIslandWorld tw;
  Cell zero = new Cell(0, 0, 0);
  IList<Cell> empty = new MTList<Cell>();
  IList<Cell> cons1 = new ConsList<Cell>(this.zero, this.empty);
  Player player1 = new Player(25, 25);
  Player player2 = new Player(32, 32);
  Player pUp = new Player(32, 1);
  Player pLeft = new Player(1, 32);
  Player pRight = new Player(64, 32);
  Player pDown = new Player(32, 64);
  Target t1 = new Target(45, 45);
  HelicopterTarget ht1 = new HelicopterTarget(45, 35);

  void initData() {
    this.mw = new ForbiddenIslandWorld("mountain");
    this.rw = new ForbiddenIslandWorld("random");
    this.tw = new ForbiddenIslandWorld("terrain");
  }

  // testing draw for cell, oceanCell, player, target, HelicopterTarget
  void testDraw(Tester t) {
    initData();
    Cell middle = mw.allCells.get(32).get(32);
    Cell side = mw.allCells.get(33).get(32);
    side.isFlooded = true;
    t.checkExpect(middle.draw(0), new RectangleImage(10, 10, "solid", new Color(234, 234, 234)));
    t.checkExpect(middle.draw(33), new RectangleImage(10, 10, "solid", new Color(150, 175, 0)));
    t.checkExpect(side.draw(33), new RectangleImage(10, 10, "solid", new Color(0, 0, 239)));
    t.checkExpect(mw.allCells.get(0).get(0).draw(100),
        new RectangleImage(10, 10, "solid", new Color(0, 0, 180)));
    t.checkExpect(player1.draw(), new FromFileImage("pilot-icon.png"));
    t.checkExpect(ht1.draw(), new FromFileImage("helicopter.png"));
    t.checkExpect(t1.draw(), new CircleImage(5, "solid", Color.orange));
  }

  // testing adjacentToWater
  void testAdjacentToWater(Tester t) {
    initData();
    t.checkExpect(mw.allCells.get(32).get(32).adjacentToWater(), false);
    t.checkExpect(mw.allCells.get(32).get(1).adjacentToWater(), true);
  }

  // testing check in player, target
  void testCheck(Tester t) {
    initData();
    Player randomP = new Player(0, 0).check(mw.allCells);
    Target randomT = new Target(60, 55).check(mw.allCells);
    t.checkExpect(player1.check(mw.allCells), player1);
    t.checkExpect(randomP.x == 0, false);
    t.checkExpect(randomP.y != 0, true);
    t.checkExpect(t1.check(mw.allCells), t1);
    t.checkExpect(randomT.x == 60, false);
    t.checkExpect(randomT.y != 55, true);
  }

  // testing legal move
  void testLegalMove(Tester t) {
    initData();
    t.checkExpect(pUp.legalMove("up", mw), false);
    t.checkExpect(pLeft.legalMove("left", mw), false);
    t.checkExpect(pRight.legalMove("right", mw), false);
    t.checkExpect(pDown.legalMove("down", mw), false);
    t.checkExpect(player1.legalMove("up", mw), true);
    t.checkExpect(new Player(35, 25).legalMove("left", mw), true);
    t.checkExpect(new Player(45, 45).legalMove("right", mw), true);
    t.checkExpect(new Player(15, 35).legalMove("down", mw), true);
  }

  // testing move
  void testMove(Tester t) {
    initData();
    this.player1.move("up", mw);
    this.player1.move("left", mw);
    this.player2.move("right", this.mw);
    this.player2.move("down", this.mw);
    this.pUp.move("up", this.mw);
    this.pLeft.move("left", this.mw);
    this.pDown.move("down", this.mw);
    this.pRight.move("right", mw);
    t.checkExpect(this.player1.y, 24);
    t.checkExpect(this.player1.x, 24);
    t.checkExpect(this.player2.x, 33);
    t.checkExpect(this.player2.y, 33);
    t.checkExpect(this.pUp, this.pUp);
    t.checkExpect(this.pDown, this.pDown);
    t.checkExpect(this.pLeft, this.pLeft);
    t.checkExpect(this.pRight, this.pRight);
  }

  // testing init
  void testInit(Tester t) {
    initData();
    mw.init();
    t.checkExpect(this.mw.allHeights.size(), 0);
    t.checkExpect(this.mw.allCells.size(), 0);
    t.checkExpect(this.mw.targets.size(), 0);
    t.checkExpect(this.mw.waterHeight, 0);
  }

  // testing createTarget and CreateHeli
  void testCreateTandH(Tester t) {
    initData();
    Target t2 = this.mw.targets.get(2);
    t.checkExpect(this.mw.targets.size(), 5);
    t.checkExpect(this.mw.heli, new HelicopterTarget(32, 32));
    t.checkExpect((this.mw.allCells.get(t2.y).get(t2.x) instanceof Cell), true);
    t.checkExpect((this.mw.allCells.get(this.mw.heli.y).get(this.mw.heli.x) instanceof OceanCell),
        false);
  }

  // testing set
  void testSet(Tester t) {
    initData();
    mw.randomTerrain();
    // random terrain calls set and set only set value if the height is 0.0
    t.checkRange(this.mw.allHeights.get(0).get(0), -33.0, 33.0);
    t.checkRange(this.mw.allHeights.get(60).get(60), -33.0, 33.0);
  }
  
  // test onKeyEvent
  void testOnKeyEvent(Tester t) {
    initData();
    rw.onKeyEvent("t");
    mw.onKeyEvent("y");
    t.checkExpect(this.rw, this.tw);
    t.checkExpect(this.mw, this.mw);
    t.checkExpect(this.mw.targets.size(), 5);
  }
  
  // test lastScene
  void testLastScene(Tester t) {
    WorldScene scene = mw.makeScene();
    scene.placeImageXY(new TextImage("yolo", 40 , Color.RED), 320, 320);
    t.checkExpect(mw.lastScene("yolo"), scene);
  }
  
  // test onTick
  void testOnTick(Tester t) {
    initData();
    for (int i = 0; i <= 21; i = i + 1) {
      mw.onTick();
    }
    t.checkExpect(this.mw.currentTick, 0);
    t.checkExpect(this.mw.waterHeight, 2);
  }

  // testing random Terrain
  void testRandomTerrain(Tester t) {
    initData();
    this.mw.randomTerrain();
    t.checkExpect(this.mw.allHeights.size(), 65);
    t.checkExpect(this.mw.allHeights.get(32).get(32), 32.0);
    t.checkRange(this.mw.allHeights.get(25).get(25), 0.0, 33.0);
  }

  // testing boardCells
  void testBoardCells(Tester t) {
    initData();
    t.checkExpect((this.mw.board instanceof IList), true);
  }

  // testing mountainHeight
  void testMountainHeights(Tester t) {
    initData();
    this.mw.mountainHeights();
    t.checkExpect(this.mw.allHeights.size(), 65);
    t.checkExpect(this.mw.allHeights.get(32).get(32), 32.0);
    t.checkExpect(this.mw.allHeights.get(32).get(0), 0.0);
    t.checkExpect(this.mw.allHeights.get(0).get(0), -32.0);
    t.checkExpect(this.mw.allHeights.get(64).get(64), -32.0);
    t.checkExpect(this.mw.allHeights.get(33).get(32), 31.0);
    t.checkExpect(this.mw.allHeights.get(0).get(1), -31.0);
  }

  // testing randomHeights
  void testRandomHeights(Tester t) {
    initData();
    this.mw.randomHeights();
    this.mw.generalCells();
    t.checkExpect(this.mw.allHeights.size(), 65);
    t.checkExpect((this.mw.allCells.get(0).get(0) instanceof OceanCell), true);
    t.checkRange(this.mw.allHeights.get(32).get(32), 0.0, 33.0);
    t.checkRange(this.mw.allHeights.get(25).get(45), 0.0, 33.0);
  }

  // testing generalCells
  void testGeneralCells(Tester t) {
    initData();
    t.checkExpect(this.mw.allCells.size(), 65);
    t.checkExpect(this.mw.allCells.get(0).get(0),
        new OceanCell(-32.0, 0, 0, this.mw.allCells.get(0).get(0).left,
            this.mw.allCells.get(0).get(0).top, this.mw.allCells.get(0).get(0).right,
            this.mw.allCells.get(0).get(0).bottom));
    t.checkExpect(this.mw.allCells.get(32).get(32),
        new Cell(32.0, 32, 32, this.mw.allCells.get(32).get(32).left,
            this.mw.allCells.get(32).get(32).top, this.mw.allCells.get(32).get(32).right,
            this.mw.allCells.get(32).get(32).bottom));
    t.checkExpect((this.mw.allCells.get(0).get(0) instanceof OceanCell), true);
    t.checkExpect((this.mw.allCells.get(25).get(25) instanceof OceanCell), false);
  }

  // testing fixEdgeCell
  void testFixEdgeCell(Tester t) {
    initData();
    t.checkExpect(this.mw.allCells.get(0).get(0).top, this.mw.allCells.get(0).get(0));
    t.checkExpect(this.mw.allCells.get(0).get(64).right, this.mw.allCells.get(0).get(64));
    t.checkExpect(this.mw.allCells.get(32).get(32).top, this.mw.allCells.get(31).get(32));
    t.checkExpect(this.mw.allCells.get(32).get(32).right, this.mw.allCells.get(32).get(33));
    t.checkExpect(this.mw.allCells.get(32).get(32).bottom, this.mw.allCells.get(33).get(32));
    t.checkExpect(this.mw.allCells.get(32).get(32).left, this.mw.allCells.get(32).get(31));
    t.checkExpect(this.mw.allCells.get(64).get(64).bottom, this.mw.allCells.get(64).get(64));
    t.checkExpect(this.mw.allCells.get(10).get(10).bottom, this.mw.allCells.get(11).get(10));
  }

  // testing asCons
  void testAsCons(Tester t) {
    t.checkException(new RuntimeException("not a consList"), this.empty, "asCons");
    t.checkExpect(this.cons1.asCons(), new ConsList<Cell>(this.zero, this.empty));
  }

  // testing isCons
  void testIsCons(Tester t) {
    t.checkExpect(this.empty.isCons(), false);
    t.checkExpect(this.cons1.isCons(), true);
  }

  // uncomment to run the world world function
  public static void main(String[] args) {
    ForbiddenIslandWorld w = new ForbiddenIslandWorld("mountain");
    w.bigBang(650, 650, 0.5);
  }

}
