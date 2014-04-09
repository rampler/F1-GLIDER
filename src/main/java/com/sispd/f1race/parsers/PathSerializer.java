package com.sispd.f1race.parsers;

import com.sispd.f1race.Path;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class PathSerializer {


    public static void main(String[] args) {
        Path path;

//        PathParser bahrainPathParser = new PathParser(8500, 8201, 150, "bahrain");
//        path = bahrainPathParser.parse();
//        System.out.println("Path length: " + path.calculateLength() / 10000 + " km");

        PathParser silverstonePathParser = new PathParser(7597, 3826, 170, "silverstone");
        path = silverstonePathParser.parse();
        System.out.println("Path length: " + path.calculateLength() / 10000 + " km");

        try {
            FileOutputStream fileOut = new FileOutputStream("./" + path.name + ".path");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(path);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + path.name + ".path");

        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
