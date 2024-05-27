package com.etlv1.data;

import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SequenceID {

    public static String sequenceid() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fecha = new Date();
        String fechaFormateada = formato.format(fecha).replace(" ", "").replace("/", "").replace("_", "").replace(":", "");
        return fechaFormateada;
    }

    public static Mono<String> generarId() {
        return Mono.fromSupplier(() -> {
            return UUID.randomUUID().toString();
        });
    }

    public static Mono<String> getid() {
        String after = (String)generarId().block();
        String before = after.replace("-", "").substring(0, 11).concat(sequenceid());
        Mono<String> mono = Mono.just(before);
        return mono;
    }
}