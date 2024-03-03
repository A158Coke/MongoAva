package com.dam.U5EX01_YC.Mongo;

import com.dam.U5EX01_YC.Util.Configuration;
import com.dam.U5EX01_YC.Util.Constantes;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static java.lang.Float.sum;

@Component
public class MongoUtil {
    private final Configuration config;

    @Autowired
    public MongoUtil(Configuration config) {
        this.config = config;
    }

    //Connection Parts
    public MongoClient getDbClient() {
        return MongoClients.create(config.getConnectionString());
    }

    public MongoDatabase getDB() {
        return getDbClient().getDatabase(config.getDatabaseName());

    }

    public MongoCollection<Document> getCollection() {
        return getDB().getCollection(config.getCollectionName());
    }

    //---------------------------------------------------------------------------------------------------------
    //Insert Parts
    //Metodo para leer el fichero csv
    public LinkedList<Document> readCSVFile() {
        LinkedList<Document> list = new LinkedList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getDataFormatRegex());
        // Leer el csv file con csv reader, OpenCSV library
        try (CSVReader csvReader = new CSVReader(new FileReader(config.getCsvFilePath()))) {
            // Leer primera liena como el titulo, si no hay titolo en el fichero csv. Commentar esta linea
            String[] titulos = csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                Document document = new Document();
                for (int i = 0; i < titulos.length; i++) {
                    titulos[i] = titulos[i].trim().toLowerCase();
                    String titulo = titulos[i];
                    String valor = line[i].trim();
                    if (titulo.equalsIgnoreCase(Constantes.date)) {
                        LocalDateTime date = LocalDateTime.parse(valor, formatter);
                        document.append(titulo, date);
                    } else {
                        document.append(titulo, valor);
                    }
                }
                list.add(document);
            }
        } catch (IOException | CsvException e) {
            System.err.println("Hay error en el metodo readCSVFile");
            e.printStackTrace();
        }
        return list;
    }

    //Metodo para insertar los datos de fichero csv al mongo.
    public void insertDocToDB() {
        LinkedList<Document> list = readCSVFile();
        try {
            getCollection().insertMany(list);
            System.out.println("Data insertado!");
        } catch (Exception e) {
            System.err.println("Error en el metodo de insertDocToDB");
            e.printStackTrace();
        }
    }


    //--------------------------------------------------------------------------------------------------
    //Consulta part
    //Metodo para hacer la consulta 5.
    public void ConsultaNumeroDeVenta(String itemName) {
        int total = 0;
        try {
            Iterable<Document> resultados = getCollection().find(new Document(Constantes.item, itemName));
            for (Document doc : resultados) {
                int quantity = Integer.parseInt(doc.getString(Constantes.quantity));
                total += quantity;
            }
        } catch (Exception e) {
            System.err.println("Error en el metodo ConsultaNumeroDeVenta");
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Total venta de producto: " + itemName + ", su numero de venta es: " + total);
    }

    //Consulta numero5
    public void ConsultaVenta(int Month, int Year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(config.getDataFormatRegex());
        String startDay = Year + "-" + Month + "-01T00:00:00Z";
        int nextYear = Year;
        int nextMonth = Month + 1;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextYear++;
        }
        String endDay = nextYear + "-" + nextMonth + "-01T00:00:00Z";
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(startDay);
            endDate = simpleDateFormat.parse(endDay);
        } catch (ParseException e) {
            System.err.println("Error en  el ConsultaVenta MEthod");
            e.printStackTrace();
        }

        //Asegurar que startDate y end Date no son nulos
        assert startDate != null;
        assert endDate != null;
        BasicDBObject query = new BasicDBObject(Constantes.date,
                new BasicDBObject(Constantes.granteThan, startDate).append(Constantes.littleThan, endDate));


        FindIterable<Document> cursor = getCollection().find(query);
        double totalVentas = 0;

        for (Document doc : cursor) {
            int price = 0;
            int quantity = 0;
            try {
                price = Integer.parseInt(doc.getString(Constantes.price));
                quantity = Integer.parseInt(doc.getString(Constantes.quantity));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            totalVentas += price * quantity;
        }
        System.out.println("--------------------------------------------------------------------------");
        switch (Month) {
            case 1 -> System.out.println("Total ventas de Enero : " + totalVentas + "Euros");
            case 2 -> System.out.println("Total ventas de Febrero : " + totalVentas + "Euros");
            case 3 -> System.out.println("Total ventas de Marzo : " + totalVentas + "Euros");
            case 4 -> System.out.println("Total ventas de April : " + totalVentas + "Euros");
            case 5 -> System.out.println("Total ventas de Mayo : " + totalVentas + "Euros");
            case 6 -> System.out.println("Total ventas de Junio : " + totalVentas + "Euros");
            case 7 -> System.out.println("Total ventas de Julio : " + totalVentas + "Euros");
            case 8 -> System.out.println("Total ventas de Augosto : " + totalVentas + "Euros");
            case 9 -> System.out.println("Total ventas de Septiembre : " + totalVentas + "Euros");
            case 10 -> System.out.println("Total ventas de Octubre : " + totalVentas + "Euros");
            case 11 -> System.out.println("Total ventas de Noviembre : " + totalVentas + "Euros");
            case 12 -> System.out.println("Total ventas de Diciembre : " + totalVentas + "Euros");
            default -> System.out.println("Error De Mes");
        }

//        while (iterator.hasNext()) {
//            Document doc = iterator.next();
//            int cantidad = Integer.parseInt(doc.getString(Constantes.quantity));
//            int precio = Integer.parseInt(doc.getString(Constantes.price));
//            totalVentas += cantidad * precio;
//        }

        // System.out.println("Total ventas: "+totalVentas);

    }


}

//Method abandoned because cant solve problem of address contain comma so spliter cant ignore it even its btw "". Then
//generate problems that out of bound(index). At the end, I gave up, turn into OpenCSV library to solve this problem
    /*
    public String handleCsvComma(String str) {
        String handledStr = str;
        if (str.contains(",")) {
            if (str.matches("^\".*\"$")) {

                handledStr = str.substring(1, str.length() - 1);
            } else {
                handledStr = str.replace(",", "###comma###");
            }
        }
        return handledStr;
    }


    public static void readCSVFile() {
        LinkedList<Document> list = new LinkedList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            BufferedReader br = new BufferedReader(new FileReader(getCsvFilePath()));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {

                String[] content = line.split(",");
                Document document = new Document()
                        .append("item", content[0].trim().toUpperCase())
                        .append("email", content[1].trim())

                        .append("date", LocalDateTime.parse(content[5], formatter));

                list.add(document);


                if (list.size() > 800) {
                    collection.insertMany(list);
                    list.clear();
                }
            }


            if (!list.isEmpty()) {
                collection.insertMany(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */

/*
      while ((line = br.readLine()) != null) {
            //String part
            String[] content = line.split(",");
            String item = content[0].trim().toUpperCase();
            String email = handleCsvComma(content[1].trim());
            String telefone = handleCsvComma(content[2].trim());
            String address = handleCsvComma(content[3].trim());
            String quantity = handleCsvComma(content[4].trim());
            LocalDateTime date = LocalDateTime.parse(content[5], formater);


            System.out.println(address);

                /*


                //Mongo part
                Document document = new Document();
                document.put("item", item);
                document.put("email", email);
                document.put("address", address);
                document.put("price", price);
                document.put("quantity", quantity);
                document.put("date", date);

                list.add(document);


                if (list.size() > 800) {
                    collection.insertMany(list);
                    list.clear();
                }

        }

                if (!list.isEmpty()) {
                collection.insertMany(list);
                }


                } catch(
                Exception e)

                {
                e.printStackTrace();
                }
 */
