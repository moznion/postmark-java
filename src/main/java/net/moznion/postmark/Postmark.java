package net.moznion.postmark;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Postmark runs the specified procedure according to the status that is in a {@code try-with-resource} clause.
 * <p>
 * Once the instance method of {@code commit()} has been called, it will call the registered {@code onCommitted} consumer
 * on escaping from than {@code try-with-resource} clause. And {@code cancel()} behaves in the same way, it calls registered
 * {@code onCanceled} consumer.
 * If either of {@code commit()} and {@code canceled()} were not called, it calls registered {@code onNoCommitted} consumer.
 * <p>
 * When a consumer wasn't given, it treats that as {@code null} and ignores to call that.
 * <p>
 * NOTE: priority of {@code cancel()} is higher than {@code commit()}'s one. It means, if {@code cancel()} was called,
 * the instance always invokes {@code onCanceled} consumer even if {@code commit()} was called.
 *
 * @param <T> type parameter for {@code onCommitted} consumer's parameter
 * @param <U> type parameter for {@code onCanceled} consumer's parameter
 */

public class Postmark<T, U> implements Closeable {
	final private Consumer<T> onCommitted;
	final private Consumer<U> onCanceled;
	final private Consumer<Void> onNoCommitted;

	private boolean isCommitted;
	private T commitValue;

	private boolean isCanceled;
	private U cancelValue;

	/**
	 * A constructor of {@link Postmark}.
	 * <p>
	 * Also it provides {@link PostmarkBuilder} to construct an instance easily.
	 *
	 * @param onCommitted a consumer that runs on committed
	 * @param onCanceled a consumer that runs on canceled
	 * @param onNoCommitted a consumer that runs when either of {@code commit()} and {@code cancel()} were not called
	 */
	public Postmark(final Consumer<T> onCommitted,
					final Consumer<U> onCanceled,
					final Consumer<Void> onNoCommitted
	) {
		this.onCommitted = onCommitted;
		this.onCanceled = onCanceled;
		this.onNoCommitted = onNoCommitted;
	}

	/**
	 * Mark as committed.
	 *
	 * @param value to give to {@code onCommitted} consumer as a parameter
	 */
	public void commit(final T value) {
		this.isCommitted = true;
		this.commitValue = value;
	}

	/**
	 * Mark as canceled.
	 *
	 * @param value to give to {@code onCanceled} consumer as a parameter
	 */
	public void cancel(final U value) {
		this.isCanceled = true;
		this.cancelValue = value;
	}

	@Override
	public void close() throws IOException {
		if (isCanceled) {
			if (onCanceled != null) {
				onCanceled.accept(cancelValue);
			}
			return;
		}

		if (isCommitted) {
			if (onCommitted != null) {
				onCommitted.accept(commitValue);
			}
			return;
		}

		if (onNoCommitted != null) {
			onNoCommitted.accept(null);
		}
	}

	/**
	 * PostmarkBuilder is a builder of {@link Postmark}.
	 *
	 * @param <T> type parameter for {@code onCommitted} consumer's parameter
	 * @param <U> type parameter for {@code onCanceled} consumer's parameter
	 */
	public static class PostmarkBuilder<T, U> {
		private Consumer<T> onCommitted;
		private Consumer<U> onCanceled;
		private Consumer<Void> onNoCommitted;

		/**
		 * A constructor of {@link PostmarkBuilder}.
		 */
		public PostmarkBuilder() {
		}

		/**
		 * Register a consumer for committed.
		 *
		 * @param onCommitted a consumer for committed
		 * @return itself of builder
		 */
		public PostmarkBuilder<T, U> onCommitted(final Consumer<T> onCommitted) {
			this.onCommitted = onCommitted;
			return this;
		}

		/**
		 * Register a consumer for canceled.
		 *
		 * @param onCanceled a consumer for canceled
		 * @return itself of builder
		 */
		public PostmarkBuilder<T, U> onCanceled(final Consumer<U> onCanceled) {
			this.onCanceled = onCanceled;
			return this;
		}

		/**
		 * Register a consumer for no committed and no canceled.
		 *
		 * @param onNoCommitted a consumer for no committed and no canceled
		 * @return itself of builder
		 */
		public PostmarkBuilder<T, U> onNoCommitted(final Consumer<Void> onNoCommitted) {
			this.onNoCommitted = onNoCommitted;
			return this;
		}

		/**
		 * Register a consumer for committed.
		 *
		 * @return a {@link Postmark} instance
		 */
		public Postmark<T, U> build() {
			return new Postmark<>(onCommitted, onCanceled, onNoCommitted);
		}
	}
}
