package hello.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Sinks;

import java.util.Arrays;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class FluxTest {

    @Test void just() throws Exception {
        Flux<String> flux = Flux.just("blue", "green", "orange", "purple")
                .map(String::toUpperCase);
        flux.subscribe(d -> System.out.println("s1: " + d));
        flux.subscribe(d -> System.out.println("s2: " + d));
    }

    @Test void hotSource() {
        Sinks.Many<String> hotSource = Sinks.many().multicast().directBestEffort();
        Flux<String> hotFlux = hotSource.asFlux().map(String::toUpperCase);
        hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));
        hotSource.emitNext("blue", FAIL_FAST);
        hotSource.tryEmitNext("green").orThrow();

        hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

        hotSource.emitNext("orange", FAIL_FAST);
        hotSource.emitNext("purple", FAIL_FAST);
        hotSource.emitComplete(FAIL_FAST);

//        Hooks.onOperatorError(B);
//        Hooks.onEachOperator();

    }
}
