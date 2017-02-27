/**
 *
 *  Copyright (c) 2017 Dell Inc., or its subsidiaries.
 *
 */
package com.emc.pravega.controller.client.rest;

import com.emc.pravega.controller.client.rest.generated.api.DefaultApi;
import com.emc.pravega.controller.client.rest.generated.invoker.ApiClient;
import com.emc.pravega.controller.client.rest.generated.model.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import rx.Observable;
import rx.Observer;

import javax.xml.ws.http.HTTPException;
import java.util.concurrent.*;

@Slf4j
public class ControllerClient {
    private final DefaultApi controllerClientService;

    public ControllerClient(final String host, final int port) {
        ApiClient restAPIClient = new ApiClient();
        restAPIClient.getOkBuilder().connectionPool(new ConnectionPool(1, Long.MAX_VALUE, TimeUnit.SECONDS));
        restAPIClient.setAdapterBuilder(restAPIClient.getAdapterBuilder().baseUrl("http://" + host + ":" + port + "/v1/"));
        controllerClientService = restAPIClient.createService(DefaultApi.class);
    }

    private static final class RPCAsyncCallBack<T> {
        private T result = null;
        private final CompletableFuture<T> future = new CompletableFuture<>();

        public RPCAsyncCallBack(Observable<T> observable) {
            observable.subscribe(new Observer<T>() {
                @Override
                public void onCompleted() {
                    future.complete(result);
                }

                @Override
                public void onError(Throwable e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onNext(T t) {
                    result = t;
                }
            });
        }

        public CompletableFuture<T> getFuture() {
            return future;
        }
    }

    public static void main(String[] args) throws Exception {
        ControllerClient client = new ControllerClient("localhost", 9091);







        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                while (true) {
                    final RPCAsyncCallBack<ScopesList> scopes = new RPCAsyncCallBack<>(client.controllerClientService.listScopes());
                    scopes.getFuture().whenComplete(((res, error) -> {
                        if (error != null) {
                            if (error instanceof HTTPException) {
                                if (((HTTPException) error).getStatusCode() == 409) {
                                }
                            }
                        } else {
                            log.info("received list of scoeps");
                        }
                    }));

                    Thread.sleep(1000);
                }
            });
        }
        executorService.wait();
    }
}

