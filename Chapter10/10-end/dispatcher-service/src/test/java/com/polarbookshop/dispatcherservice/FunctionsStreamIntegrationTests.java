package com.polarbookshop.dispatcherservice;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class FunctionsStreamIntegrationTests {
	/*
	InputDestination bean representing the input
	binding packlabel-in-0 (by default, since it’s the only one).
	 */
	@Autowired
	private InputDestination input;
	/*
	OutputDestination bean representing the output
	binding packlabel-out-0 (by default, since it’s the only one)
	 */
	@Autowired
	private OutputDestination output;
	// Uses Jackson to deserialize JSON message payloads to Java objects
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void whenOrderAcceptedThenDispatched() throws IOException {
		long orderId = 121;
		Message<OrderAcceptedMessage> inputMessage = MessageBuilder
				.withPayload(new OrderAcceptedMessage(orderId)).build();
		Message<OrderDispatchedMessage> expectedOutputMessage = MessageBuilder
				.withPayload(new OrderDispatchedMessage(orderId)).build();
		// send the message to the input channel
		this.input.send(inputMessage);
		assertThat(objectMapper.readValue(output.receive().getPayload(), OrderDispatchedMessage.class))
				.isEqualTo(expectedOutputMessage.getPayload());
	}

}
