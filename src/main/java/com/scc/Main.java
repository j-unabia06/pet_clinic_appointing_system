package  com.scc;
import com.scc.dao.UserDao;

import java.util.Scanner;


public class Main  {

    public static final Scanner sc = new Scanner(System.in);
    public static final UserDao userDao = new UserDao();
    public static void main(String[] args){
        System.out.println("====================================");
        System.out.println("=== Pet Clinic Appointing System ===");
        System.out.println("1. login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Select: ");
        String select = sc.nextLine();
        System.out.println("====================================");


        switch (select){
            case "1":
                break;
            case "2":
                System.out.print("Enter Username: ");
                String user = sc.nextLine();
                System.out.print("Enter Password: ");
                String pass = sc.nextLine();
                System.out.println("Select role: [1] Admin [2]Veterinarian [3]Receptionist");
                String roleChoice = sc.nextLine();
                String role;
                switch (roleChoice){
                    case "1":
                        role = "ADMIN";
                        break;
                    case "2":
                        role = "VETERINARIAN";
                        break;
                    case "3":
                        role = "RECEPTIONIST";
                        break;
                    default:
                        role = null;
                        break;
                }
                if(role != null){
                    userDao.registerUser(user, pass, role);
                }else {
                    System.out.println("INVALID ROLE SELECTION!");
                }

                break;
            case "3":
                System.out.printf("Thank you for using PET CLINIC APPOINTING SYSTEM.");
                break;
        }
    }
}