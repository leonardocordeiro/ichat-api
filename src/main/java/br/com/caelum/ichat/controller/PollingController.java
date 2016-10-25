package br.com.caelum.ichat.controller;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import br.com.caelum.ichat.model.Message;

@Controller
@RequestMapping("/polling")
public class PollingController {

	private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
	private Queue<DeferredResult<Message>> clients = new ConcurrentLinkedQueue<>();
	
	@PostConstruct
	public void init() throws ServletException {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					try {
						// its block until receive message
						Message message = messages.take();
						sendToClients(message);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public DeferredResult<Message> ouvirMensagem() throws ServletException, IOException {
		final DeferredResult<Message> result = new DeferredResult<>(20 * 1000L);
		result.onTimeout(new Runnable() {
			@Override
			public void run() {
				clients.remove(result);
				result.setResult(new Message());
			}
		});
		result.onCompletion(new Runnable() {
			@Override
			public void run() {
				clients.remove(result);
			}
		});
		
		
		
		clients.offer(result);
		return result;
	}

	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void doPost(@RequestBody Message message) throws ServletException, IOException {
		messages.add(message);
	}
	
	private void sendToClients(Message message) {
		for (DeferredResult<Message> client : clients) {
			try {
				writeResponseFor(client, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void writeResponseFor(DeferredResult<Message> client, Message message) throws IOException {
		client.setResult(message);
	}

}
