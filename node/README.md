# Node.js JavaScript Execution Server

This project provides a Node.js-based REST API that executes arbitrary JavaScript code in a sandboxed environment. The server leverages Node's `vm` module to run user-supplied code securely and maintains a per-chat context using a key-value store (kvStore). Each execution request can include a `chatId` to ensure state is maintained across calls for that conversation.

## Features

- **Sandboxed JavaScript Execution:** Executes arbitrary JavaScript code securely using Node's `vm` module.
- **Stateful Context:** Maintains a dedicated kvStore per `chatId` for sharing data between multiple executions.
- **Built-In Libraries:** The sandbox includes standard objects (`Math`, `Date`, `console`) and Node.js modules (`http`, `https`, timer functions).
- **Enhanced Logging:** Detailed logging for incoming requests, execution results, and error debugging.

## Setup and Installation

### Prerequisites

- [Node.js](https://nodejs.org/) v14 or later

### Steps

#### 1. Start the Server
   
Run the following command to start the server:

```
node server.js
```

You should see the output:

```
Server running at http://localhost:3000
```

#### 2. Test the Endpoint

You can test the API using a tool like Postman or via the command line with curl.

Example using curl:
```
curl -X POST http://localhost:3000/execute \
-H "Content-Type: application/json" \
-d '{"chatId": "test1", "code": "(async () => { kvStore.test = \"Hello, world!\"; return kvStore.test; })()"}'
```

The server will execute the provided JavaScript code and return a JSON response with the result and the chatId.

#### 3. Debugging

The server logs detailed information for each execution request:

* **Incoming Request**: Displays the chatId and received code.
* **Initial and Updated kvStore**: Shows the state before and after execution.
* **Execution Results**: Logged both before and after any Promise resolution.
* **Errors**: Any errors, including stack traces, are logged for easier debugging.

Use these logs in your terminal to help diagnose any issues.