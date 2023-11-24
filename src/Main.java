import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, Integer> appsPerCategory = new HashMap<>();
        Map<String, Integer> top100Companies = new HashMap<>();
        Map<String, Integer> top3Developers = new HashMap<>();
        Map<String, Long> downloads = new HashMap<>();

        try {
            NumOfAppsPerCategory(appsPerCategory);
            Top100Companies(top100Companies);
            Top3Developers(top3Developers);
            Budget(10000.0, 1000.0);
            FreeNPaidDownloads(downloads);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void NumOfAppsPerCategory(Map<String, Integer> appsPerCategory) throws IOException {
        File file = new File("src/Google Play Store Apps.csv");
        Scanner s = new Scanner(file);
        if (s.hasNextLine()) s.nextLine();
        try {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] linePart = SplitLine(line);
                String category = linePart[2].strip();
                if (appsPerCategory.containsKey(category))
                    appsPerCategory.put(category, appsPerCategory.get(category) + 1);
                else
                    appsPerCategory.put(category, 1);
            }
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        FileWriter output = new FileWriter("AppsPerCategory.csv");
        output.write("Category" + "," + "Number of Apps" + "\n");
        try {
            Iterator<Map.Entry<String, Integer>> iterator = appsPerCategory.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> e = iterator.next();
                output.append(e.getKey()).append(String.valueOf(e.getValue())).append("\n");
            }
            output.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static String[] SplitLine(String line) {
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=,|^)(\"(?:[^\"]|\"\")*\"|[^,]*)(?:,|$)");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find())
            values.add(matcher.group().trim());

        return values.toArray(new String[0]);
    }

    private static void Top100Companies(Map<String, Integer> top100Companies) throws IOException {
        File file = new File("src/Google Play Store Apps.csv");
        Scanner s = new Scanner(file);
        if(s.hasNextLine())s.nextLine();
        try{
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] linePart = SplitLine(line);
                String appID = linePart[1].strip();
                String[] parts = appID.split("\\.");
                String company;
                if (parts.length >= 2) company = parts[0] + "." + parts[1];
                else company = appID;

                if (top100Companies.containsKey(company))
                    top100Companies.put(company, top100Companies.get(company) + 1);
                else
                    top100Companies.put(company, 1);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        s.close();

        FileWriter output = new FileWriter("Top100Companies.csv");
        output.write("Company,NumberOfApps\n");
        List<Map.Entry<String, Integer>> top = top100Companies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue((v1, v2) -> v2.compareTo(v1)))
                .collect(Collectors.toList());

        int newSize = Math.min(100, top.size());
        top = top.subList(0, newSize);
        Iterator<Map.Entry<String, Integer>> iterator = top.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> e = iterator.next();
            String company = e.getKey();
            int numberOfApps = e.getValue();
            output.append(company).append(",").append(String.valueOf(numberOfApps)).append("\n");
        }
        output.close();
    }
    private static void Top3Developers(Map<String, Integer> top3Developers) throws IOException {
        File file = new File("src/Google Play Store Apps.csv");
        Scanner s = new Scanner(file);
        if (s.hasNextLine()) s.nextLine();
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] linePart = SplitLine(line);
            String email = linePart[15].strip();
            String devID = linePart[13].strip();
            String company = linePart[1].strip();
            String[] id = company.split("\\.");
            if(id.length >= 2)
                company = id[1];

            if(!email.contains(company)){
                if (top3Developers.containsKey(devID))
                    top3Developers.put(devID, top3Developers.get(devID) + 1);
                else
                    top3Developers.put(devID, 1);
            }
        }

        s.close();
        FileWriter output = new FileWriter("Top3Developers.csv");
        try {
            output.write("Developer" + "," + "NumberOfApps" + "\n");

            List<Map.Entry<String, Integer>> developers = top3Developers.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue((v1, v2) -> v2.compareTo(v1)))
                    .collect(Collectors.toList());

            int newSize = Math.min(3, developers.size());
            developers = developers.subList(0, newSize);

            Iterator<Map.Entry<String, Integer>> iterator = developers.iterator();
            while(iterator.hasNext()){
                Map.Entry<String, Integer> e = iterator.next();
                String developer = e.getKey();
                int numberOfApps = e.getValue();
                output.append(developer).append(String.valueOf(numberOfApps)).append("\n");
            }
            output.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void Budget(Double budget1, double budget2) throws IOException {
        int count1 = 0;
        int count2 = 0;
        double available1 = budget1;
        double available2 = budget2;
        ArrayList<Double> prices = new ArrayList<>();
        File file = new File("src/Google Play Store Apps.csv");
        Scanner s = new Scanner(file);
        if(s.hasNextLine())s.nextLine();

        try{
            while(s.hasNextLine()){
                String line = s.nextLine();
                String[] linePart = SplitLine(line);
                String priceprt = linePart[9].strip();

                if(priceprt.startsWith("\"") || priceprt.endsWith("\""))priceprt = priceprt.replace("\"", "");
                if(priceprt.contains(","))priceprt = priceprt.replace(",", "");
                double price = Double.parseDouble(priceprt);
                prices.add(price);

                for (Double Price : prices)
                    if (available1 >= price) {
                        available1 -= price;
                        count1++;
                    }

                for(Double Price : prices)
                    if(available2 >= price){
                        available2 -= price;
                        count2++;

                    }
            }

        }catch (Exception e){
            System.out.println(e);
        }
        s.close();

        FileWriter output = new FileWriter("NumberOfAppsPerBudget.csv");
        output.write("Budget" + "," + "NumberOfApps" + "\n");
        try {
            output.write(budget1 + "," + count1 + "\n");
            output.write(budget2 + "," + count2 + "\n");

            output.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void FreeNPaidDownloads(Map<String, Long> downloads) throws IOException {
        File file = new File("src/Google Play Store Apps.csv");
        Scanner scanner = new Scanner(file);

        if (scanner.hasNextLine())  scanner.nextLine();

        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] linePart = SplitLine(line);
                String installs = linePart[5].strip();

                if (installs.startsWith("\"") && installs.endsWith("\"")) {
                    installs = installs.substring(1, installs.length() - 1);
                }
                if (installs.contains(".")) installs = installs.replace(".", "");
                if (installs.contains(",")) installs = installs.replace(",", "");
                if (installs.endsWith("+")) installs = installs.replace("+", "");
                if(installs.isEmpty()) installs = "0";

                Long download = Long.parseLong(installs);

                boolean free = Boolean.parseBoolean(linePart[8].strip());

                if (free) {
                    downloads.computeIfPresent("Free apps", (key, oldValue) -> oldValue + download);
                    downloads.computeIfAbsent("Free apps", key -> download);

                } else {
                    downloads.computeIfPresent("Paid apps", (key, oldValue) -> oldValue + download);
                    downloads.computeIfAbsent("Paid apps", key -> download);
                }
            }
        }catch (NumberFormatException e){
            //continue;
        }
        scanner.close();
        try {
            FileWriter output = new FileWriter("FreeNPaidDownloads.csv");
            output.write("Category" + "," + "Downloads" + "\n");

            Iterator<Map.Entry<String, Long>> iterator = downloads.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                output.append(entry.getKey()).append(",").append(String.valueOf(entry.getValue())).append("\n");
            }
            output.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}