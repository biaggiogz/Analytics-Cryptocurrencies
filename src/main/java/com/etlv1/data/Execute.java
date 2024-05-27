package com.etlv1.data;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Execute {

    public static void parallel() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Democall dc = new Democall();


            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    dc.executeMEXC();
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return "Resultado del método 1";
            }, executor);
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                try {
                    dc.executeBinance();
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return "Resultado del método 2";
            }, executor);
            CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
                try {
                    dc.executeCoinMarketCap();
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return "Resultado del método 3";
            }, executor);
            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future3, future2);

            try {
                combinedFuture.get();
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            System.out.println("It´s working");
            Thread.sleep(600000);
        }

}
