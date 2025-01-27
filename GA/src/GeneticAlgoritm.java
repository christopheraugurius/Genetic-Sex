import java.util.*;

public class GeneticAlgoritm {
    public static ArrayList<int[][]> generatePopulation(int jumlahPembangkit, int periode, int[] kebutuhanMaintanace, int[][] periodeMaintanace) {
        Random random = new Random();
        ArrayList<int[][]> populasi = new ArrayList<>();

        for (int n = 0; n < 50; n++) {
            int[] maintanaceTemp = kebutuhanMaintanace.clone();
            int[][] result = new int[jumlahPembangkit][periode];

            for (int i = 0; i < jumlahPembangkit; i++) {
                while (maintanaceTemp[i] > 0) {
                    int index = random.nextInt(periode);

                    if (periodeMaintanace[i][index] == 0) {
                        result[i][index] = 1;
                        maintanaceTemp[i]--;
                    }
                }
            }

            populasi.add(result);
        }

        return populasi;
    }

    public static HashMap<int[][], Integer> fitnessCheck(ArrayList<int[][]> populasi, int[] kapasitasPembangkit, int cadanganListrik, int kebutuhanListrik) {
        HashMap<int[][], Integer> fitnessScore = new HashMap<>();
        float mean = 0;

        for (int[][] pops: populasi) {
            int totalSelisih = 0;

            for (int i=0; i<pops[0].length; i++) {
                int total = 0;
                for (int j=0; j<pops.length; j++) {
                    if (pops[j][i] == 0) {
                        total += kapasitasPembangkit[j];
                    }
                }

                if ((cadanganListrik + kebutuhanListrik) - total >= 0) {
                    totalSelisih += (cadanganListrik + kebutuhanListrik) - total;
                }

            }
            fitnessScore.put(pops, totalSelisih);
            mean += totalSelisih;
        }
        System.out.println(mean / 50);
        return fitnessScore;
    }

    public static ArrayList<int[][]> crossover(HashMap<int[][], Integer> fitnessScore) {
        HashMap<int[][], Integer> elite = elitism(fitnessScore);
        HashMap<int[][], Integer> parentsHash = selection(fitnessScore);

        ArrayList<int[][]> parentsList = new ArrayList<>(parentsHash.keySet());
        Random random = new Random();
        ArrayList<int[][]> childList = new ArrayList<>(elite.keySet());

        while (childList.size() < 50) {
            int indexSwap = random.nextInt(7);

            int child1 = random.nextInt(parentsList.size());
            int[][] childArray1 = parentsList.get(child1).clone();

            int child2 = random.nextInt(parentsList.size());
            while (child1 == child2) {
                child2 = random.nextInt(parentsList.size());

            }

            int[][] childArray2 = parentsList.get(child2).clone();

            int[] temp = childArray1[indexSwap];
            childArray1[indexSwap] = childArray2[indexSwap];
            childArray2[indexSwap] = temp;

            childList.add(childArray1);

            if (!(childList.size() >= 49)) {
                childList.add(childArray2);

            }
        }

        return childList;
    }

    public static HashMap<int[][], Integer> elitism(HashMap<int[][], Integer> fitnessScore) {
        ArrayList<int[][]> elite = new ArrayList<>(fitnessScore.keySet());
        HashMap<int[][], Integer> eliteMap = new HashMap<>();

        Collections.sort(elite, Comparator.comparingInt(fitnessScore::get));

        for (int i = 0; i < 5; i++) {
            eliteMap.put(elite.get(i), fitnessScore.get(elite.get(i)));
            fitnessScore.remove(elite.get(i));
        }

        return eliteMap;
    }

    public static HashMap<int[][], Integer> selection(HashMap<int[][], Integer> fitnessScore) {
        ArrayList<int[][]> listOfFitness = new ArrayList<>(fitnessScore.keySet());
        HashMap<int[][], Integer> hasilTourney = new HashMap<>();

        while (listOfFitness.size() > 0) {
            Collections.shuffle(listOfFitness);
            ArrayList<int[][]> tourneys = new ArrayList<>();

            for (int i=0; i<5; i++) {
                tourneys.add(listOfFitness.remove(0));
            }

            int smallest = fitnessScore.get(tourneys.get(0));
            int smallestIndex = 0;
            for (int i=1; i<tourneys.size(); i++) {
                if (fitnessScore.get(tourneys.get(i)) < smallest) {
                    smallestIndex = i;
                    smallest = fitnessScore.get(tourneys.get(i));
                }
            }

            hasilTourney.put(tourneys.get(smallestIndex), smallest);
        }

        return hasilTourney;
    }

    public static void main(String[] args) {
        String rawData = "7 100 15\n" +
                "1 20 2\n" +
                "2 15 2\n" +
                "3 35 1\n" +
                "4 40 1\n" +
                "5 15 1\n" +
                "6 15 2\n" +
                "7 10 1";   //FileRead.Read("test.txt");
        String[] data = rawData.split("\n");

        int jumlahPembangkit = Integer.parseInt(data[0].split(" ")[0]);
        int kebutuhanListrik = Integer.parseInt(data[0].split(" ")[1]);
        int cadanganListrik = Integer.parseInt(data[0].split(" ")[2]);
        int periode = 12;
        int[] kebutuhanMaintanance = new int[jumlahPembangkit];
        int[] kapasitasPembangkit = new int[jumlahPembangkit];
        int[][] periodeMaintanace = new int[jumlahPembangkit][periode];

        for (int i=0; i<data.length; i++) {
            if (i == 0) {
                continue;
            }

            kebutuhanMaintanance[i - 1] = Integer.parseInt(data[i].split(" ")[2]);
            kapasitasPembangkit[i - 1] = Integer.parseInt(data[i].split(" ")[1]);
        }


        ArrayList<int[][]> populasi = generatePopulation(jumlahPembangkit, periode, kebutuhanMaintanance, periodeMaintanace);

        for (int i=0 ;i<100; i++) {

            HashMap<int[][], Integer> hasilFitness = fitnessCheck(populasi, kapasitasPembangkit, cadanganListrik, kebutuhanListrik);

            populasi = crossover(hasilFitness);
            System.out.println("---------");
        }
    }
}
