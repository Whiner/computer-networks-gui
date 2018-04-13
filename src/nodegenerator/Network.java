package nodegenerator;

import nodegenerator.generatorException.NodeExistException;
import nodegenerator.generatorException.NodeRelationsException;
import nodegenerator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;


public class Network {
    private NetworkType type;
    private List<Node> nodes;
    private int maxNodeCount;


    public Node getLastNode(){
        return nodes.get(nodes.size() - 1);
    }
    public void setMaxNodeRelations(int maxNodeRelations) throws Exception {
        for(Node t: nodes){
            t.setMaxRelationsCount(maxNodeRelations);
        }
    }
    public void setMaxNodeCount(int maxNodeCount) {
        this.maxNodeCount = maxNodeCount;
    }

    public Node getByCoord(int x, int y){
        for (Node t: nodes){
            if(t.getCellNumber_X() == x)
                if(t.getCellNumber_Y() == y)
                    return t;
        }
        return null;
    }

    public Node getNodeByID(int ID){
        for(Node t: nodes)
            if(t.getID() == ID)
                return t;
         return null;
    }
    public boolean isAllHaveMaxRelations(){
        if(nodes.isEmpty()){
            return false;
        }
        for (Node t: nodes){
            if(t.getRelationsCount() < t.getMaxRelationsCount())
                return false;
        }
        return true;
    }




    public boolean checkIntersection(Node from, Node to){ //переделать
        int t_x = Math.abs(from.getCellNumber_X() - to.getCellNumber_X());
        int t_y = Math.abs(from.getCellNumber_Y() - to.getCellNumber_Y());
        if(t_x == 0){
            int start = Math.min(from.getCellNumber_Y(), to.getCellNumber_Y());
            for (int i = 1; i < t_y; i++){
                if(getByCoord(from.getCellNumber_X(), start + i) != null)
                    return true;
            }
            return false;
        }
        else
            if(t_y == 0){
            int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
            for (int i = 1; i < t_x; i++){
                if(getByCoord(start + i, from.getCellNumber_Y()) != null)
                    return true;
            }
            return false;
        }
        else
            if(t_x == t_y) {
                int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
                for (int i = 1; i < t_x; i++){
                    if(getByCoord(start + i, start + i) != null)
                        return true;
                }
                return false;
        }
        return false;
    }


    public void addNode(int x, int y, List<Integer> connectWith, int maxRelationsQuantity) throws Exception {
        if(nodes.size() + 1 > maxNodeCount) {
            throw new NodeExistException("Максимальное количество узлов в этой сети уже достигнуто");
        }
        if(maxRelationsQuantity <= 0){
            throw new NodeRelationsException("Значение максимального количества связей не корректно");
        }
        if(getByCoord(x, y) != null){
            throw new NodeExistException("В этой ячейке уже существует узел");
        }
        int ID;
        if(nodes.isEmpty()) {
            ID = 0;
        }
        else {
            ID = nodes.get(nodes.size() - 1).getID() + 1;
        }

        nodes.add(new Node(type, x, y, ID));

        Node lastAdded = nodes.get(ID);
        lastAdded.setMaxRelationsCount(maxRelationsQuantity);

        int con = connectWith.size();

        for (int i = 0; i < connectWith.size(); i++) {
            Node ConnectingNode = getNodeByID(connectWith.get(i));
            if (ConnectingNode != null) {
                try {
                    if (checkIntersection(lastAdded, ConnectingNode)) {
                        if (lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1) {
                            throw new NodeRelationsException("");
                        }
                        con--;
                        continue;
                    }
                    lastAdded.connectNode(ConnectingNode, false);
                }
                catch(OneselfConnection | NodeRelationsException e) {
                    con--;
                   if(lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1){
                       nodes.remove(lastAdded);
                       throw new NodeExistException("Узел был удален т.к. не соединился ни с одним уже существующим");
                   }
                }
            }
        }
        System.out.println("Conn " + con + "/" + connectWith.size());
    }

    public int size(){
        return nodes.size();
    }

    public Network() {
        nodes = new ArrayList<>();
        maxNodeCount = 8965545;
    }

    public Network(NetworkType type, int maxNodeCount) {
        this.maxNodeCount = maxNodeCount;
        nodes = new ArrayList<>();
        this.type = type;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public List<nodegenerator.Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}