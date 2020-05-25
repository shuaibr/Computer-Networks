import java.util.Scanner;

public class LinkState {
    public static int[][] weights;
    public static int nVal;
    public static int nextHop;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // n vertices, topology of AS
        System.out.println("Enter n value: ");
        nVal = Integer.parseInt(scanner.nextLine());

        // adjacency matrix with weights
        weights = new int[nVal][nVal];

        int i = 1;
        while (i <= nVal) {
            System.out.println("Enter next row: ");
            String row = scanner.nextLine();

            int j = 0;
            for (String r : row.split(" ")) {
                weights[i - 1][j] = Integer.parseInt(r);
                j++;
            }
            i++;
        }

        System.out.println("Enter gateway routers: ");
        String gatewayInput = scanner.nextLine();

        // System.out.println("nval: " + nVal);
        gateway_check(gatewayInput);

        scanner.close();

    }

    public static void dijkstras_algorithm(int s, int g) {

        int[] visited = new int[nVal];
        // output array which includes the shortest distance from s to g
        int[] distance = new int[nVal];
        // if vertex is included in finalized form
        Boolean vInc[] = new Boolean[nVal];
        int nextHop[] = new int[nVal];

        // initialize vInc to false, distance[i] set to infinity
        int i = 0;
        while (i < nVal) {
            distance[i] = Integer.MAX_VALUE;
            vInc[i] = false;
            nextHop[i] = -1;
            i++;
        }

        // distance to s router is always zero
        distance[s] = 0;
        int check = 0;

        // find shortest vector and then print
        for (int count = 0; count < nVal - 1; count++) {
            int shortVec = shortestVector(distance, vInc);
            vInc[shortVec] = true;

            for (int v = 0; v < nVal; v++) {

                if (!vInc[v] && weights[shortVec][v] != -1 && distance[shortVec] != Integer.MAX_VALUE
                        && distance[shortVec] + weights[shortVec][v] < distance[v]) {
                    distance[v] = distance[shortVec] + weights[shortVec][v];
                    if (shortVec == s) {
                        nextHop[v] = v + 1;
                    } else {
                        nextHop[v] = nextHop[shortVec];
                    }
                }
            }

        }
        // if unavailable, print -1
        if (distance[g - 1] == Integer.MAX_VALUE) {
            distance[g - 1] = -1;
        }

        System.out.printf("%d\t%d\t%d" + "\n", g, distance[g - 1], nextHop[g - 1]);

    }

    public static void gateway_check(String gateway) {

        int gateway_len = gateway.split(" ").length;
        String[] gwArray = new String[gateway_len];
        gwArray = gateway.split(" ");

        gateway_check: for (int i = 0; i < nVal; i++) {
            for (int x = 0; x < gateway_len; x++) {
                if (i + 1 == Integer.parseInt(gwArray[x])) {
                    // System.out.println("gcheck i val: " + Integer.parseInt(gwArray[x]));
                    continue gateway_check;
                }
            }
            print_forwarding(i);
            for (int x = 0; x < gateway_len; x++) {
                dijkstras_algorithm(i, Integer.parseInt(gwArray[x]));
            }
        }
    }

    public static int shortestVector(int check[], Boolean visited[]) {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < nVal; v++)
            if (visited[v] == false && check[v] <= min) {
                min = check[v];
                min_index = v;
            }

        return min_index;
    }

    public static void print_forwarding(int i) {
        System.out.println("Forwarding table for " + (i + 1));
        System.out.println("To\tCost\tNext");
    }

}