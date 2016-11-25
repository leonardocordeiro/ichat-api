package br.com.caelum.ichat.controller;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import br.com.caelum.ichat.callback.TimeoutCallback;
import br.com.caelum.ichat.callbacks.ClientCallback;
import br.com.caelum.ichat.model.Message;

@Controller
@RequestMapping("/polling")
public class PollingController {

	private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
	private Queue<DeferredResult<Message>> clients = new ConcurrentLinkedQueue<>();
	
	@PostConstruct
	public void init()  {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Message message = messages.take();
						sendToClients(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void sendToClients(Message message)  {
		for (DeferredResult<Message> client : clients) {
			client.setResult(message);
		}
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public DeferredResult<Message> ouvirMensagem()  {
		
		long timeout = 20 * 1000L;
		final DeferredResult<Message> client = new DeferredResult<>(timeout);
		
		TimeoutCallback timeoutCallback = new TimeoutCallback(client, clients);
		ClientCallback clientCallback = new ClientCallback(client, clients);
		
		client.onTimeout(timeoutCallback);
		client.onCompletion(clientCallback);
		
		clients.offer(client);
		return client;
	}

	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void doPost(@RequestBody Message message)  {
		messages.add(message);
	}
}