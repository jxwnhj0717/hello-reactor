package hello.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;

public class ContextTest {

    @Test void deferContextual() {
        String key = "message";
        Mono<String> r = Mono.just("Hello")
                .flatMap(s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        r.subscribe(System.out::println);
    }

    @Test void deferContextual2() {
        String key = "message";
        Mono<String> r = Mono.just("Hello")
                .contextWrite(ctx -> ctx.put(key, "World"))
                .flatMap( s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.getOrDefault(key, "Stranger"))));

        r.subscribe(System.out::println);
    }

    static final String HTTP_CORRELATION_ID = "reactive.http.library.correlationId";

    Mono<Tuple2<Integer, String>> doPut(String url, Mono<String> data) {
        Mono<Tuple2<String, Optional<Object>>> dataAndContext =
                data.zipWith(Mono.deferContextual(c ->
                        Mono.just(c.getOrEmpty(HTTP_CORRELATION_ID)))
                );

//        return dataAndContext.<String>handle((dac, sink) -> {
//            if (dac.getT2().isPresent()) {
//                sink.next("PUT <" + dac.getT1() + "> sent to " + url +
//                        " with header X-Correlation-ID = " + dac.getT2().get());
//            }
//            else {
//                sink.next("PUT <" + dac.getT1() + "> sent to " + url);
//            }
//            sink.complete();
//        }).map(msg -> Tuples.of(200, msg));
        return dataAndContext.map(dac -> {
            if(dac.getT2().isPresent()) {
                return "PUT <" + dac.getT1() + "> sent to " + url + " with header X-Correlation-ID = " + dac.getT2().get();
            } else {
                return "PUT <" + dac.getT1() + "> sent to " + url;
            }
        }).map(msg -> Tuples.of(200, msg));
    }

    @Test void url() {
        doPut("www.baidu.com", Mono.just("name"))
                .contextWrite(c -> c.put(HTTP_CORRELATION_ID, "hj"))
                .subscribe(System.out::println);
    }

}
