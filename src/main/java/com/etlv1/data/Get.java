package com.etlv1.data;



import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Get {
    static String content = "Content-Type";
    static String charset = "application/json; charset=UTF-8";


    public static String clientTimestamp() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "****");
        BinanceApiRestClient client = factory.newRestClient();
        long serverTime = client.getServerTime();
        String timestamp = String.valueOf(serverTime).substring(0, 10);
        return timestamp;
    }

    public static List<String> getTickerBinance() {
        String eth = "https://api.binance.com/api/v3/ticker?symbol=ETHUSDT&windowSize=1d";
        String btc = "https://api.binance.com/api/v3/ticker?symbol=BTCUSDT&windowSize=1d";
        List<String> parameters = Arrays.asList(eth, btc);
        Flux<String> parametersCoinFlux = Flux.fromIterable(parameters);
        List<String> outDataApi = (List)parametersCoinFlux.flatMap((parameterCoin) -> {
            return Mono.fromSupplier(() -> {
                try {
                    return clientBinance(parameterCoin);
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return null;
            }).subscribeOn(Schedulers.parallel());
        }).collectList().block();
        return outDataApi;
    }

    public static String clientBinance(String parameterCoin) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(parameterCoin)).header(content, charset).header("X-MBX-APIKEY", "****").GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return (String)response.body();
    }

    public static List<String> getTickerCoinmarketCap() {
        String eth = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=ETH";
        String btc = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=BTC";
        List<String> parameters = Arrays.asList(eth, btc);
        Flux<String> parametersCoinFlux = Flux.fromIterable(parameters);
        List<String> outDataApi = (List)parametersCoinFlux.flatMap((parameterCoin) -> {
            return Mono.fromSupplier(() -> {
                try {
                    return clientCoinmarketCap(parameterCoin);
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return null;
            }).subscribeOn(Schedulers.parallel());
        }).collectList().block();
        return outDataApi;
    }

    public static String clientCoinmarketCap(String parameterCoin) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(parameterCoin)).header(content, charset).header("X-CMC_PRO_API_KEY", "****").GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return (String)response.body();
    }

    public static String clientMEXC(String parameterCoin) throws IOException, InterruptedException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(parameterCoin)).header(content, charset).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return (String)response.body();
    }

    public static List<String> getTickerMEXC() {
        String eth = "https://api.mexc.com/api/v3/ticker/24hr?symbol=ETHUSDT";
        String btc = "https://api.mexc.com/api/v3/ticker/24hr?symbol=BTCUSDT";
        List<String> parameters = Arrays.asList(eth, btc);
        Flux<String> parametersCoinFlux = Flux.fromIterable(parameters);
        List<String> outDataApi = (List)parametersCoinFlux.flatMap((parameterCoin) -> {
            return Mono.fromSupplier(() -> {
                try {
                    return clientMEXC(parameterCoin);
                } catch (IOException var2) {
                    var2.printStackTrace();
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return null;
            }).subscribeOn(Schedulers.parallel());
        }).collectList().block();
        return outDataApi;
    }
}
