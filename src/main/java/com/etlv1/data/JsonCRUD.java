package com.etlv1.data;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Component
public class JsonCRUD {






    public  AmazonS3 getS3Client() {


        AWSCredentials credentials = new BasicAWSCredentials("AKIATTU5R5N2HPB6TFCI","4j8nPLhtVnBXzrZMYSFQ4vU+mIlpV5Uyp9vUar+T");
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();
        return s3client;
    }



    public  String horario() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public  List<JSONObject> transformTickerBinance(List<String> inputDataApi) throws JsonProcessingException {
        Instant now1 = Instant.now();
        List<JSONObject> readyjsonObject = (List)Flux.fromIterable(inputDataApi).parallel().runOn(Schedulers.parallel()).map((json) -> {
            return new JSONObject(json);
        }).map((json) -> {
            return json.put("time", horario());
        }).map((json) -> {
            return json.put("intervaltime", "10m");
        }).map((json) -> {
            return json.put("ID", SequenceID.getid().block());
        }).sequential().collectList().block();
        return readyjsonObject;
    }
    public  void writeJsonTickerBinance(List<JSONObject> inputjsonObject) throws IOException {

        Flux.fromIterable(inputjsonObject).parallel().runOn(Schedulers.parallel()).map((json) -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json.toString());
                String value2 = jsonNode.get("symbol").asText();
                String value3 = jsonNode.get("ID").asText();
                Double value4 = jsonNode.get("priceChange").asDouble();
                Double value5 = jsonNode.get("quoteVolume").asDouble();
                Double value6 = jsonNode.get("volume").asDouble();
                Double value7 = jsonNode.get("priceChangePercent").asDouble();
                String value8 = jsonNode.get("time").asText();
                String value9 = jsonNode.get("intervaltime").asText();
                StringWriter stringWriter = new StringWriter();
                CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
                List<String> headers = Arrays.asList("symbol", "ID", "priceChange", "quoteVolume", "volume", "priceChangePercent", "time", "intervaltime");
                csvPrinter.printRecord(headers);
                csvPrinter.printRecord(new Object[]{ value2, value3, value4, value5, value6, value7, value8, value9});
                csvPrinter.close();
                String csvData = stringWriter.toString();
                String bucketName = "s3-raw-data-drypto";
                String var10000 = Get.clientTimestamp();
                String s3Key = "databaseCrypto/binance_mexc/BinanceDate:_" + var10000 + "_" + String.valueOf(json.get("symbol")) + ".csv";
                JsonCRUD js = new JsonCRUD();
                AmazonS3 s3Client = js.getS3Client();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength((long)csvData.length());
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, new ByteArrayInputStream(csvData.getBytes()), metadata);
                s3Client.putObject(putObjectRequest);
            } catch (JsonProcessingException var21) {
                var21.printStackTrace();
            } catch (IOException var22) {
                var22.printStackTrace();
            }

            return json;
        }).sequential().subscribe();
    }

    public  List<JSONObject> transformTickerCoinmarketCap(List<String> inputDataApi) throws JsonProcessingException {
        List<String> listmanipulated = (List)Flux.fromIterable(inputDataApi).parallel().runOn(Schedulers.parallel()).map((data) -> {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode l = objectMapper.readTree(data.toString());
                return l;
            } catch (JsonProcessingException var3) {
                var3.printStackTrace();
                return null;
            }
        }).map((jsonNode) -> {
            return (JsonNode)((Map.Entry)jsonNode.get("data").fields().next()).getValue();
        }).map((jsonNode) -> {
            return jsonNode.toString().substring(1, jsonNode.toString().length() - 1);
        }).sequential().collectList().block();
        Instant now2 = Instant.now();
        List<JSONObject> readyjsonObject = (List)Flux.fromIterable(listmanipulated).parallel().runOn(Schedulers.parallel()).map((string) -> {
            return new JSONObject(string);
        }).map((json) -> {
            JSONObject quoteObject = json.getJSONObject("quote");
            JSONObject quoteUSDObject = quoteObject.getJSONObject("USD");
            json.put("fully_diluted_market_cap", quoteUSDObject.getFloat("fully_diluted_market_cap"));
            json.put("percent_change_1h", quoteUSDObject.getFloat("percent_change_1h"));
            json.put("percent_change_24h", quoteUSDObject.getFloat("percent_change_24h"));
            json.put("market_cap", quoteUSDObject.getFloat("market_cap"));
            json.put("volume_change_24h", quoteUSDObject.getFloat("volume_change_24h"));
            json.put("price", quoteUSDObject.getFloat("price"));
            json.put("volume_24h", quoteUSDObject.getFloat("volume_24h"));
            json.put("symbolcm", json.get("symbol").toString() + "USDT");
            return json;
        }).map((json) -> {
            return json.put("time", horario());
        }).map((json) -> {
            return json.put("intervaltime", "10m");
        }).map((json) -> {
            return json.put("ID", SequenceID.getid().block());
        }).sequential().collectList().block();
        readyjsonObject = eliminarCampo(readyjsonObject, "quote");
        readyjsonObject = eliminarCampo(readyjsonObject, "symbol");
        return readyjsonObject;
    }

    public  void writeJsonTickerCoinmarketCap(List<JSONObject> jo) throws IOException {
        Flux.fromIterable(jo).parallel().runOn(Schedulers.parallel()).map((ss) -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(ss.toString());
                String value2 = jsonNode.get("symbolcm").asText();
                String value3 = jsonNode.get("ID").asText();
                Double value4 = jsonNode.get("percent_change_1h").asDouble();
                Double value5 = jsonNode.get("percent_change_24h").asDouble();
                Double value6 = jsonNode.get("market_cap").asDouble();
                Double value7 = jsonNode.get("volume_change_24h").asDouble();
                Double value8 = jsonNode.get("price").asDouble();
                Double value9 = jsonNode.get("volume_24h").asDouble();
                String value10 = jsonNode.get("time").asText();
                String value11 = jsonNode.get("fully_diluted_market_cap").asText();
                Double value12 = jsonNode.get("circulating_supply").asDouble();
                Double value13 = jsonNode.get("total_supply").asDouble();
                String value14 = jsonNode.get("intervaltime").asText();
                StringWriter stringWriter = new StringWriter();
                CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
                List<String> headers = Arrays.asList("symbolcm", "ID", "percent_change_1h", "percent_change_24h", "market_cap", "volume_change_24h", "price", "volume_24h", "time", "fully_diluted_market_cap", "circulating_supply", "total_supply", "intervaltime");
                csvPrinter.printRecord(headers);
                csvPrinter.printRecord(new Object[]{ value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14});
                csvPrinter.close();
                String csvData = stringWriter.toString();
                String bucketName = "s3-raw-data-drypto";
                String var10000 = Get.clientTimestamp();
                String s3Key = "databaseCrypto/coinmarketCap/CoinmarketCapDate:_" + var10000 + "_" + String.valueOf(ss.get("symbolcm")) + ".csv";
                JsonCRUD js = new JsonCRUD();
                AmazonS3 s3Client = js.getS3Client();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength((long)csvData.length());
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, new ByteArrayInputStream(csvData.getBytes()), metadata);
                s3Client.putObject(putObjectRequest);
                return jsonNode;
            } catch (JsonProcessingException var26) {
                var26.printStackTrace();
            } catch (IOException var27) {
                var27.printStackTrace();
            }

            return ss;
        }).sequential().subscribe();
    }

    public  List<JSONObject> transformTickerMEXC(List<String> inputDataApi) throws JsonProcessingException {
        Instant now = Instant.now();
        List<JSONObject> readyjsonObject = (List)Flux.fromIterable(inputDataApi).parallel().runOn(Schedulers.parallel()).map((json) -> {
            return new JSONObject(json);
        }).map((json) -> {
            return json.put("time", horario());
        }).map((json) -> {
            return json.put("intervaltime", "10m");
        }).map((json) -> {
            return json.put("ID", SequenceID.getid().block());
        }).sequential().collectList().block();
        return readyjsonObject;
    }

    public  void writeJsonTickerMEXC(List<JSONObject> inputjsonObject) throws IOException {
        Flux.fromIterable(inputjsonObject).parallel().runOn(Schedulers.parallel()).map((json) -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json.toString());
                String value2 = jsonNode.get("symbol").asText();
                String value3 = jsonNode.get("ID").asText();
                Double value4 = jsonNode.get("priceChange").asDouble();
                Double value5 = jsonNode.get("quoteVolume").asDouble();
                Double value6 = jsonNode.get("volume").asDouble();
                Double value7 = jsonNode.get("priceChangePercent").asDouble();
                String value8 = jsonNode.get("time").asText();
                String value9 = jsonNode.get("intervaltime").asText();
                StringWriter stringWriter = new StringWriter();
                CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
                List<String> headers = Arrays.asList("symbol", "ID", "priceChange", "quoteVolume", "volume", "priceChangePercent", "time", "intervaltime");
                csvPrinter.printRecord(headers);
                csvPrinter.printRecord(new Object[]{ value2, value3, value4, value5, value6, value7, value8, value9});
                csvPrinter.close();
                String csvData = stringWriter.toString();
                String bucketName = "s3-raw-data-drypto";
                String var10000 = Get.clientTimestamp();
                String s3Key = "databaseCrypto/binance_mexc/MexcDate:_" + var10000 + "_" + String.valueOf(json.get("symbol")) + ".csv";
                JsonCRUD js = new JsonCRUD();
                AmazonS3 s3Client = js.getS3Client();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength((long)csvData.length());
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, new ByteArrayInputStream(csvData.getBytes()), metadata);
                s3Client.putObject(putObjectRequest);
                return jsonNode;
            } catch (JsonProcessingException var21) {
                var21.printStackTrace();
            } catch (IOException var22) {
                var22.printStackTrace();
            }

            return json;
        }).sequential().subscribe();
    }

    public  List<JSONObject> eliminarCampo(List<JSONObject> jsonData, String campoAEliminar) {
        Iterator var2 = jsonData.iterator();

        while(var2.hasNext()) {
            JSONObject json = (JSONObject)var2.next();
            json.remove(campoAEliminar);
        }

        return jsonData;
    }

}
