postmark
=============

Postmark runs the specified procedure according to the status that is in a `try-with-resource` clause.

Synopsis
---

```java
final Consumer<String> onCommitted = (str) -> {
    // do something
};

final Postmark.PostmarkBuilder<String, Void> postmarkBuilder =
    new Postmark.PostmarkBuilder<String, Void>().onCommitted(onCommitted)
                                                .onCanceled(null)
                                                .onNoCommitted(null);
try (final Postmark<String, Void> postmark = postmarkBuilder.build()) {
    postmark.commit("foo");
} // => it calls `onCommitted` consumer automatically
```

```java
final Consumer<String> onCanceled = (str) -> {
    // do something
};

final Postmark.PostmarkBuilder<String, Void> postmarkBuilder =
    new Postmark.PostmarkBuilder<String, Void>().onCommitted(null)
                                                .onCanceled(onCanceled)
                                                .onNoCommitted(null);
try (final Postmark<String, Void> postmark = postmarkBuilder.build()) {
    postmark.canceled("foo");
} // => it calls `onCanceled` consumer automatically
```

```java
final Consumer<Void> onNoCommitted = () -> {
    // do something
};

final Postmark.PostmarkBuilder<String, Void> postmarkBuilder =
    new Postmark.PostmarkBuilder<String, Void>().onCommitted(null)
                                                .onCanceled(null)
                                                .onNoCommitted(onNoCommitted);
try (final Postmark<String, Void> postmark = postmarkBuilder.build()) {
    // do nothing...
} // => it calls `onNoCommitted` consumer automatically
```

Description
--

Postmark runs the specified procedure according to the status that is in a `try-with-resource` clause.

Once the instance method of `commit()` has been called, it will call the registered `onCommitted` consumer on escaping from than `try-with-resource` clause. And `cancel()` behaves in the same way, it calls registered `onCanceled` consumer.
If either of `commit()` and `canceled()` were not called, it calls registered `onNoCommitted` consumer.

When a consumer wasn't given, it treats that as `null` and ignores to call that.

### NOTE

priority of `cancel()` is higher than `commit()`'s one. It means, if `cancel()` was called, the instance always invokes `onCanceled` consumer even if `commit()` was called.

Author
--

moznion (<moznion@gmail.com>)

License
--

```
The MIT License (MIT)
Copyright © 2019 moznion, http://moznion.net/ <moznion@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

