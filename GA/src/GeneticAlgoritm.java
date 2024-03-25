import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
        for (int[][] pops: populasi) {
            int score = 0;

            for (int i=0; i<pops[0].length; i++) {
                int totalWatt = 0;
                for (int j=0; j<pops.length; j++) {
                    if (pops[j][i] == 0) {
                        totalWatt += kapasitasPembangkit[j];
                    }
                }

                if (totalWatt >= kebutuhanListrik) {
                    score+= totalWatt;
                } else if (totalWatt + cadanganListrik < kebutuhanListrik) {
                    score = 0;
                    break;
                }
            }


            fitnessScore.put(pops, score);
        }
        return fitnessScore;
    }

    public static void crossover(int[][] pop1, int[][] pop2) {
        Random random = new Random();
        int index1;
        int indexRandom;

        for (int i=0; i<pop1[i].length; i++) {
            index1 = Arrays.asList(pop1[i]).indexOf(0);
            System.out.println(index1);
            indexRandom = random.nextInt(12);

            while (pop1[i][indexRandom] == 1) {
                indexRandom = random.nextInt(12);
            }

            pop1[i][indexRandom] = 1;
            pop1[i][index1] = 0;
        }

        for (int i=0; i<pop2[i].length; i++) {
            index1 = Arrays.binarySearch(pop2[i], 1);
            indexRandom = random.nextInt(12);

            while (pop2[i][indexRandom] == 1) {
                indexRandom = random.nextInt(12);
            }

            pop2[i][indexRandom] = 1;
            pop2[i][index1] = 0;
        }

    }

    public static void main(String[] args) {
        String rawData = "7 100 15\n" +
                "1 21 2\n" +
                "2 14 2\n" +
                "3 37 1\n" +
                "4 12 1\n" +
                "5 10 1\n" +
                "6 13 2\n" +
                "7 27 1";   //FileRead.Read("test.txt");
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
        fitnessCheck(populasi, kapasitasPembangkit, cadanganListrik, kebutuhanListrik);

        System.out.println(Arrays.deepToString(populasi.get(0)));
        System.out.println(Arrays.deepToString(populasi.get(1)));

        crossover(populasi.get(0), populasi.get(1));

        System.out.println(Arrays.deepToString(populasi.get(0)));
        System.out.println(Arrays.deepToString(populasi.get(1)));

    }
}
