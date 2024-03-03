package com.dam.U5EX01_YC;

import com.dam.U5EX01_YC.Mongo.MongoUtil;
import com.dam.U5EX01_YC.Util.Constantes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;

import java.text.ParseException;
import java.util.Scanner;

@SpringBootApplication
public class U5Ex01YcApplication {

    public static void main(String[] args) {
        MongoUtil mongoUtil = SpringApplication.run(U5Ex01YcApplication.class, args).getBean(MongoUtil.class);
        try( Scanner sc = new Scanner(System.in);) {
            while (true) {
                System.out.println("Que quieres hacer?");
                System.out.println("1. Insertar los datos csv al mongo DB");
                System.out.println("2. Consulta el numero de venta de un producto");
                System.out.println("3. Consulta numero de venta de un mes");
                System.out.println("4. Exit");
                System.out.println("Introduce el numero de operacion que quieres hacer ");
                int choose = sc.nextInt();
                switch (choose) {
                    case 1 ->
                            mongoUtil.insertDocToDB(); //Este metodo para insert el fichero csv al MONGO DB, solo ejecuta una vez
                    case 2 -> {
                        System.out.println("Que producto quieres que consulta???");
                        sc.nextLine();
                        String producto = sc.nextLine();
                        mongoUtil.ConsultaNumeroDeVenta(producto); //Este metodo para consulta el numero de venta de un producto
                    }
                    case 3 -> {
                        System.out.println("Que mes quieres que consulta???");
                        sc.nextLine();
                        int mes = sc.nextInt();
                        System.out.println("Que año quieres que consulta???");
                        sc.nextLine();
                        int year = sc.nextInt();
                        if (mes > 12 || mes <= 0) {
                            System.err.println("Error, numero de mes invalid");
                        } else if (year <= 0) {
                            System.err.println("Error, numero de año invalid");
                        } else {
                            mongoUtil.ConsultaVenta(mes, year); //Metodo para consulta total ventas(euros) del mes y año especifico
                        }
                    }
                    case 4 -> System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
