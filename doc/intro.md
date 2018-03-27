# Introduction to hive

## Overview

Hive contains 3 main modules:
- Tracer
    - Handles connection to the services receving events and hadling then
- API
    - HTTP GraphQL Interface to serve data gathered by the Tracer module
- Storage
    - Stores and manages the events and data collected by the tracer module

## Message Schemas

Under the hood Hive uses a custom protocol uppon TCP connection together with ZeroMQ sockets.
Messages sent to Hive by the clients are multipart with the following pattern:

```
[IDENTITY, META, PAYLOAD]
```

Where:

```
IDENTITY = String
META = {
    type: ENUM{ new-event, heartbeat, register, close },
    service: String,
    produced-at: LocalDateTime,
    trace-id: String
}
PAYLOAD = {
    [String]: String
}
```

All information is transited in JSON, and is decoded in Hive right after being received to a message-map:

```
{
    identity: IDENTITY,
    meta: META,
    payload: PAYLOAD
}
```

- `Meta` follow the same schema accross all event types and is always present on messages.
- `Payload` contains specific data related to the event `type`, this schema can vary depending on the event being sent.

## Protocol Details

All communication is Push based from the clients to the Hive Server.

- `Hive` server uses a single `Router` socket to receive all events and messages from it's clients.
- `Clients` relies on `Dealer` sockets to connects to the `Hive` server and send events.

Given the nature of those sockets all communication is asynchronous and non-blocking. However the client is the one that always starts the dialog, with the serving sending responses back. Every message sent by the client needs a `ack` response before sending the next one, this ensures that the server has received the event correctly and is working as expected. While next event can't be sent while the ack is not received, those are enqueded in a non-blocking way to be sent when possible.

After receiving the event `Hive` rightly responds with an ack, it does not wait for the message processing be done (different from a tradicional request-response pattern, the ack is just a confirmation of the delivery, ensuring network connectivity and not logical correcteness).

On both sides just one socket is used, so while the interface is totally async and non-blocking, events are fair-queuened to be sent one at time avoid any concurrency and thread issue. In the client side, is possible to multiplex the number os sockets as long as you keep track of each one responses. In the server all work is done asynchronously and dispached to other threads to avoid blocking incoming messages.

It's very important to `Hive` know about the status of it's peers, different from a normal request-response, where the servers does not care about client going away, `Hive` must keep track of it's connections. In order to accomplish this, a `ping-pong` heartbeat mechanism is used, where clients must send heartbeat messages every X seconds to `Hive`. Those messages are treated in a different way, and are used to update a internal timestamp counter of each service. `Hive` checks those timestamps every N seconds to ensure all services are healthy and working, in the case of a client not sending heartbeats `Hive` will mark this one as `unresponsive`, and after as `dead`.

In the client side, like any other message type, heartbeats must await an ack from the `Hive` server, ensuring that clients are also aware of `Hive` presence. In the case of a response not being sent by the server (or received by the client), the hive client will start a retry strategy trying to reconnect to the `Hive` server on each attempt. If this does not succeed after a number of times, we would throw sayind that `Hive` unavaiable.



