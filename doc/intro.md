# Introduction to hive

## Overview

Hive contains 3 main modules:
- Tracer
    - Handles connection to the services receving events and hadling then

- API
    - HTTP Interface to serve data gathered by the Tracer module

- Storage
    - Stores and manages the events and data collected by the tracer module

## Message Schemas

Messages sent to Hive by the clients are multipart with the following pattern:

```
[IDENTITY, META, PAYLOAD]
```
Where:

```
IDENTITY = String
META = {
    event-type: ENUM{ new-event, heartbeat, register, close }
    name: String
}
PAYLOAD = {
    [String]: String
}
```

This is converted to a message-map inside Hive:

```
{
    identity: IDENTITY,
    meta: META,
    payload: PAYLOAD
}
```