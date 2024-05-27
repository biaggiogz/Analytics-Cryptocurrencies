package com.etlv1.data;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Democall {

    public void executeBinance() throws IOException, InterruptedException {
        try {
            JsonCRUD js = new JsonCRUD();
            List<JSONObject> jo = js.transformTickerBinance(Get.getTickerBinance());
            js.writeJsonTickerBinance(jo);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void executeCoinMarketCap() throws IOException, InterruptedException {
        try {
            JsonCRUD js = new JsonCRUD();
            List<JSONObject> jo = js.transformTickerCoinmarketCap(Get.getTickerCoinmarketCap());
            js.writeJsonTickerCoinmarketCap(jo);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void executeMEXC() throws IOException, InterruptedException {
        try {
            JsonCRUD js = new JsonCRUD();
            List<JSONObject> jo = js.transformTickerMEXC(Get.getTickerMEXC());
            js.writeJsonTickerMEXC(jo);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
