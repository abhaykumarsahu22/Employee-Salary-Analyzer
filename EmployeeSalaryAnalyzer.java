import java.io.*;
import java.util.Scanner;

class Employee {
    String id;
    String name;
    double salary;

    public Employee(String id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    // Format helper: Agar salary whole number hai (e.g., 45000.0) toh "45000" dikhayega, warna decimal
    public String getFormattedSalary() {
        if (salary == (long) salary) {
            return String.format("%d", (long) salary);
        } else {
            return String.format("%.2f", salary);
        }
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + getFormattedSalary();
    }
}

public class EmployeeSalaryAnalyzer {
    private static final String FILE_NAME = "salaries.txt";
    private static final int MAX_EMPLOYEES = 1000;
    
    // Bare Minimum Requirement #2: Store objects in an array and keep count
    private static Employee[] employees = new Employee[MAX_EMPLOYEES];
    private static int count = 0;

    // 1. Load data from salaries.txt at startup
    public static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Expected file format: E101|Amit Sharma|45000.0
                String[] parts = line.split("\\|");
                if (parts.length == 3 && count < MAX_EMPLOYEES) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double salary = Double.parseDouble(parts[2].trim());
                    employees[count++] = new Employee(id, name, salary);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading records from file: " + e.getMessage());
        }
    }

    // 2. Save data to salaries.txt on exit
    public static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                pw.println(employees[i].id + "|" + employees[i].name + "|" + employees[i].salary);
            }
        } catch (IOException e) {
            System.out.println("Error saving records to file: " + e.getMessage());
        }
    }

    // Auto-generate next ID (Starts from E101)
    private static String generateNextId() {
        int maxNum = 100;
        for (int i = 0; i < count; i++) {
            String idStr = employees[i].id;
            if (idStr.startsWith("E") && idStr.length() > 1) {
                try {
                    int num = Integer.parseInt(idStr.substring(1));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return "E" + (maxNum + 1);
    }

    // Menu Option 1: Add Employee with strict validation
    public static void addEmployee(Scanner sc) {
        if (count >= MAX_EMPLOYEES) {
            System.out.println("Storage full! Cannot add more employees.");
            return;
        }

        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Invalid name. Name cannot be empty.");
            return;
        }

        System.out.print("Salary: ");
        String salaryInput = sc.nextLine().trim();
        double salary;

        try {
            salary = Double.parseDouble(salaryInput);
            if (salary <= 0) {
                System.out.println("Invalid salary. Salary must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary. Salary must be a positive number.");
            return;
        }

        String id = generateNextId();
        Employee newEmp = new Employee(id, name, salary);
        employees[count++] = newEmp;

        System.out.println("Employee added. ID: " + newEmp.toString());
    }

    // Menu Option 2: Display All Employees
    public static void displayAll() {
        if (count == 0) {
            System.out.println("No employee records found.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + employees[i].toString());
        }
    }

    // Menu Option 3: Custom Bubble Sort (High to Low) - No built-in sort used
    public static void sortBySalary() {
        if (count == 0) {
            System.out.println("No employee records to sort.");
            return;
        }

        // Create a temporary copy so original insertion order stays intact if needed, or sort directly
        Employee[] sorted = new Employee[count];
        for (int i = 0; i < count; i++) {
            sorted[i] = employees[i];
        }

        // Manual Bubble Sort (Descending Order)
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (sorted[j].salary < sorted[j + 1].salary) {
                    Employee temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + sorted[i].toString());
        }
    }

    // Menu Option 4: Custom Linear Search by Name (Case-insensitive partial match)
    public static void searchByName(Scanner sc) {
        if (count == 0) {
            System.out.println("No employee records available to search.");
            return;
        }

        System.out.print("Enter name to search: ");
        String query = sc.nextLine().trim().toLowerCase();
        boolean found = false;

        for (int i = 0; i < count; i++) {
            if (employees[i].name.toLowerCase().contains(query)) {
                System.out.println("Found: " + employees[i].toString());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No employee found matching: " + query);
        }
    }

    // Menu Option 5: Salary Statistics (Highest, Lowest, Average)
    public static void viewStatistics() {
        if (count == 0) {
            System.out.println("No employee records available for statistics.");
            return;
        }

        Employee highest = employees[0];
        Employee lowest = employees[0];
        double sum = 0;

        for (int i = 0; i < count; i++) {
            if (employees[i].salary > highest.salary) {
                highest = employees[i];
            }
            if (employees[i].salary < lowest.salary) {
                lowest = employees[i];
            }
            sum += employees[i].salary;
        }

        double average = sum / count;

        System.out.printf("Highest: %s (%s) | Lowest: %s (%s) | Average: %.2f\n",
                highest.name, highest.getFormattedSalary(),
                lowest.name, lowest.getFormattedSalary(),
                average);
    }

    // Main Console Loop
    public static void main(String[] args) {
        loadFromFile();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Employee Salary Analyzer ---");
            System.out.println("1. Add Employee");
            System.out.println("2. Display All");
            System.out.println("3. Sort by Salary (high to low)");
            System.out.println("4. Search by Name");
            System.out.println("5. Salary Statistics");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            String choiceStr = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1:
                    addEmployee(sc);
                    break;
                case 2:
                    displayAll();
                    break;
                case 3:
                    sortBySalary();
                    break;
                case 4:
                    searchByName(sc);
                    break;
                case 5:
                    viewStatistics();
                    break;
                case 6:
                    saveToFile();
                    System.out.println("Data saved to " + FILE_NAME + ". Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 6.");
            }
        }
    }
}