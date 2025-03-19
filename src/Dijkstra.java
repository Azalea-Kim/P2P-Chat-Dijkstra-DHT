import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Dijkstra {

    // generate an array with all the connected users
    public static String[] generateArray(){
        String[] a = new String[Server.clientsInServer.keySet().size()];
        int i = 0;
        for (String peer : Server.clientsInServer.keySet()) {
            a[i] = peer;
            i+=1;
        }
        return a;
    }

    // get the three nearest receivers of this sender

    public static String[] getOSPF(String sender){
        // the array of all the vertexes in the graph
        String[] vertexArray = generateArray();
        int len = vertexArray.length;//7
        // the adjacent matrix
        int[][] matrix = new int[vertexArray.length][vertexArray.length];

        int i = 0;
        int j;

        // create an empty matrix with the required size to represent graph
        while(i<len){
            matrix[i] = new int[len];
            i++;
        }

        int ii;
        for (ii= 0; ii<len;ii++){

            for (j= 0; j<len;j++){
                if(ii!=j) {
                    Random r = new Random();
                    // the weight is given randomly to represent the bandwith
                    // and the value is inversely given.
                    matrix[ii][j] = r.nextInt(10)+1;
                    matrix[j][ii] = matrix[ii][j];

                }}}

        // generate a graph
        Graph graph = new Graph(vertexArray, matrix);

        // get the  50% receivers of the current sender and print out the detailed information in the terminal
        String s = graph.getThreeNearest(sender);
        String[] ss = s.split(" ");
        int b = len/2;
        String[]k = Arrays.copyOfRange(ss, len-b ,len-1);
        String x = "";
        for (String sss : k){
            x+=sss+" ";
            System.out.println(x);
        }

        return x.split(" ");
    }


    public static void main(String[] args) {

        // For test

        String[] vertexArray = {"A","B","C","D","E","F","G"};

        int[][] matrix = new int[vertexArray.length][vertexArray.length]; //7*7
        int len = vertexArray.length;
        int i = 0;
        int j = 0;

        while(i<len){
            matrix[i] = new int[len];
            i++;
        }
        int ii;
        for (ii= 0; ii<len;ii++){

            for (j= 0; j<len;j++){
                if(ii!=j) {
                    Random r = new Random();
                    matrix[ii][j] = r.nextInt(10)+1;
                    matrix[j][ii] = matrix[ii][j];

            }}}
        Graph graph = new Graph(vertexArray, matrix);
        String s = graph.getThreeNearest("G");
        String[] ss = s.split(" ");
        String[]k = Arrays.copyOfRange(ss, 3,6);

        // print out the three nearest receivers in the terminal
        for (String sss : k){
            System.out.println(sss);
        }

    }
}

class Graph implements Serializable {
    private static final long serialVersionUID = -6363517010637626584L;

    // the array that stores all the vertexes in the graph
    private String[] vertexArray;
    // the array that stores all the edges in the graph
    private int[][] edgeArray;

    // store the last vertex in this path during operation
    private int[] pre;
    // store information that wheter a vertex is visited or not
    private boolean[] visited;

    // the array that stores the distance from a given starting point to all the other vetexes in the graph
    // and eventually get the shortest distance of this point to others
    private int[] distance;


    // Graph Constructor
    public Graph(String[] vertexArray, int[][] edgeArray) {
        this.vertexArray = vertexArray;
        this.edgeArray = edgeArray;

        this.distance = new int[this.vertexArray.length];
        for(int i = 0; i < this.vertexArray.length; i++) {
            this.distance[i] = Integer.MAX_VALUE/2;
        }

        this.pre = new int[this.vertexArray.length];
        for(int i = 0; i < this.vertexArray.length; i++) {
            this.pre[i] = i;
        }

        this. visited = new boolean[this.vertexArray.length];
    }


    // Dijkstra Algorithm

    private void DijkstraAlgorithm(String initVertex) {

        int vertexIndex;
        vertexIndex = this.getIndexOfVertex(initVertex);

        // initialize the original distance to 0
        this.distance[vertexIndex] = 0;

        // store the visiting order of each vertex
        LinkedList<String> visitingOrder = new LinkedList<>();

        // get the shortest path from the indexed vertex
        updatePath(vertexIndex, visitingOrder);

        String vertex;
        // Breadth-First Search
        while(!visitingOrder.isEmpty()) {
            vertex = visitingOrder.removeFirst();
            vertexIndex = this.getIndexOfVertex(vertex);
            updatePath(vertexIndex, visitingOrder);
        }
    }

   // get the shortest path from the indexed vertex
    private void updatePath(int vertexIndex, LinkedList<String> visitingOrder) {

        this.visited[vertexIndex] = true;
        int dis;
        for(int i = 0; i < this.vertexArray.length; i++) { //iterate the distance of this vertex to all the others
            dis = this.distance[vertexIndex] + this.edgeArray[vertexIndex][i];
            // compare distances
            if(!this.visited[i] && dis < this.distance[i]) {
                // update the distance
                this.distance[i] = dis;
                this.pre[i] = vertexIndex;
                if(this.edgeArray[vertexIndex][i] != Integer.MAX_VALUE/2 && !visitingOrder.contains(this.vertexArray[i])) {
                    visitingOrder.addLast(this.vertexArray[i]);
                }
            }
        }
    }


    public String getThreeNearest(String initVertex) {
        // dijkstra algorithm
        DijkstraAlgorithm(initVertex);
        Map<String, Integer> result = new HashMap<>();

        // provide detailed information of the graph with shortest path to each vertexes
        int indexOfInitVertex = this.getIndexOfVertex(initVertex);
        int tempIndex;
        String tempStr = "";
        for(int i = 0; i < this.distance.length; i++) {
            tempIndex = i;
            while((tempIndex = this.pre[tempIndex]) != indexOfInitVertex) {
                tempStr = this.vertexArray[tempIndex] + "->" + tempStr;
            }
            tempStr = this.vertexArray[indexOfInitVertex] + "->" + tempStr + this.vertexArray[i];
            result.put(this.vertexArray[i],this.distance[i]);

            System.out.println("The shortest path of "+this.vertexArray[indexOfInitVertex] + " to " + this.vertexArray[i] + " is " + this.distance[i]
                    + " (" + tempStr + ")");
            tempStr = "";
        }


        // to further obtain three receivers from the graph

        return sortMapByValues(result);



    }

    // sort the map by values (big to small)
    public static < String extends Comparable, Integer extends Comparable> java.lang.String sortMapByValues(Map<String, Integer> aMap) {
        HashMap<String, Integer> finalOut = new LinkedHashMap<>();
        aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
        java.lang.String s = "";
        for(String i:finalOut.keySet()){
            s+=i+" ";
        }
        return s;


    }

    private int getIndexOfVertex(String vertex) {
        for(int i = 0; i < this.vertexArray.length; i++) {
            if(vertex.equals(this.vertexArray[i])) {
                return i;
            }
        }
        return -1;
    }




}
