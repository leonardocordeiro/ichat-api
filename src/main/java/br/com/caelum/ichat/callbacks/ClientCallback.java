package br.com.caelum.ichat.callbacks;

import java.util.Queue;

import org.springframework.web.context.request.async.DeferredResult;

import br.com.caelum.ichat.model.Message;

public class ClientCallback implements Runnable {
	
	private DeferredResult<Message> client;
	private Queue<DeferredResult<Message>> clients;
	
	public ClientCallback(DeferredResult<Message> client, Queue<DeferredResult<Message>> clients) {
		this.client = client;
		this.clients = clients;
	}

	@Override
	public void run() {
		clients.remove(client);
	}

}
