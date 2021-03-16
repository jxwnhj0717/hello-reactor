package hello.reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoTest {

    @Test void just() {
        Mono.just("hello").subscribe(System.out::println);
    }

    @Test void just2() {
        Mono.just("hello").subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    @Test void filterAndMap() {
        Mono<Integer> mono = Mono.just(1); // MonoJust
        Mono<Integer> mono1 = mono.filter(i -> i > 0); // MonoFilterFuseable
        Mono<Character> mono2 = mono1.map(i -> (char) (i + 'a')); // MonoMapFuseable
        mono2.subscribe(s -> System.out.println(Thread.currentThread() + ": " + s));
    }

    @Test void publishOn() {
        Mono.defer(() -> Mono.just(1))
                .publishOn(Schedulers.parallel())
                .subscribe(s -> System.out.println(Thread.currentThread() + ": " + s));
    }

    @Test void subscribeOn() {
        Mono.defer(() -> Mono.just(1))
                .subscribeOn(Schedulers.parallel())
                .subscribe(s -> System.out.println(Thread.currentThread() + ": " + s));
    }
}
