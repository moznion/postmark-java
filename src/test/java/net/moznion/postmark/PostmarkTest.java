package net.moznion.postmark;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test for Postmark.
 */
class PostmarkTest {
	@Test
	void shouldCallOnCommitted() throws Exception {
		final AtomicReference<String> record = new AtomicReference<>();
		final String expectedWord = "hello";

		@SuppressWarnings("unchecked")
		final Consumer<String> onCommitted = (Consumer<String>) mock(Consumer.class);
		doAnswer(answer -> {
			record.set(answer.getArgument(0));
			return null;
		}).when(onCommitted).accept(expectedWord);

		@SuppressWarnings("unchecked")
		final Consumer<Void> nop = (Consumer<Void>) Mockito.mock(Consumer.class);

		final Postmark.PostmarkBuilder<String, Void> postmarkBuilder =
			new Postmark.PostmarkBuilder<String, Void>().onCommitted(onCommitted)
														.onCanceled(nop)
														.onNoCommitted(nop);
		try (final Postmark<String, Void> postmark = postmarkBuilder.build()) {
			postmark.commit(expectedWord);
		}
		assertEquals(expectedWord, record.get());

		verify(onCommitted, times(1)).accept(expectedWord);
		verify(nop, times(0)).accept(any());
	}

	@Test
	void shouldCallOnCanceled() throws Exception {
		final AtomicReference<String> record = new AtomicReference<>();
		final String expectedWord = "hello";

		@SuppressWarnings("unchecked")
		final Consumer<String> onCanceled = (Consumer<String>) mock(Consumer.class);
		doAnswer(answer -> {
			record.set(answer.getArgument(0));
			return null;
		}).when(onCanceled).accept(expectedWord);

		@SuppressWarnings("unchecked")
		final Consumer<Void> nop = (Consumer<Void>) Mockito.mock(Consumer.class);

		final Postmark.PostmarkBuilder<Void, String> postmarkBuilder =
			new Postmark.PostmarkBuilder<Void, String>().onCommitted(nop)
														.onCanceled(onCanceled)
														.onNoCommitted(nop);
		try (final Postmark<Void, String> postmark = postmarkBuilder.build()) {
			postmark.cancel(expectedWord);
		}
		assertEquals(expectedWord, record.get());

		verify(onCanceled, times(1)).accept(expectedWord);
		verify(nop, times(0)).accept(any());
	}

	@Test
	void shouldCallOnNoCommitted() throws Exception {
		@SuppressWarnings("unchecked")
		final Consumer<Void> onNoCommitted = (Consumer<Void>) mock(Consumer.class);
		doAnswer(answer -> null).when(onNoCommitted).accept(null);

		@SuppressWarnings("unchecked")
		final Consumer<Void> nop = (Consumer<Void>) Mockito.mock(Consumer.class);

		final Postmark.PostmarkBuilder<Void, Void> postmarkBuilder =
			new Postmark.PostmarkBuilder<Void, Void>().onCommitted(nop)
													  .onCanceled(nop)
													  .onNoCommitted(onNoCommitted);
		try (final Postmark<Void, Void> postmark = postmarkBuilder.build()) {
			// nothing to do
		}

		verify(onNoCommitted, times(1)).accept(null);
		verify(nop, times(0)).accept(any());
	}

	@Test
	void shouldCancelationPriorityIsHigherThanCommitting() throws Exception {
		final AtomicReference<String> record = new AtomicReference<>();
		final String expectedWord = "hello";

		@SuppressWarnings("unchecked")
		final Consumer<String> onCanceled = (Consumer<String>) mock(Consumer.class);
		doAnswer(answer -> {
			record.set(answer.getArgument(0));
			return null;
		}).when(onCanceled).accept(expectedWord);

		@SuppressWarnings("unchecked")
		final Consumer<Void> nop = (Consumer<Void>) Mockito.mock(Consumer.class);

		final Postmark.PostmarkBuilder<Void, String> postmarkBuilder =
			new Postmark.PostmarkBuilder<Void, String>().onCommitted(nop)
														.onCanceled(onCanceled)
														.onNoCommitted(nop);
		try (final Postmark<Void, String> postmark = postmarkBuilder.build()) {
			postmark.commit(null);
			postmark.cancel(expectedWord);
			postmark.commit(null);
		}
		assertEquals(expectedWord, record.get());

		verify(onCanceled, times(1)).accept(expectedWord);
		verify(nop, times(0)).accept(any());
	}
}
