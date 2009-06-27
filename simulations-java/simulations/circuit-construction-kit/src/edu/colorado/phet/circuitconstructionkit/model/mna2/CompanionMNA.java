//package edu.colorado.phet.circuitconstructionkit.model.mna2;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class CompanionMNA {
//    abstract static class CompanionModel {
//        ArrayList<MNA.Battery> batteries;
//        ArrayList<MNA.Resistor> resistors;
//        ArrayList<MNA.CurrentSource> currentSources;
//
//        protected CompanionModel(ArrayList<MNA.Battery> batteries, ArrayList<MNA.Resistor> resistors, ArrayList<MNA.CurrentSource> currentSources) {
//            this.batteries = batteries;
//            this.resistors = resistors;
//            this.currentSources = currentSources;
//        }
//
//        abstract double getCurrent(MNA.Solution solution);
//
//        abstract double getVoltage(MNA.Solution solution);
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            CompanionModel that = (CompanionModel) o;
//
//            if (!batteries.equals(that.batteries)) return false;
//            if (!currentSources.equals(that.currentSources)) return false;
//            if (!resistors.equals(that.resistors)) return false;
//
//            return true;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = batteries.hashCode();
//            result = 31 * result + resistors.hashCode();
//            result = 31 * result + currentSources.hashCode();
//            return result;
//        }
//
//        @Override
//        public String toString() {
//            return "CompanionModel{" +
//                    "batteries=" + batteries +
//                    ", resistors=" + resistors +
//                    ", currentSources=" + currentSources +
//                    '}';
//        }
//    }
//
//    interface NodeCreator {
//        int newNode();
//    }
//
//    interface HasCompanionModel {
//        CompanionModel getCompanionModel(double dt, NodeCreator newNode);
//    }
//
//    static class Capacitor extends MNA.Element implements HasCompanionModel {
//        double capacitance;
//        double voltage;
//        double current;
//
//        Capacitor(int node0, int node1, double capacitance, double voltage, double current) {
//            super(node0, node1);
//            this.capacitance = capacitance;
//            this.voltage = voltage;
//            this.current = current;
//        }
//
//        public CompanionModel getCompanionModel(final double dt, NodeCreator newNode) {
//            //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
//            //and p.23 pillage
//            //our signs differ from Pillage because:
//            //at T=0 across an uncharged capacitor, the capacitor should create a simulated voltage that prevents more charge
//            //from building up on the capacitor; this means a negative voltage (or a backwards battery)
//            int midNode = newNode.newNode();
//            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
//            batteries.add(new MNA.Battery(node0, midNode, voltage - dt * current / 2 / capacitance));
//            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
//            resistors.add(new MNA.Resistor(midNode, node1, dt / 2 / capacitance));
//            return new CompanionModel(batteries, resistors, new ArrayList<MNA.CurrentSource>()) {
//                double getCurrent(MNA.Solution solution) {
//                    return solution.getCurrent(batteries.get(0));
//                }
//
//                double getVoltage(MNA.Solution solution) {
//                    return voltage - dt / 2 / capacitance * (current + getCurrent(solution));
//                }
//            };
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            if (!super.equals(o)) return false;
//
//            Capacitor capacitor = (Capacitor) o;
//
//            if (Double.compare(capacitor.capacitance, capacitance) != 0) return false;
//            if (Double.compare(capacitor.current, current) != 0) return false;
//            if (Double.compare(capacitor.voltage, voltage) != 0) return false;
//
//            return true;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = super.hashCode();
//            long temp;
//            temp = capacitance != +0.0d ? Double.doubleToLongBits(capacitance) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            temp = voltage != +0.0d ? Double.doubleToLongBits(voltage) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            temp = current != +0.0d ? Double.doubleToLongBits(current) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            return result;
//        }
//
//        @Override
//        public String toString() {
//            return "Capacitor{" +
//                    "capacitance=" + capacitance +
//                    ", voltage=" + voltage +
//                    ", current=" + current +
//                    '}';
//        }
//    }
//
//    class Inductor extends MNA.Element implements HasCompanionModel {
//        double inductance;
//        double voltage;
//        double current;
//
//        Inductor(int node0, int node1, double inductance, double voltage, double current) {
//            super(node0, node1);
//            this.inductance = inductance;
//            this.voltage = voltage;
//            this.current = current;
//        }
//
////    def getCompanionModel(dt: Double, newNode: () => Int) = {
////    //Thevenin, Pillage p.23.  Pillage says this is the model used in Spice
////    val midNode = newNode()
////    new CompanionModel(Battery(node0, midNode, voltage + 2 * inductance * current / dt) :: Nil,
////      new Resistor(midNode, node1, 2 * inductance / dt) :: Nil, Nil) {
////      def getCurrent(solution: Solution) = solution.getCurrent(batteries(0))
////
////      def getVoltage(solution: Solution) = (getCurrent(solution) - current) * 2 * inductance / dt - voltage
////    }
//
//        public CompanionModel getCompanionModel(final double dt, NodeCreator newNode) {
//            //Thevenin, Pillage p.23.  Pillage says this is the model used in Spice
//            int midNode = newNode.newNode();
//            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
//            batteries.add(new MNA.Battery(node0, midNode, voltage + 2 * inductance * current / dt));
//            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
//            resistors.add(new MNA.Resistor(midNode, node1, 2 * inductance / dt));
//            return new CompanionModel(batteries, resistors, new ArrayList<MNA.CurrentSource>()) {
//                double getCurrent(MNA.Solution solution) {
//                    return solution.getCurrent(batteries.get(0));
//                }
//
//                double getVoltage(MNA.Solution solution) {
//                    return (getCurrent(solution) - current) * 2 * inductance / dt - voltage;
//                }
//            };
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            if (!super.equals(o)) return false;
//
//            Inductor inductor = (Inductor) o;
//
//            if (Double.compare(inductor.current, current) != 0) return false;
//            if (Double.compare(inductor.inductance, inductance) != 0) return false;
//            if (Double.compare(inductor.voltage, voltage) != 0) return false;
//
//            return true;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = super.hashCode();
//            long temp;
//            temp = inductance != +0.0d ? Double.doubleToLongBits(inductance) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            temp = voltage != +0.0d ? Double.doubleToLongBits(voltage) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            temp = current != +0.0d ? Double.doubleToLongBits(current) : 0L;
//            result = 31 * result + (int) (temp ^ (temp >>> 32));
//            return result;
//        }
//
//        @Override
//        public String toString() {
//            return "Inductor{" +
//                    "inductance=" + inductance +
//                    ", voltage=" + voltage +
//                    ", current=" + current +
//                    '}';
//        }
//    }
//
//    //This models a battery with a resistance in series
//    class ResistiveBattery extends MNA.Element implements HasCompanionModel {
//        double voltage;
//        double resistance;
//
//        ResistiveBattery(int node0, int node1, double voltage, double resistance) {
//            super(node0, node1);
//            this.voltage = voltage;
//            this.resistance = resistance;
//        }
//
//        public CompanionModel getCompanionModel(double dt, NodeCreator newNode) {
//            int midNode = newNode.newNode();
//            ArrayList<MNA.Battery> batteries = new ArrayList<MNA.Battery>();
//            batteries.add(new MNA.Battery(node0, midNode, voltage));
//            ArrayList<MNA.Resistor> resistors = new ArrayList<MNA.Resistor>();
//            resistors.add(new MNA.Resistor(midNode, node1, resistance));
//            return new CompanionModel(batteries, resistors, new ArrayList<MNA.CurrentSource>()) {
//                double getCurrent(MNA.Solution solution) {
//                    return solution.getCurrent(batteries.get(0));
//                }
//
//                double getVoltage(MNA.Solution solution) {
//                    return solution.getVoltageDifference(node0, node1);
//                }
//            };
//        }
//    }
//
//    class InitialCondition {    //todo: needs hashcode, equals and tostring?
//        double voltage;
//        double current;
//
//        InitialCondition(double voltage, double current) {
//            this.voltage = voltage;
//            this.current = current;
//        }
//    }
//
//    class FullCircuit extends MNA.AbstractCircuit {
//        //(batteries: Seq[ResistiveBattery], resistors: Seq[Resistor], capacitors: Seq[Capacitor], inductors: Seq[Inductor])
//
//        ArrayList<ResistiveBattery> batteries;
//        ArrayList<MNA.Resistor> resistors;
//        ArrayList<Capacitor> capacitors;
//        ArrayList<Inductor> inductors;
//
//        FullCircuit(ArrayList<ResistiveBattery> batteries, ArrayList<MNA.Resistor> resistors, ArrayList<Capacitor> capacitors, ArrayList<Inductor> inductors) {
//            this.batteries = batteries;
//            this.resistors = resistors;
//            this.capacitors = capacitors;
//            this.inductors = inductors;
//        }
//
//        FullCircuit stepInTime(double dt) {
//            MNA.Solution solution = solve(dt);
//            ArrayList<Capacitor> newcapacitors = new ArrayList<Capacitor>();
//            for (Capacitor c : capacitors) {
//                newcapacitors.add(new Capacitor(c.node0, c.node1, c.capacitance, solution.getVoltage(c), solution.getCurrent(c)));
//            }
//            ArrayList<Inductor> newinductors = new ArrayList<Inductor>();
//            for (Inductor i : inductors) {
//                newinductors.add(new Inductor(i.node0, i.node1, i.inductance, solution.getVoltage(i), solution.getCurrent(i)));
//            }
//            return new FullCircuit(batteries, resistors, newcapacitors, newinductors);
//        }
//
//        FullCircuit getInitializedCircuit(){
//            InitialConditionSet initConditions=getInitialConditions();
//            ArrayList<Capacitor> newcapacitors = new ArrayList<Capacitor>();
//            for (Capacitor c : capacitors) {
//                newcapacitors.add(new Capacitor(c.node0, c.node1, c.capacitance, initConditions.capacitorMap.get(c).voltage, initConditions.capacitorMap.get(c).current));
//            }
//            ArrayList<Inductor> newinductors = new ArrayList<Inductor>();
//            for (Inductor i : inductors) {
//                newinductors.add(new Inductor(i.node0, i.node1, i.inductance, initConditions.inductorMap.get(i).voltage, initConditions.inductorMap.get(i).current));
//            }
//            return new FullCircuit(batteries, resistors, newcapacitors,newinductors);
//        }
//  //Create a circuit that has correct initial voltages and currents for capacitors and inductors
//  //This is done by:
//  // treating a capacitor as a R=0.0 resistor and computing the current through it
//  // treating an inductor as a R=INF resistor and computing the voltage drop across it
//  //Todo: finding inital bias currently ignores internal resistance in batteries
//  //Todo: Is this computation even used by CCK?  Should it be?
//  InitialConditionSet getInitialConditions() {
//      ArrayList<MNA.Battery>b=new ArrayList<MNA.Battery>();
//      ArrayList<MNA.Resistor> r=new ArrayList<MNA.Resistor>(resistors);
//    ArrayList<MNA.CurrentSource> cs = new ArrayList<MNA.CurrentSource>();
//
//      for (ResistiveBattery batt : batteries) {
//          b.add(new MNA.Battery(batt.node0,batt.node1,batt.voltage));//todo: account for internal resistance of battery in initial bias computation
//      }
//      HashMap<Capacitor, MNA.Resistor> capToRes=new HashMap<Capacitor, MNA.Resistor>();
//      for (Capacitor c : capacitors) {
//          MNA.Resistor resistor=new MNA.Resistor(c.node0,c.node1,0.0);
//          r.add(resistor);
//          capToRes.put(c,resistor);
//      }
//
//    HashMap<Inductor, MNA.Resistor> indToRes = new HashMap<Inductor, MNA.Resistor>();
//      for (Inductor i : inductors) {
//          MNA.Resistor resistor=new MNA.Resistor(i.node0,i.node1,1E14);
//          r.add(resistor);//todo: could make base model handle Infinity properly, via maths or via circuit architecture remapping
//          indToRes.put(i,resistor);
//      }
//    MNA.Circuit circuit = new MNA.Circuit(b, r);
//    MNA.Solution solution = circuit.solve();
//
//      HashMap<Capacitor,InitialCondition> capacitorMap=new HashMap<Capacitor, InitialCondition>();
//      for (Capacitor c : capacitors) capacitorMap.put(c,new InitialCondition(0,solution.getCurrent(capToRes.get(c))));
//
//      HashMap<Inductor,InitialCondition> inductorMap=new HashMap<Inductor, InitialCondition>();
//      for (Inductor i : inductors) {
//          inductorMap.put(i,new InitialCondition(solution.getVoltage(indToRes.get(i)),0));
//      }
//    return new InitialConditionSet(capacitorMap, inductorMap);
//  }
//  case class InitialConditionSet(capacitorMap: HashMap[Capacitor, InitialCondition], inductorMap: HashMap[Inductor, InitialCondition])
//
//  def getCompanionModel(dt: Double) = {
//    val b = new ArrayBuffer[Battery]//batteries use companion model since they have optionally have internal resistance
//    val r = new ArrayBuffer[Resistor]
//    r ++= resistors
//    val cs = new ArrayBuffer[CurrentSource]
//
//    val usedIndices = new ArrayBuffer[Int]
//
//    val companionMap = new HashMap[HasCompanionModel, CompanionModel]
//    val sourceElements: Seq[HasCompanionModel] = capacitors.toList ::: inductors.toList ::: batteries.toList
//    for (c <- sourceElements) {
//      val cm = c.getCompanionModel(dt, () => getFreshIndex(usedIndices))
//      companionMap += c -> cm
//      for (battery <- cm.batteries) b += battery
//      for (resistor <- cm.resistors) r += resistor
//      for (currentSource <- cm.currentSources) cs += currentSource
//    }
//    new CompanionCircuit(new Circuit(b, r, cs), companionMap)
//  }
//
//  //Find the first node index that is unused in the node set or used indices, and update the used indices
//  def getFreshIndex(usedIndices: ArrayBuffer[Int]) = {
//    var selected = -1
//    var testIndex = 0
//    while (selected == -1) {
//      if (!getNodeSet.contains(testIndex) && !usedIndices.contains(testIndex)) {
//        selected = testIndex
//      }
//      testIndex = testIndex + 1
//    }
//    usedIndices += selected
//    selected
//  }
//
//  def getElements: Seq[Element] = batteries.toList ::: resistors.toList ::: capacitors.toList ::: inductors.toList
//
//  def solve(dt: Double) = {
//    val companionModel = getCompanionModel(dt)
//    val solution = companionModel.circuit.solve
//    new CompanionSolution(this, companionModel, solution)
//  }
//}
//
//case class CompanionCircuit(val circuit: Circuit, val elementMap: HashMap[HasCompanionModel, CompanionModel]) {
//  def getCurrent(c: HasCompanionModel, solution: Solution) = elementMap(c).getCurrent(solution)
//
//  def getVoltage(c: HasCompanionModel, solution: Solution) = elementMap(c).getVoltage(solution)
//}
//
//class CompanionSolution(fullCircuit: FullCircuit, companionModel: CompanionCircuit, solution: Solution) extends ISolution {
//  def getNodeVoltage(node: Int) = solution.getNodeVoltage(node)
//
//  def getVoltage(e: Element) = {
//    e match {
//      case c: HasCompanionModel => companionModel.getVoltage(c, solution)
//      case _ => solution.getVoltage(e)
//    }
//  }
//
//  def getCurrent(e: Element) = {
//    e match {
//      case c: HasCompanionModel => companionModel.getCurrent(c, solution)
//      case _ => solution.getCurrent(e)
//    }
//  }
//}
//        }