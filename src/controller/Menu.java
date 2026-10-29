package controller;

import dto.IMenu;
import utils.InputUtils;

import java.util.ArrayList;


public class Menu extends ArrayList<String> implements IMenu {

    private static final long serialVersionUID = 1L;

    private final String title;

    public Menu() {
        this("CAR INSURANCE MANAGEMENT SYSTEM");
    }

    public Menu(String title) {
        super();
        this.title = title;
    }

    @Override
    public void addItem(String item) {
        this.add(item);
    }

    @Override
    public void showMenu() {
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("           " + this.title);
        System.out.println("==========================================================");
        for (String item : this) {
            System.out.println("  " + item);
        }
        System.out.println("==========================================================");
    }

    @Override
    public int getChoice() {
        return InputUtils.readIntInRange(">> Please choose an option (1-" + this.size() + "): ", 1, this.size());
    }
}
